package computer.living.gamepadyn

import computer.living.gamepadyn.InputType.*

/**
 * Superclass represents the state of any input.
 * @see InputDataDigital
 * @see InputDataAnalog1
 * @see InputDataAnalog2
 */
sealed class InputData {
    abstract val type: InputType
//    abstract operator fun invoke(): Any
}

/**
 * Represents the value of a digital action.
 * This is effectively a Boolean that implements ActionData.
 */
data class InputDataDigital(
    /**
     * The boolean state of the input.
     */
    @JvmField var active: Boolean = false
): InputData() {
    override val type = DIGITAL

    operator fun invoke(): Boolean = active
}

data class InputDataAnalog1(
    /**
     * The X axis of the analog input data.
     */
    @JvmField var x: Float = 0f
) : InputData() {
    override val type: InputType = ANALOG1

    operator fun invoke(): Float = x
}

data class InputDataAnalog2(
    /**
     * The X axis of the analog input data.
     */
    @JvmField var x: Float = 0f,
    /**
     * The Y axis of the analog input data.
     */
    @JvmField var y: Float = 0f
) : InputData() {
    override val type: InputType = ANALOG2
}