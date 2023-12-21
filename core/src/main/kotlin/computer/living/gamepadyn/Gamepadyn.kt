package computer.living.gamepadyn

import computer.living.gamepadyn.InputType.*
import kotlin.reflect.KClass

sealed interface ActionEnum
interface ActionEnumDigital : ActionEnum
interface ActionEnumAnalog1 : ActionEnum
interface ActionEnumAnalog2 : ActionEnum
interface ActionEnumAnalog3 : ActionEnum

data class ActionMap<Td, Ta, Taa, Taaa>(
    var digitalActions: Set<Td> = setOf(),
    var analog1Actions: Set<Ta> = setOf(),
    var analog2Actions: Set<Taa> = setOf(),
    var analog3Actions: Set<Taaa> = setOf()
) where Td : ActionEnumDigital, Ta : ActionEnumAnalog1, Taa : ActionEnumAnalog2, Taaa : ActionEnumAnalog3, Td : Enum<Td>, Ta : Enum<Ta>, Taa : Enum<Taa>, Taaa : Enum<Taaa> {

    init {
        require(digitalActions.isNotEmpty() || analog1Actions.isNotEmpty() || analog2Actions.isNotEmpty() || analog3Actions.isNotEmpty()) { "Must provide at least 1 action!" }
    }
}

/**
 * A Gamepadyn instance.
 *
 * @param actions A map of actions that this Gamepadyn instance should be aware of
 */
@Suppress("MemberVisibilityCanBePrivate", "BOUNDS_NOT_ALLOWED_IF_BOUNDED_BY_TYPE_PARAMETER")
class Gamepadyn<TD, TA, TAA, TAAA> @JvmOverloads constructor(
    /**
     * The backend input source.
     */
    internal val backend: InputBackend,
    /**
     * If enabled, failures will be loud and catastrophic. Usually, that's better than "silent but deadly."
     */
    var strict: Boolean = true,
    var actions: ActionMap<TD, TA, TAA, TAAA>
    )
        where TD : ActionEnumDigital,
              TA : ActionEnumAnalog1,
              TAA : ActionEnumAnalog2,
              TAAA : ActionEnumAnalog3,
//              TD : Ti,
//              TA : Ti,
//              TAA : Ti,
//              TAAA : Ti,
              TD : Enum<TD>,
              TA : Enum<TA>,
              TAA : Enum<TAA>,
              TAAA : Enum<TAAA> {

    /**
     * A transformation from inputs to actions.
     *
     * I am not happy with this API as it is, and will be working on developing it further in the future.
     */
    /*
     * TODO: Rework the binding API to require less type casting/assumption in implementations.
     *      The constructor isn't great either, almost all work should be done in the transform function.
     *      Why isn't it a lambda? Because users may want to store persistent state *in the bind.*
     *      That might change though. It's not at the top of my priority list right now.
     */
    open class ActionBind<I, O>
            where I : InputData,
                  O : InputData,
//        where Td : computer.living.gamepadyn.ActionEnumDigital,
//              Ta : computer.living.gamepadyn.ActionEnumAnalog1,
//              Taa : computer.living.gamepadyn.ActionEnumAnalog2,
//              Taaa : computer.living.gamepadyn.ActionEnumAnalog3,
//              Td : kotlin.Enum<Td>,
//              Ta : kotlin.Enum<Ta>,
//              Taa : kotlin.Enum<Taa>,
//              Taaa : kotlin.Enum<Taaa>,
    {

        constructor(reifier: O) {

        }

        fun reify(i: I): KClass<*> = i.type.kClass
        fun reify(o: O): KClass<*> = o.type.kClass

        /**
         * Performs a transformation on the input data.
         */
        open fun transform(data: I): O? = when (data) {
            reify(data).
        }

    }

    /**
     * A list of active Players.
     *
     * TODO: This can never update, needs to be fixed.
     */
    val players: ArrayList<Player<TD, TA, TAA, TAAA>> =
        ArrayList(backend.getGamepads().map { Player(this, it) })

    /**
     * Convenience function for Java
     */
    fun getPlayer(index: Int): Player<TD, TA, TAA, TAAA>? = players.getOrNull(index)

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

            // update state
            val statePrevious = player.state.entries.associate {
                when (it.value.type) {
                    DIGITAL -> it.key to (it.value as InputDataDigital).copy()
                    ANALOG -> it.key to (it.value as InputDataAnalog).copy()
                }
            }

//            println("PREVIOUS STATE:")
//            for (e in statePrevious) {
//                println("${e.key} = ${e.value}")
//            }

            if (config != null) for (bind in config.binds) {
//                println("bind (${bind.input.name} to ${bind.targetAction.name}) {")
                val descriptor = actions[bind.targetAction]
                // loud / silent null check
                if (strict) descriptor!!; else if (descriptor == null) break

                val rawState: InputData = backend.getGamepads()[i].getState(bind.input)

                val previousData = statePrevious[bind.targetAction]
                val newData: InputData = bind.transform(rawState, descriptor) ?: continue

                if (newData.type != descriptor.type)
                    if (strict) throw Exception("Mismatched transformation result (expected ${descriptor.type.name.lowercase()}, got ${newData.type.name.lowercase()})")
                    else break

                if (newData is InputDataAnalog && descriptor.type == ANALOG) {
                    if (newData.axes != descriptor.axes) {
                        if (strict) throw Exception("Mismatched transformation result (expected ${descriptor.axes} axes, got ${newData.axes})")
                        else break
                    } else {
                        for ((j, e) in newData.analogData.withIndex()) {
                            if (e != null) {
//                                println("    axes $j state before mutation: ${statePrevious[bind.targetAction]}")
                                (player.state[bind.targetAction] as InputDataAnalog).analogData[j] = e
//                                println("    axes $j == $e")
//                                println("    axes $j state (still) before mutation: ${statePrevious[bind.targetAction]}")
                            }
                        // else {
//                                println("    axes $j == null")
//                            }
                        }
                    }
                } else player.state[bind.targetAction] = newData


//                if (newData is InputDataAnalog) {
//                    println("    previous = ${(previousData as InputDataAnalog).analogData.contentToString()}, current = ${newData.analogData.contentToString()}")
//                } else {
//                    println("    previous = ${(previousData as InputDataDigital).digitalData}, current = ${(newData as InputDataDigital).digitalData}")
//                }

                // changes in state trigger events
                if (previousData != player.state[bind.targetAction] /* && previous != null */) when (descriptor.type) {
                    DIGITAL -> player.eventsDigital[bind.targetAction]?.trigger(player.state[bind.targetAction] as InputDataDigital)
                    ANALOG -> player.eventsAnalog[bind.targetAction]?.trigger(player.state[bind.targetAction] as InputDataAnalog)
                }
//                println("}")
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
            actions.analog2Actions.toSet(),
            actions.analog3Actions.toSet()
        )
        require(actions.digitalActions.isNotEmpty() || actions.analog1Actions.isNotEmpty() || actions.analog2Actions.isNotEmpty() || actions.analog3Actions.isNotEmpty()) { "Must provide at least 1 action!" }
    }

}