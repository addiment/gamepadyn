package computer.living.gamepadyn

import computer.living.gamepadyn.InputType.*

/**
 * Superclass represents the state of any input.
 * @see InputDataDigital
 * @see InputDataAnalog1
 * @see InputDataAnalog2
 */
sealed class InputData { abstract val type: InputType }

/**
 * Boolean input (i.e. a button or a D-pad)
 */
data class InputDataDigital(
    /**
     * The boolean state of the input.
     */
    @JvmField var active: Boolean = false
): InputData() {
    override val type = DIGITAL

    // TODO: remove this, this is misusing operator functions
    operator fun invoke(): Boolean = active
}

/**
 * One-dimensional floating-point input (i.e. an analog trigger).
 */
data class InputDataAnalog1(
    /**
     * The X axis of the analog input data.
     */
    @JvmField var x: Float = 0f
) : InputData() {
    override val type: InputType = ANALOG1

    // TODO: remove this, this is misusing operator functions
    operator fun invoke(): Float = x
}

/**
 * Two-dimensional floating-point input (i.e. a gamepad thumbstick).
 */
data class InputDataAnalog2(
    /**
     * The X axis of the analog input data.
     */
    @JvmField var x: Float,

    /**
     * The Y axis of the analog input data.
     */
    @JvmField var y: Float
) : InputData() {
    constructor() : this(0f, 0f)
    override val type: InputType = ANALOG2
}