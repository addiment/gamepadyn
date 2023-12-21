package computer.living.gamepadyn

import computer.living.gamepadyn.InputType.*

/**
 * Superclass represents the state of any input.
 * @see InputDataDigital
 * @see InputDataAnalog
 */
sealed class InputData {

    /**
     * The type of the InputData, corresponding to the [InputDataDigital] and [InputDataAnalog] classes respectively.
     */
    abstract val type: InputType

}

/**
 * Represents the value of a digital action.
 * This is effectively a Boolean that implements ActionData.
 */
data class InputDataDigital(
    /**
     * The boolean state of the input.
     */
    @JvmField var digitalData: Boolean = false
): InputData() {
    override val type = DIGITAL

    operator fun invoke(): Boolean = digitalData

    override fun equals(other: Any?): Boolean {
        if (other !is InputDataDigital) return false
        return this.digitalData == other.digitalData
    }

    override fun hashCode(): Int {
        var result = digitalData.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

}

/**
 * Represents the value of an analog action.
 */
sealed class InputDataAnalog : InputData() {
//    abstract override val type: InputType;

    /**
     * The amount of axes of the input.
     */
    abstract val axes: Int

    /**
     * The X axis of the analog input data.
     */
    open var x: Float? = 0f

    open operator fun get(axis: Int): Float? = if (axis == 0) x else null
    open operator fun invoke(): Array<Float?> = arrayOf(x)
}

data class InputDataAnalog1(override var x: Float? = 0f): InputDataAnalog() {
    override val type: InputType = ANALOG1
    override val axes: Int = 1
}

data class InputDataAnalog2(
    override var x: Float? = 0f,
    /**
     * The Y axis of the analog input data.
     */
    var y: Float? = 0f
): InputDataAnalog() {
    override val type: InputType = ANALOG2
    override val axes: Int = 2
}

data class InputDataAnalog3(
    override var x: Float? = 0f,
    /**
     * The Y axis of the analog input data.
     */
    var y: Float? = 0f,
    /**
     * The Z axis of the analog input data.
     */
    var z: Float? = 0f
): InputDataAnalog() {
    override val type: InputType = ANALOG3
    override val axes: Int = 3
}

data class InputDataAnalog4(
    override var x: Float? = 0f,
    /**
     * The Y axis of the analog input data.
     */
    var y: Float? = 0f,
    /**
     * The Z axis of the analog input data.
     */
    var z: Float? = 0f,
    /**
     * The W axis of the analog input data.
     */
    var w: Float? = 0f
): InputDataAnalog() {
    override val type: InputType = ANALOG4
    override val axes: Int = 4
}