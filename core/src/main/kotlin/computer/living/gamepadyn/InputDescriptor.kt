package computer.living.gamepadyn

typealias GDesc = InputDescriptor

/**
 * Description of an input. This typename is long; it is recommended to use the alias if you are using Kotlin, `gamepadyn.GDesc`.
 */
class InputDescriptor(val type: InputType = InputType.DIGITAL, val axes: Int = 0) {

    companion object {
        /**
         * Syntactic sugar for the digital constructor.
         */
        @JvmStatic fun analog(axes: Int): InputDescriptor {
            return InputDescriptor(InputType.ANALOG, axes)
        }

        /**
         * Syntactic sugar for the digital constructor.
         */
        @JvmStatic fun digital(): InputDescriptor {
            return InputDescriptor(InputType.DIGITAL, 0)
        }
    }

    init {
        when (type) {
            InputType.DIGITAL -> assert(axes == 0) { "A digital input descriptor must have 0 axes!" }
            InputType.ANALOG -> assert(axes > 0) { "An analog input descriptor must have 1 or more axes!" }
        }
    }
}