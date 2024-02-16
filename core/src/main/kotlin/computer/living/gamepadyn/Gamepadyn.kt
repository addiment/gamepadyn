package computer.living.gamepadyn

import computer.living.gamepadyn.InputType.*
import java.util.function.Consumer
import kotlin.enums.EnumEntries
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

    internal var eventsDigital: Map<TD,   Event<InputDataDigital>> = actionsDigital.associateWith { Event() }
    internal var eventsAnalog1: Map<TA,   Event<InputDataAnalog1>> = actionsAnalog1.associateWith { Event() }
    internal var eventsAnalog2: Map<TAA,  Event<InputDataAnalog2>> = actionsAnalog2.associateWith { Event() }

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
                            player.getEvent(cast).trigger(currentState)
                        }
                    }
                    is ActionEnumAnalog1 -> {
                        @Suppress("UNCHECKED_CAST")
                        val cast = (update as? TA) ?: continue
                        val currentState = player.getState(cast)
                        if (statePreviousAnalog1[update] != currentState) {
                            player.getEvent(cast).trigger(currentState)
                        }
                    }
                    is ActionEnumAnalog2 -> {
                        @Suppress("UNCHECKED_CAST")
                        val cast = (update as? TAA) ?: continue
                        val currentState = player.getState(cast)
                        if (statePreviousAnalog2[update] != currentState) {
                            player.getEvent(cast).trigger(currentState)
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
    fun getEvent(action: TD): Event<InputDataDigital>   = eventsDigital[action]
        .let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }
    /**
     * Gets an Analog1 Event
     */
    @JvmName("getEventAnalog1")
    fun getEvent(action: TA): Event<InputDataAnalog1>   = eventsAnalog1[action]
        .let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }
    /**
     * Gets an Analog2 Event
     */
    @JvmName("getEventAnalog2")
    fun getEvent(action: TAA): Event<InputDataAnalog2>  = eventsAnalog2[action]
        .let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }

    /**
     * Adds a listener to the event
     */
    @JvmName("addEventListenerDigital")
    fun addEventListener(action: TD, listener: (InputDataDigital) -> Unit): Event<InputDataDigital>     = eventsDigital[action]
        .let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }
        .also { it.addListener(listener) }

    /**
     * Gets an Analog1 Event and also adds a listener
     */
    @JvmName("addEventListenerAnalog1")
    fun addEventListener(action: TA, listener: (InputDataAnalog1) -> Unit): Event<InputDataAnalog1>     = eventsAnalog1[action]
        .let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }
        .also { it.addListener(listener) }
    /**
     * Gets an Analog2 Event and also adds a listener
     */
    @JvmName("addEventListenerAnalog2")
    fun addEventListener(action: TAA, listener: (InputDataAnalog2) -> Unit): Event<InputDataAnalog2>    = eventsAnalog2[action]
        .let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }
        .also { it.addListener(listener) }

    /**
     * Gets a Digital Event and also adds a Java listener
     */
    @JvmName("addEventListenerDigital")
    fun addEventListener(action: TD, listener: Consumer<InputDataDigital>): Event<InputDataDigital>     = eventsDigital[action]
        .let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }
        .also { it.addListener(listener) }

    /**
     * Gets an Analog1 Event and also adds a Java event listener
     */
    @JvmName("addEventListenerAnalog1")
    fun addEventListener(action: TA, listener: Consumer<InputDataAnalog1>): Event<InputDataAnalog1>     = eventsAnalog1[action]
        .let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }
        .also { it.addListener(listener) }
    /**
     * Gets an Analog2 Event and also adds a Java event listener
     */
    @JvmName("addEventListenerAnalog2")
    fun addEventListener(action: TAA, listener: Consumer<InputDataAnalog2>): Event<InputDataAnalog2>    = eventsAnalog2[action]
        .let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }
        .also { it.addListener(listener) }

    //        get() = ArrayList(inputSystem.getGamepads().map { Player(this, it) })

    // for calculating delta time
    // TODO: implement timing
    internal var lastUpdateTime: Double = 0.0

    companion object {
        /**
         * Kotlin-specific (sorta) factory method to create a new Gamepadyn instance.
         * @param backend See [Gamepadyn.backend]
         * @param strict See [Gamepadyn.strict]
         * @param digitalEnum The Kotlin class of your enum that implements of [ActionEnumDigital]
         * @param analog1Enum The Kotlin class of your enum that implements of [ActionEnumAnalog1]
         * @param analog2Enum The Kotlin class of your enum that implements of [ActionEnumAnalog2]
         */
        @JvmSynthetic
        fun <TD, TA, TAA> new(
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
            val d: Array<TD> = digitalEnum.java.enumConstants
            val a: Array<TA> = analog1Enum.java.enumConstants
            val aa: Array<TAA> = analog2Enum.java.enumConstants
            return Gamepadyn(backend, strict, d, a, aa)
        }

        /**
         * Java-specific (sorta) factory method to create a new Gamepadyn instance.
         * @param digitalEnum The Java class of your enum that implements of [ActionEnumDigital]
         * @param analog1Enum The Java class of your enum that implements of [ActionEnumAnalog1]
         * @param analog2Enum The Java class of your enum that implements of [ActionEnumAnalog2]
         * @param backend See [Gamepadyn.backend]
         * @param strict See [Gamepadyn.strict]
         */
        @JvmStatic
        @JvmOverloads
        fun <TD, TA, TAA> new(
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
            val d: Array<TD> = digitalEnum.enumConstants
            val a: Array<TA> = analog1Enum.enumConstants
            val aa: Array<TAA> = analog2Enum.enumConstants
            return Gamepadyn(backend, strict, d, a, aa)
        }
    }

}