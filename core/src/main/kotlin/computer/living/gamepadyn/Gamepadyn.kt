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
//              TAAA : ActionEnumAnalog3,
//              TD : Ti,
//              TA : Ti,
//              TAA : Ti,
//              TAAA : Ti,
              TD : Enum<TD>,
              TA : Enum<TA>,
              TAA : Enum<TAA>
{

    /**
     * A list of active Players.
     *
     * TODO: This can never update, needs to be fixed.
     */
    val players: ArrayList<Player<TD, TA, TAA>> =
        ArrayList(backend.getGamepads().map { Player(this, it) })

    /**
     * Convenience function for Java
     */
    fun getPlayer(index: Int): Player<TD, TA, TAA>? = players.getOrNull(index)

    /**
     * Updates state. Should be run every "frame," "tick," "update," or whatever iteration function your program uses.
     */
    fun update() {
//        println("----------UPDATE----------")
        val playersIt = players.withIndex()
        // update each player
        for ((i, player) in playersIt) {
            // make the configuration local and constant
            val config = player.configuration

            // freeze state
            val statePreviousDigital = player.stateDigital.entries.associate { it.key to it.value!!.copy() }
            val statePreviousAnalog1 = player.stateAnalog1.entries.associate { it.key to it.value!!.copy() }
            val statePreviousAnalog2 = player.stateAnalog2.entries.associate { it.key to it.value!!.copy() }

//            println("PREVIOUS STATE:")
//            for (e in statePrevious) {
//                println("${e.key} = ${e.value}")
//            }
            val rawState: Map<RawInput, InputData> = backend.getGamepads()[i].getState()

            val potentialMutations: MutableSet<Enum<*>> = mutableSetOf()

            if (config != null) bindLoop@ for (bind in config.binds) {
//                println("bind (${bind.input.name} to ${bind.targetAction.name}) {")

                val previousState = when (bind.targetAction) {
                    is ActionEnumDigital -> statePreviousDigital[bind.targetAction]
                    is ActionEnumAnalog1 -> statePreviousAnalog1[bind.targetAction]
                    is ActionEnumAnalog2 -> statePreviousAnalog2[bind.targetAction]
                    else -> if (strict) throw Exception("Gamepadyn (strict): target action \"${bind.targetAction.name}\" of bind ${bind.name} was invalid") else null
                } ?: continue

                val newData: InputData = bind.transform(
                    rawState[bind.input] ?: (when (bind.input.type) {
                        DIGITAL -> InputDataDigital()
                        ANALOG1 -> InputDataAnalog1()
                        ANALOG2 -> InputDataAnalog2()
                    }),
                    previousState
                )

                potentialMutations.add(bind.targetAction)

                when (bind.targetAction) {
                    is ActionEnumDigital -> {
                        @Suppress("UNCHECKED_CAST")
                        val cast: TD = (bind.targetAction as? TD)
                            ?: if (strict) throw Exception("Gamepadyn (strict): target action \"${bind.targetAction.name}\" of bind ${bind.name} was invalid (guessed type Digital?)") else continue@bindLoop
                        val inCast = newData as? InputDataDigital
                            ?: if (strict) throw Exception("Gamepadyn (strict): target action \"${bind.targetAction.name}\" of bind ${bind.name} is digital but was provided type ${newData.type}") else continue@bindLoop
                        player.stateDigital[cast] = inCast
                    }
                    is ActionEnumAnalog1 -> {
                        @Suppress("UNCHECKED_CAST")
                        val cast: TA = (bind.targetAction as? TA)
                            ?: if (strict) throw Exception("Gamepadyn (strict): target action \"${bind.targetAction.name}\" of bind ${bind.name} was invalid (guessed type Analog1?)") else continue@bindLoop
                        val inCast = newData as? InputDataAnalog1
                            ?: if (strict) throw Exception("Gamepadyn (strict): target action \"${bind.targetAction.name}\" of bind ${bind.name} is analog1 but was provided type ${newData.type}") else continue@bindLoop
                        player.stateAnalog1[cast] = inCast
                    }
                    is ActionEnumAnalog2 -> {
                        @Suppress("UNCHECKED_CAST")
                        val cast: TAA = (bind.targetAction as? TAA)
                            ?: if (strict) throw Exception("Gamepadyn (strict): target action \"${bind.targetAction.name}\" of bind ${bind.name} was invalid (guessed type Analog2?)") else continue@bindLoop
                        val inCast = newData as? InputDataAnalog2
                            ?: if (strict) throw Exception("Gamepadyn (strict): target action \"${bind.targetAction.name}\" of bind ${bind.name} is analog2 but was provided type ${newData.type}") else continue@bindLoop
                        player.stateAnalog2[cast] = inCast
                    }
                    else -> if (strict) throw Exception("Gamepadyn (strict): target action \"${bind.targetAction.name}\" of bind ${bind.name} was invalid") else continue@bindLoop
                }
            }


            for (update in potentialMutations) {
                when (update) {
                    is ActionEnumDigital -> {
                        @Suppress("UNCHECKED_CAST")
                        val cast = (update as? TD) ?: continue
                        val currentState = player.getState(cast)
                        if (statePreviousDigital[update] != currentState && currentState != null) {
                            player.getEvent(cast)?.trigger(currentState)
                        }
                    }
                    is ActionEnumAnalog1 -> {
                        @Suppress("UNCHECKED_CAST")
                        val cast = (update as? TA) ?: continue
                        val currentState = player.getState(cast)
                        if (statePreviousAnalog1[update] != currentState && currentState != null) {
                            player.getEvent(cast)?.trigger(currentState)
                        }
                    }
                    is ActionEnumAnalog2 -> {
                        @Suppress("UNCHECKED_CAST")
                        val cast = (update as? TAA) ?: continue
                        val currentState = player.getState(cast)
                        if (statePreviousAnalog2[update] != currentState && currentState != null) {
                            player.getEvent(cast)?.trigger(currentState)
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