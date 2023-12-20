package computer.living.gamepadyn

typealias GDesc = InputDescriptor

/**
 * Description of an input. This typename is long; it is recommended to use the alias if you are using Kotlin, `gamepadyn.GDesc`.
 */
class InputDescriptor private constructor(val type: InputType = InputType.DIGITAL, val axes: Int = 0) {

    companion object {
        /**
         * Factory method, constructs an analog descriptor.
         */
        @JvmStatic fun analog(axes: Int): InputDescriptor = InputDescriptor(InputType.ANALOG, axes)

        /**
         * Factory method, constructs a digital descriptor.
         */
        @JvmStatic fun digital(): InputDescriptor = InputDescriptor(InputType.DIGITAL, 0)
    }

    init {
        when (type) {
            InputType.DIGITAL -> require(axes == 0) { "A digital input descriptor must have 0 axes!" }
            InputType.ANALOG -> require(axes > 0) { "An analog input descriptor must have 1 or more axes!" }
        }
    }
}