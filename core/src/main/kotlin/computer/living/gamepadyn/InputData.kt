package computer.living.gamepadyn

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
class InputDataDigital(
    /**
     * The boolean state of the input.
     */
    @JvmField var digitalData: Boolean = false
): InputData() {
    override val type = InputType.DIGITAL

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
 * @property analogData The action data. The size of the array is equal to the amount of axes the action has.
 */
class InputDataAnalog(dataFirst: Float?, vararg dataMore: Float?) : InputData() {
    override val type = InputType.ANALOG

    /**
     * The state of the input as an array of floats.
     */
    @JvmField val analogData: Array<Float?>

    /**
     * The amount of axes of the input. Corresponds to the array size of [analogData].
     */
    val axes: Int
        get() { return analogData.size; }

    init {
        this.analogData = arrayOf(dataFirst, *dataMore)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is InputDataAnalog) return false
        return this.analogData.contentEquals(other.analogData)
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + analogData.contentHashCode()
        return result
    }
}