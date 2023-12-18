package computer.living.gamepadyn

/**
 * Many things are easy in Kotlin and painful in Java.
 * This code is meant to ease that pain.
 */
object JavaThunks {
    object Tak {
        /**
         * Creates an analog action/descriptor pair.
         */
        @JvmStatic fun <T : Enum<T>> analog(action: T, axes: Int): Pair<T, InputDescriptor> {
            return Pair(action, InputDescriptor(InputType.ANALOG, axes))
        }

        /**
         * Creates a digital action/descriptor pair.
         */
        @JvmStatic fun <T : Enum<T>> digital(action: T): Pair<T, InputDescriptor> {
            return Pair(action, InputDescriptor(InputType.DIGITAL, 0))
        }

        /**
         * Because map literals didn't exist before Java 9, this exists.
         * And don't say "double brace initialization."
         */
        @SafeVarargs
        @JvmStatic
        fun <T : Enum<T>> createActionMap(vararg items: Pair<T, InputDescriptor>): Map<T, InputDescriptor> {
//            return items.associate { it.first to it.second }
            return mapOf(*items)
        }
    }
}