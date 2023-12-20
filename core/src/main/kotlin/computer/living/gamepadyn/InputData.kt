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
data class InputDataDigital(
    /**
     * The boolean state of the input.
     */
    @JvmField var digitalData: Boolean = false
): InputData() {
    override val type = InputType.DIGITAL

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
 * @property analogData The action data. The size of the array is equal to the amount of axes the action has.
 */
data class InputDataAnalog(
    /**
     * The state of the input as an array of floats.
     */
    @JvmField val analogData: Array<Float?>
) : InputData() {
    override val type = InputType.ANALOG


    /**
     * The amount of axes of the input. Corresponds to the array size of [analogData].
     */
    val axes: Int
        get() { return analogData.size; }

    constructor(dataFirst: Float?, vararg dataMore: Float?) : this(arrayOf(dataFirst, *dataMore))

    init {
        require(analogData.isNotEmpty()) { "Analog data must not be empty, this implies (axes < 0)!" }
    }

    operator fun get(axis: Int): Float? = analogData[axis]

    override fun equals(other: Any?): Boolean {
        if (other !is InputDataAnalog) return false
        return this.analogData.contentEquals(other.analogData)
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + analogData.contentHashCode()
        return result
    }

    /**
     * Returns a deep copy of the data. This is different from the data class copy method, which is shallow..
     */
    fun copy(): InputDataAnalog {
        return InputDataAnalog(this.analogData.copyOf())
    }

    fun deepCopy() = this.copy()
}