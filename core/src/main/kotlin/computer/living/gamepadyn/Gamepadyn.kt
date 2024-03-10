package computer.living.gamepadyn

import computer.living.gamepadyn.InputType.*
import kotlin.reflect.KClass


/**
 * A Gamepadyn instance. Use the factory method instead of a constructor.
 * @see [Gamepadyn.create]
 */
//@Suppress("MemberVisibilityCanBePrivate"/*, "BOUNDS_NOT_ALLOWED_IF_BOUNDED_BY_TYPE_PARAMETER"*/)
class Gamepadyn<TD, TA, TAA> private constructor(
    /**
     * The backend input source.
     */
    @JvmSynthetic
    internal val backend: InputBackend,
    /**
     * If enabled, failures will be loud and catastrophic. Usually, that's better than "silent but deadly."
     */
    var strict: Boolean = true,
    @JvmSynthetic
    internal val actionsDigital: Array<TD>,
    @JvmSynthetic
    internal val actionsAnalog1: Array<TA>,
    @JvmSynthetic
    internal val actionsAnalog2: Array<TAA>
)
        where TD : ActionEnumDigital,
              TA : ActionEnumAnalog1,
              TAA : ActionEnumAnalog2,
              TD : Enum<TD>,
              TA : Enum<TA>,
              TAA : Enum<TAA>
{
    /**
     * A list of active Players.
     */
    private var players: ArrayList<Player<TD, TA, TAA>> = ArrayList(
        backend.getGamepads().map { Player(this, it) }
    )

    /**
     * Returns a reference to the player at the specified index (starting from 0), or `null` if they don't exist.
     */
    fun getPlayer(index: Int): Player<TD, TA, TAA>? = players.getOrNull(index)

    @JvmSynthetic
    internal var globalEventsDigital: Map<TD,   Event<InputDataDigital, TD, TA, TAA>> = actionsDigital.associateWith { Event() }
    @JvmSynthetic
    internal var globalEventsAnalog1: Map<TA,   Event<InputDataAnalog1, TD, TA, TAA>> = actionsAnalog1.associateWith { Event() }
    @JvmSynthetic
    internal var globalEventsAnalog2: Map<TAA,  Event<InputDataAnalog2, TD, TA, TAA>> = actionsAnalog2.associateWith { Event() }

    /**
     * Request new state from the [InputBackend].
     */
    private fun updateGamepads() {
        val rawGamepads = backend.getGamepads()
        // TODO: check that this can't cause any issues
        if (rawGamepads.size != players.size) {
            // less players than we had previously, shift downwards
            if (rawGamepads.size < players.size) {
                // update old player gamepads
                for ((i, e) in rawGamepads.withIndex()) players[i].rawGamepad = e

                val range = (rawGamepads.size - 1)..<players.size
                // TODO: decide if we should disable or delete old players
                for (i in range) players[i].isEnabled = false

            // more players than we had previously, shift upwards.
            } else {
                // update their raw gamepads, also make sure they're enabled (just in case)
                for ((i, e) in players.withIndex()) {
                    e.isEnabled = true
                    e.rawGamepad = rawGamepads[i]
                }
                val range = (players.size - 1)..<rawGamepads.size
                // create new players
                for (i in range) players.add(i, Player(this, rawGamepads[i]))
            }
        } else {
            // just update them
            for ((i, e) in rawGamepads.withIndex()) players[i].rawGamepad = e
        }
    }

    /**
     * Updates state. Should be run every "frame," "tick," "update," or whatever iteration function your program uses.
     */
    fun update() {
        if (backend.hasUpdated()) return
        backend.update()
        val delta = backend.getDelta()
        updateGamepads()

//        println("----------UPDATE----------")
        // update each player
        for (player in players) {

            val binds = player.configuration

            // freeze state
            val statePreviousDigital = player.stateDigital.entries.associate { it.key to it.value.copy() }
            val statePreviousAnalog1 = player.stateAnalog1.entries.associate { it.key to it.value.copy() }
            val statePreviousAnalog2 = player.stateAnalog2.entries.associate { it.key to it.value.copy() }

//            println("PREVIOUS STATE:")
//            for (e in statePrevious) {
//                println("${e.key} = ${e.value}")
//            }
            val rawState: Map<RawInput, InputData> = player.rawGamepad.getState()

            val potentialMutations: MutableSet<Enum<*>> = mutableSetOf()

            if (binds == null) continue
            for (bind in binds.digital) {
                val newData = bind.pipe.eval(
                    this,
                    rawState,
                    statePreviousDigital,
                    statePreviousAnalog1,
                    statePreviousAnalog2
                ) as InputDataDigital

                potentialMutations.add(bind.action)
                player.stateDigital[bind.action] = newData
            }

            for (bind in binds.analog1) {
                val newData = bind.pipe.eval(
                    this,
                    rawState,
                    statePreviousDigital,
                    statePreviousAnalog1,
                    statePreviousAnalog2
                ) as InputDataAnalog1

                potentialMutations.add(bind.action)
                player.stateAnalog1[bind.action] = newData
            }

            for (bind in binds.analog2) {
                val newData = bind.pipe.eval(
                    this,
                    rawState,
                    statePreviousDigital,
                    statePreviousAnalog1,
                    statePreviousAnalog2
                ) as InputDataAnalog2

                potentialMutations.add(bind.action)
                player.stateAnalog2[bind.action] = newData
            }

            for (update in potentialMutations) {
                when (update) {
                    is ActionEnumDigital -> {
                        @Suppress("UNCHECKED_CAST")
                        val cast = (update as? TD) ?: continue
                        val currentState = player.getState(cast)
                        if (statePreviousDigital[update] != currentState) {
                            player.getEvent(cast).trigger(currentState, player)
                            this.getEvent(cast).trigger(currentState, player)
                        }
                    }
                    is ActionEnumAnalog1 -> {
                        @Suppress("UNCHECKED_CAST")
                        val cast = (update as? TA) ?: continue
                        val currentState = player.getState(cast)
                        if (statePreviousAnalog1[update] != currentState) {
                            player.getEvent(cast).trigger(currentState, player)
                            this.getEvent(cast).trigger(currentState, player)
                        }
                    }
                    is ActionEnumAnalog2 -> {
                        @Suppress("UNCHECKED_CAST")
                        val cast = (update as? TAA) ?: continue
                        val currentState = player.getState(cast)
                        if (statePreviousAnalog2[update] != currentState) {
                            player.getEvent(cast).trigger(currentState, player)
                            this.getEvent(cast).trigger(currentState, player)
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets a Digital Event
     */
    @JvmName("getEventDigital")
    fun getEvent(action: TD): Event<InputDataDigital, TD, TA, TAA>   = globalEventsDigital[action]!!

    /**
     * Gets an Analog1 Event
     */
    @JvmName("getEventAnalog1")
    fun getEvent(action: TA): Event<InputDataAnalog1, TD, TA, TAA>   = globalEventsAnalog1[action]!!

    /**
     * Gets an Analog2 Event
     */
    @JvmName("getEventAnalog2")
    fun getEvent(action: TAA): Event<InputDataAnalog2, TD, TA, TAA>  = globalEventsAnalog2[action]!!

    /**
     * Adds an event listener.
     * @see [Event.addListener]
     */
    @JvmName("addListenerDigital")
    fun addListener(action: TD, listener: (Event.EventData<InputDataDigital, TD, TA, TAA>) -> Unit): Boolean = globalEventsDigital[action]!!.addListener(listener)

    /**
     * Adds an event listener.
     * @see [Event.addListener]
     */
    @JvmName("addListenerAnalog1")
    fun addListener(action: TA, listener: (Event.EventData<InputDataAnalog1, TD, TA, TAA>) -> Unit): Boolean = globalEventsAnalog1[action]!!.addListener(listener)

    /**
     * Adds an event listener.
     * @see [Event.addListener]
     */
    @JvmName("addListenerAnalog2")
    fun addListener(action: TAA, listener: (Event.EventData<InputDataAnalog2, TD, TA, TAA>) -> Unit): Boolean = globalEventsAnalog2[action]!!.addListener(listener)

    /**
     * Adds an event listener.
     * @see [Event.addListener]
     */
    @JvmName("addListenerDigital")
    fun addListener(action: TD, listener: InputEventListener<InputDataDigital, TD, TA, TAA>): Boolean = globalEventsDigital[action]!!.addListener(listener)

    /**
     * Adds an event listener.
     * @see [Event.addListener]
     */
    @JvmName("addListenerAnalog1")
    fun addListener(action: TA, listener: InputEventListener<InputDataAnalog1, TD, TA, TAA>): Boolean = globalEventsAnalog1[action]!!.addListener(listener)

    /**
     * Adds an event listener.
     * @see [Event.addListener]
     */
    @JvmName("addListenerAnalog2")
    fun addListener(action: TAA, listener: InputEventListener<InputDataAnalog2, TD, TA, TAA>): Boolean = globalEventsAnalog2[action]!!.addListener(listener)

    // for calculating delta time
    // TODO: implement timing
    @JvmSynthetic
    internal var lastUpdateTime: Double = 0.0

    companion object {

        @JvmStatic val GAMEPADYN_VERSION = "0.3.0"

        /**
         * Kotlin-specific (sorta) factory method to create a new Gamepadyn instance.
         * @param backend See [Gamepadyn.backend]
         * @param strict See [Gamepadyn.strict]
         * @param digitalEnum The Kotlin class of your enum that implements [ActionEnumDigital]
         * @param analog1Enum The Kotlin class of your enum that implements [ActionEnumAnalog1]
         * @param analog2Enum The Kotlin class of your enum that implements [ActionEnumAnalog2]
         */
        @JvmSynthetic
        fun <TD, TA, TAA> create(
            digitalEnum: KClass<TD>,
            analog1Enum: KClass<TA>,
            analog2Enum: KClass<TAA>,
            backend: InputBackend,
            strict: Boolean = true
        ): Gamepadyn<TD, TA, TAA>
            where TD : ActionEnumDigital,
                  TA : ActionEnumAnalog1,
                  TAA : ActionEnumAnalog2,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA>
        {
            val d: Array<TD>? = digitalEnum.java.enumConstants
            val a: Array<TA>? = analog1Enum.java.enumConstants
            val aa: Array<TAA>? = analog2Enum.java.enumConstants
            if (d == null || a == null || aa == null) {
                val s = mutableSetOf<String>()
                if (d == null) s.add("digital")
                if (a == null) s.add("analog1")
                if (aa == null) s.add("analog2")
                throw Exception("Unable to find enum constants of the following action types: ${s.joinToString()}")
            }
            return Gamepadyn(backend, strict, d, a, aa)
        }

        /**
         * Java-specific (sorta) factory method to create a new Gamepadyn instance.
         * @param digitalEnum The Java class of your enum that implements [ActionEnumDigital]
         * @param analog1Enum The Java class of your enum that implements [ActionEnumAnalog1]
         * @param analog2Enum The Java class of your enum that implements [ActionEnumAnalog2]
         * @param backend See [Gamepadyn.backend]
         * @param strict See [Gamepadyn.strict]
         */
        @JvmStatic
        @JvmOverloads
        fun <TD, TA, TAA> create(
            digitalEnum: Class<TD>,
            analog1Enum: Class<TA>,
            analog2Enum: Class<TAA>,
            backend: InputBackend,
            strict: Boolean = true
        ): Gamepadyn<TD, TA, TAA>
            where TD : ActionEnumDigital,
                  TA : ActionEnumAnalog1,
                  TAA : ActionEnumAnalog2,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA>
        {
            val d: Array<TD>? = digitalEnum.enumConstants
            val a: Array<TA>? = analog1Enum.enumConstants
            val aa: Array<TAA>? = analog2Enum.enumConstants
            if (d == null || a == null || aa == null) {
                val s = mutableSetOf<String>()
                if (d == null) s.add("digital")
                if (a == null) s.add("analog1")
                if (aa == null) s.add("analog2")
                throw Exception("Unable to find enum constants of the following action types: ${s.joinToString()}")
            }
            return Gamepadyn(backend, strict, d, a, aa)
        }
    }

}