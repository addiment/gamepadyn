package computer.living.gamepadyn

import computer.living.gamepadyn.InputType.*

/**
 * Superclass represents the state of any input.
 * @see InputDataDigital
 * @see InputDataAnalog
 */
sealed class InputData {
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
    @JvmField var active: Boolean = false
): InputData() {
    override val type = DIGITAL

    operator fun invoke(): Boolean = active

//    override fun toString(): String {
//        return "InputData(${if (active) "true" else "false"})"
//    }

//    override fun equals(other: Any?): Boolean {
//        if (other !is InputDataDigital) return false
//        return this.digitalData == other.digitalData
//    }
//
//    override fun hashCode(): Int {
//        var result = digitalData.hashCode()
//        result = 31 * result + type.hashCode()
//        return result
//    }

}
data class InputDataAnalog1(
    /**
     * The X axis of the analog input data.
     */
    var x: Float = 0f
) : InputData() {
    override val type: InputType = ANALOG1

    operator fun invoke(): Array<Float> = arrayOf(x)
}

data class InputDataAnalog2(
    /**
     * The X axis of the analog input data.
     */
    var x: Float = 0f,
    /**
     * The Y axis of the analog input data.
     */
    var y: Float = 0f
) : InputData() {
    override val type: InputType = ANALOG2

    operator fun invoke(): Array<Float> = arrayOf(x, y)
}