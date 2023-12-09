package computer.living.gamepadyn

import computer.living.gamepadyn.InputType.ANALOG
import computer.living.gamepadyn.InputType.DIGITAL
import kotlin.collections.toMap

/**
 * Description of an input. This typename is long; it is recommended to use the alias if you are using Kotlin, `gamepadyn.GDesc`.
 */
class InputDescriptor {
    val type: InputType
    val axes: Int

    /**
     * An Analog descriptor with the amount of axes specified
     * @throws
     */
    constructor(axes: Int) {
        assert(axes > 0)
        this.type = ANALOG
        this.axes = axes
    }

    constructor(type: InputType = DIGITAL, axes: Int = 0) {
        when (type) {
            DIGITAL -> assert(axes == 0)
            ANALOG -> assert(axes > 0)
        }
        this.type = type
        this.axes = axes
    }
}

typealias GDesc = InputDescriptor

/**
 * A Gamepadyn instance.
 *
 * @param inputBackend The backend input source.
 * @param actions A map of actions that this Gamepadyn instance should be aware of
 */
@Suppress("MemberVisibilityCanBePrivate")
class Gamepadyn<T : Enum<T>> @JvmOverloads constructor(
    internal val inputBackend: InputBackend,
    /**
     * If enabled, failures will be loud and catastrophic. Usually, that's better than "silent but deadly."
     */
    var strict: Boolean = true,
//    @JvmField val useInputThread: Boolean = false,
    internal val actions: Map<T, InputDescriptor?>
) {


    /**
     * Creates a Gamepadyn instance.
     * @param inputBackend The backend input source.
     * @param strict If enabled, failures will be loud and catastrophic. Whether that's better than "silent but deadly" is up to you.
     * @param actions A map of actions that this Gamepadyn instance should be aware of
     */
    @JvmOverloads
    constructor(
        inputBackend: InputBackend,
        strict: Boolean = true,
//        useInputThread: Boolean = false,
        vararg actions: Pair<T, InputDescriptor?>
    ) : this(inputBackend, strict, /* useInputThread,*/ actions.toMap()) {
        this.strict = strict
    }

    // for calculating delta time
    // TODO: implement timing
    internal var lastUpdateTime: Double = 0.0

    /**
     * A list of active Players.
     *
     * TODO: This can never update, needs to be fixed.
     */
    val players: ArrayList<Player<T>> =
        ArrayList(inputBackend.getGamepads().map { Player(this, it) })
//        get() = ArrayList(inputSystem.getGamepads().map { Player(this, it) })

    /**
     * Convenience function for Java
     */
    fun getPlayer(index: Int): Player<T>? = players.getOrNull(index)

    /**
     * Updates state. Should be run every "frame," "tick," "update," or whatever iteration function your program uses.
     */
    fun update() {
        val playersIt = players.withIndex()
        // update each player
        for ((i, player) in playersIt) {
            // make the configuration local and constant
            val config = player.configuration

            // update state
            player.statePrevious = player.state.toMap()

            if (config != null) for (bind in config.binds) {
                val descriptor = actions[bind.targetAction]
                // loud / silent null check
                if (strict) descriptor!!; else if (descriptor == null) break

                val rawState: InputData = inputBackend.getGamepads()[i].getState(bind.input)

                val previousData = player.statePrevious[bind.targetAction]
                val newData: InputData = bind.transform(rawState, descriptor)

                if (newData.type != descriptor.type)
                    if (strict) throw Exception("Mismatched transformation result (expected ${descriptor.type.name.lowercase()}, got ${newData.type.name.lowercase()})")
                    else break

//                TODO: this block of code doesn't work and I'm too tired rn to figure out why.
//                      the intent is so that you can replace *only specific axes* of an action,
//                      allowing for more granular control.
//                if (newData is InputDataAnalog && descriptor.type == ANALOG) {
//                    if (newData.axes != descriptor.axes) {
//                        if (strict) throw Exception("Mismatched transformation result (expected ${descriptor.axes} axes, got ${newData.axes})")
//                        else break
//                    } else {
//                        for ((j, e) in newData.analogData.withIndex()) {
//                            if (e != null) {
//                                (player.state[bind.targetAction] as InputDataAnalog).analogData[j] = e
//                            }
//                        }
//                    }
//                }
                player.state[bind.targetAction] = newData


                if (newData is InputDataAnalog) {
                    println("previous = ${(previousData as InputDataAnalog).analogData.contentToString()}, current = ${newData.analogData.contentToString()}")
                } else {
//                    println("previous = ${(previousData as InputDataDigital).digitalData}, current = ${(newData as InputDataDigital).digitalData}")
                }

                // changes in state trigger events
                if (previousData != player.state[bind.targetAction] /* && previous != null */) when (descriptor.type) {
                    DIGITAL -> player.eventsDigital[bind.targetAction]?.trigger(player.state[bind.targetAction] as InputDataDigital)
                    ANALOG -> player.eventsAnalog[bind.targetAction]?.trigger(player.state[bind.targetAction] as InputDataAnalog)
                }
            }
        }
    }

    init {
//         multithread input (remove this line)
//        if (useInputThread) throw Exception("Gamepadyn has no multithreading implementation yet!")
        for ((_, descriptor) in actions.entries) when (descriptor!!.type) {
            ANALOG -> assert(descriptor.axes > 0)
            DIGITAL -> assert(descriptor.axes == 0)
        }
    }

}