package computer.living.gamepadyn

import computer.living.gamepadyn.InputType.*
import kotlin.reflect.KClass

sealed interface ActionEnum
interface ActionEnumDigital : ActionEnum
interface ActionEnumAnalog1 : ActionEnum
interface ActionEnumAnalog2 : ActionEnum

/**
 * A Gamepadyn instance.
 */
@Suppress("MemberVisibilityCanBePrivate"/*, "BOUNDS_NOT_ALLOWED_IF_BOUNDED_BY_TYPE_PARAMETER"*/)
class Gamepadyn<TD, TA, TAA> private constructor(
    /**
     * The backend input source.
     */
    internal val backend: InputBackend,
    /**
     * If enabled, failures will be loud and catastrophic. Usually, that's better than "silent but deadly."
     */
    var strict: Boolean = true,
    internal val actionsDigital: Array<TD>,
    internal val actionsAnalog1: Array<TA>,
    internal val actionsAnalog2: Array<TAA>
)
        where
              TD : ActionEnumDigital,
              TA : ActionEnumAnalog1,
              TAA : ActionEnumAnalog2,
              TD : Enum<TD>,
              TA : Enum<TA>,
              TAA : Enum<TAA>
{
    /**
     * A list of active Players.
     */
    // Note for me/other developers:
    // I might make this private, because it would allow for more control over access.
    // It would also make getPlayer() the correct method across different languages (consistency = good)
    // and it would also mean less array OOB exceptions (yay!)

    var players: ArrayList<Player<TD, TA, TAA>> = ArrayList(
        backend.getGamepads().map { Player(this, it) }
    )
        private set

    /**
     * Convenience function (equivalent to [players]`.getOrNull(index)`).
     * This is mainly for Java, which can't use Kotlin getters with property access syntax.
     */
    fun getPlayer(index: Int): Player<TD, TA, TAA>? = players.getOrNull(index)

    internal var globalEventsDigital: Map<TD,   Event<InputDataDigital, TD, TA, TAA>> = actionsDigital.associateWith { Event() }
    internal var globalEventsAnalog1: Map<TA,   Event<InputDataAnalog1, TD, TA, TAA>> = actionsAnalog1.associateWith { Event() }
    internal var globalEventsAnalog2: Map<TAA,  Event<InputDataAnalog2, TD, TA, TAA>> = actionsAnalog2.associateWith { Event() }

    /**
     * Request new state from the [InputBackend].
     */
    private fun updateGamepads() {
        val rawGamepads = backend.getGamepads()
        // TODO: check that this can't cause any issues
        if (rawGamepads.size != players.size) {
            if (rawGamepads.size < players.size) {
                // update old player gamepads
                for ((i, e) in rawGamepads.withIndex()) players[i].rawGamepad = e

                val range = (rawGamepads.size - 1)..<players.size
                // TODO: decide if we should disable or delete old players
                for (i in range) players[i].isEnabled = false

            } else if (rawGamepads.size > players.size) {
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
            // make the configuration local and constant
            val config = player.configuration

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

            bindLoop@ for (bind in config.binds) {
//                println("bind (${bind.input.name} to ${bind.targetAction.name}) {")

                val previousState = when (bind.targetAction) {
                    is ActionEnumDigital -> statePreviousDigital[bind.targetAction]
                    is ActionEnumAnalog1 -> statePreviousAnalog1[bind.targetAction]
                    is ActionEnumAnalog2 -> statePreviousAnalog2[bind.targetAction]
                    else -> if (strict) throw Exception("Gamepadyn (strict): bind with target action \"${bind.targetAction.name}\" and raw input ${bind.input} was invalid") else null
                } ?: continue

                val newData: InputData = bind.transform(
                    rawState[bind.input] ?: (when (bind.input.type) {
                        DIGITAL -> InputDataDigital()
                        ANALOG1 -> InputDataAnalog1()
                        ANALOG2 -> InputDataAnalog2()
                    }),
                    previousState,
                    delta
                )

//                println(rawState[bind.input])
//                println("old data: $previousState")
//                println("new data: $newData")

                potentialMutations.add(bind.targetAction)

                when (bind.targetAction) {
                    is ActionEnumDigital -> {
                        @Suppress("UNCHECKED_CAST")
                        val cast: TD = (bind.targetAction as? TD)
                            ?: if (strict) throw Exception("Gamepadyn (strict): bind with target action \"${bind.targetAction.name}\" and raw input ${bind.input} was invalid (guessed type Digital?)") else continue@bindLoop
                        val inCast = newData as? InputDataDigital
                            ?: if (strict) throw Exception("Gamepadyn (strict): bind with target action \"${bind.targetAction.name}\" and raw input ${bind.input} is digital but was provided type ${newData.type}") else continue@bindLoop
                        player.stateDigital[cast] = inCast
                    }
                    is ActionEnumAnalog1 -> {
                        @Suppress("UNCHECKED_CAST")
                        val cast: TA = (bind.targetAction as? TA)
                            ?: if (strict) throw Exception("Gamepadyn (strict): bind with target action \"${bind.targetAction.name}\" and raw input ${bind.input} was invalid (guessed type Analog1?)") else continue@bindLoop
                        val inCast = newData as? InputDataAnalog1
                            ?: if (strict) throw Exception("Gamepadyn (strict): bind with target action \"${bind.targetAction.name}\" and raw input ${bind.input} is analog1 but was provided type ${newData.type}") else continue@bindLoop
                        player.stateAnalog1[cast] = inCast
                    }
                    is ActionEnumAnalog2 -> {
                        @Suppress("UNCHECKED_CAST")
                        val cast: TAA = (bind.targetAction as? TAA)
                            ?: if (strict) throw Exception("Gamepadyn (strict): bind with target action \"${bind.targetAction.name}\" and raw input ${bind.input} was invalid (guessed type Analog2?)") else continue@bindLoop
                        val inCast = newData as? InputDataAnalog2
                            ?: if (strict) throw Exception("Gamepadyn (strict): bind with target action \"${bind.targetAction.name}\" and raw input ${bind.input} is analog2 but was provided type ${newData.type}") else continue@bindLoop
                        player.stateAnalog2[cast] = inCast
                    }
                    else -> if (strict) throw Exception("Gamepadyn (strict): bind with target action \"${bind.targetAction.name}\" and raw input ${bind.input} was invalid") else continue@bindLoop
                }
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
    internal var lastUpdateTime: Double = 0.0

    companion object {
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
        @Throws()
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