package computer.living.gamepadyn

import computer.living.gamepadyn.InputType.ANALOG
import computer.living.gamepadyn.InputType.DIGITAL

/**
 * A Gamepadyn instance.
 *
 * @param backend The backend input source.
 * @param actions A map of actions that this Gamepadyn instance should be aware of
 */
@Suppress("MemberVisibilityCanBePrivate")
class Gamepadyn<T : Enum<T>> @JvmOverloads constructor(
    internal val backend: InputBackend,
    /**
     * If enabled, failures will be loud and catastrophic. Usually, that's better than "silent but deadly."
     */
    var strict: Boolean = true,
    internal var actions: Map<T, InputDescriptor?>
) {

    /**
     * Creates a Gamepadyn instance.
     * @param backend The backend input source.
     * @param strict If enabled, failures will be loud and catastrophic. Whether that's better than "silent but deadly" is up to you.
     * @param actions A map of actions that this Gamepadyn instance should be aware of
     */
    @SafeVarargs
    @JvmOverloads
    constructor(
        backend: InputBackend,
        strict: Boolean = true,
        vararg actions: Pair<T, InputDescriptor?>
    ) : this(backend, strict, mapOf(*actions)) {
        this.strict = strict
    }

    /**
     * A list of active Players.
     *
     * TODO: This can never update, needs to be fixed.
     */
    val players: ArrayList<Player<T>> =
        ArrayList(backend.getGamepads().map { Player(this, it) })

    /**
     * Convenience function for Java
     */
    fun getPlayer(index: Int): Player<T>? = players.getOrNull(index)

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
        for ((_, descriptor) in actions.entries) when (descriptor!!.type) {
            ANALOG -> require(descriptor.axes > 0) { "A digital input descriptor must have 0 axes!" }
            DIGITAL -> require(descriptor.axes == 0) { "An analog input descriptor must have 1 or more axes!" }
        }
    }

}