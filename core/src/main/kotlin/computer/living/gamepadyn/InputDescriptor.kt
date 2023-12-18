package computer.living.gamepadyn

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
        this.type = InputType.ANALOG
        this.axes = axes
    }

    constructor(type: InputType = InputType.DIGITAL, axes: Int = 0) {
        when (type) {
            InputType.DIGITAL -> assert(axes == 0)
            InputType.ANALOG -> assert(axes > 0)
        }
        this.type = type
        this.axes = axes
    }
}