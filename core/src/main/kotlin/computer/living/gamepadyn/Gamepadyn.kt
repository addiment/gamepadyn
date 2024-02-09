package computer.living.gamepadyn

import computer.living.gamepadyn.InputType.*
import kotlin.enums.EnumEntries

sealed interface ActionEnum
interface ActionEnumDigital : ActionEnum
interface ActionEnumAnalog1 : ActionEnum
interface ActionEnumAnalog2 : ActionEnum

/**
 * @constructor The entries of the enum that you use for actions (i.e. ActionDigital.entries, ActionAnalog1.entries, ActionAnalog2.entries).
 */
data class ActionMap<TD, TA, TAA>(
    var digitalActions: Set<TD>,
    var analog1Actions: Set<TA>,
    var analog2Actions: Set<TAA>
)
        where TD : ActionEnumDigital,
              TA : ActionEnumAnalog1,
              TAA : ActionEnumAnalog2,
              TD : Enum<TD>,
              TA : Enum<TA>,
              TAA : Enum<TAA>
{
    /**
     * Creates the ActionMap with the entries of the enum that you use for actions (i.e. ActionDigital.entries, ActionAnalog1.entries, ActionAnalog2.entries).
     */
    constructor(
        digitalActions: Array<TD>,
        analog1Actions: Array<TA>,
        analog2Actions: Array<TAA>
    ) : this (digitalActions.toSet(), analog1Actions.toSet(), analog2Actions.toSet())

    /**
     * Creates the ActionMap with the entries of the enum that you use for actions (i.e. ActionDigital.entries, ActionAnalog1.entries, ActionAnalog2.entries).
     */
    constructor(
        digitalActions: EnumEntries<TD>,
        analog1Actions: EnumEntries<TA>,
        analog2Actions: EnumEntries<TAA>
    ) : this (digitalActions.toSet(), analog1Actions.toSet(), analog2Actions.toSet())

    val size: Int
        get() = digitalActions.size + analog1Actions.size + analog2Actions.size

    init {
        require(digitalActions.isNotEmpty() || analog1Actions.isNotEmpty() || analog2Actions.isNotEmpty()) { "Must provide at least 1 action!" }
    }
}

/**
 * A Gamepadyn instance.
 *
 * @param actions A map of actions that this Gamepadyn instance should be aware of
 */
@Suppress("MemberVisibilityCanBePrivate"/*, "BOUNDS_NOT_ALLOWED_IF_BOUNDED_BY_TYPE_PARAMETER"*/)
class Gamepadyn<TD, TA, TAA> @JvmOverloads constructor(
    /**
     * The backend input source.
     */
    internal val backend: InputBackend,
    /**
     * If enabled, failures will be loud and catastrophic. Usually, that's better than "silent but deadly."
     */
    var strict: Boolean = true,
    var actions: ActionMap<TD, TA, TAA>
)
        where
              TD : ActionEnumDigital,
              TA : ActionEnumAnalog1,
              TAA : ActionEnumAnalog2,
              TD : Enum<TD>,
              TA : Enum<TA>,
              TAA : Enum<TAA>
//              TAAA : ActionEnumAnalog3,
//              TD : Ti,
//              TA : Ti,
//              TAA : Ti,
//              TAAA : Ti,
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

    //        get() = ArrayList(inputSystem.getGamepads().map { Player(this, it) })

    // for calculating delta time
    // TODO: implement timing
    internal var lastUpdateTime: Double = 0.0

    init {
        actions = ActionMap(
            actions.digitalActions.toSet(),
            actions.analog1Actions.toSet(),
            actions.analog2Actions.toSet()
        )
        require(actions.digitalActions.isNotEmpty() || actions.analog1Actions.isNotEmpty() || actions.analog2Actions.isNotEmpty()) { "Must provide at least 1 action!" }
    }

}