package computer.living.gamepadyn

/**
 * Thunk Action
 */
class Tak<T : Enum<T>> private constructor(val action: T, val type: InputType, val axis: Int) {
    companion object {
        /**
         * Creates an analog Tak
         */
        @JvmStatic fun <T : Enum<T>> a(action: T, axes: Int): Tak<T> {
            assert(axes > 0)
            return Tak(action, InputType.ANALOG, axes)
        }

        /**
         * Creates a digital Tak
         */
        @JvmStatic fun <T : Enum<T>> d(action: T): Tak<T> {
            return Tak(action, InputType.DIGITAL, 0)
        }
        
        @JvmStatic fun <T : Enum<T>> makeActionMap(items: List<Tak<T>>): Map<T, InputDescriptor> {
            return items.associate { it.action to InputDescriptor(it.type, it.axis) }
        }
    }
}