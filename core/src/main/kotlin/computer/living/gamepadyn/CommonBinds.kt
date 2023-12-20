
package computer.living.gamepadyn

import computer.living.gamepadyn.InputType.ANALOG
import computer.living.gamepadyn.InputType.DIGITAL
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Binds a digital input to one singular axis of an action.
 */
@Suppress("unused")
class BindDigitalToAnalogAxis<T: Enum<T>>(private var activeAmount: Float = 1f, private var inactiveAmount: Float = 0f, private var axis: Int, input: RawInput, targetAction: T) : ActionBind<T>(input, targetAction) {
    override fun transform(data: InputData, targetAction: InputDescriptor): InputData? {
        if (data !is InputDataDigital || targetAction.type != ANALOG || axis >= targetAction.axes) return null

        val f = arrayOfNulls<Float?>(targetAction.axes)

        f[axis] = if (data.digitalData) activeAmount else inactiveAmount
        return InputDataAnalog(f)
    }
}

/**
 * Binds one analog axis of an input to one analog axis of an action action.
 */
@Suppress("unused")
class BindSingleAxis<T: Enum<T>>(private var sourceAxis: Int, private var targetAxis: Int, input: RawInput, targetAction: T) : ActionBind<T>(input, targetAction) {
    override fun transform(data: InputData, targetAction: InputDescriptor): InputData? {
        if (data !is InputDataAnalog || targetAction.type != ANALOG || sourceAxis >= input.descriptor.axes || targetAxis >= targetAction.axes) return null

        val f = arrayOfNulls<Float?>(targetAction.axes)

        f[targetAxis] = data[sourceAxis]
        return InputDataAnalog(f)
    }
}

@Suppress("unused")
class BindSingleAxisThresholdToDigital<T: Enum<T>>(private var threshold: Float = 0.75f, private var axis: Int, input: RawInput, targetAction: T) : ActionBind<T>(input, targetAction) {
    override fun transform(data: InputData, targetAction: InputDescriptor): InputData? {
        if (data !is InputDataAnalog || targetAction.type != DIGITAL || axis >= targetAction.axes) return null

        return data.analogData[axis]?.let { InputDataDigital(abs(it) > threshold) }
    }
}


@Suppress("unused")
class BindSnapInputToOutput<T: Enum<T>>(input: RawInput, targetAction: T) : ActionBind<T>(input, targetAction) {
    override fun transform(data: InputData, targetAction: InputDescriptor): InputData {
        when (data) {
            is InputDataDigital -> {
                return when (targetAction.type) {
                    DIGITAL -> data

                    ANALOG -> {
                        val outValue = if (data.digitalData) 1f else 0f
                        if (targetAction.axes > 1) InputDataAnalog(
                            outValue,
                            *(FloatArray(targetAction.axes - 1) { outValue }.toTypedArray())
                        ) else InputDataAnalog(
                            outValue
                        )
                    }
                }
            }

            is InputDataAnalog -> {
                when (targetAction.type) {
                    DIGITAL -> {
                        return if (data.axes > 1) {
                            var sum = 0f
                            for (e in data.analogData) if (e != null) sum += e * e

                            InputDataDigital(sqrt(sum) >= 0.75)
                        } else {
                            InputDataDigital(data.analogData[0].let { if (it == null) false else (it > 0.75) } )
                        }
                    }
                    ANALOG -> {
                        return if (targetAction.axes > data.axes) {
                            val arr: Array<Float?> = arrayOf(*data.analogData, *arrayOfNulls(data.axes - targetAction.axes))
                            InputDataAnalog(arr)
                        } else if (targetAction.axes < data.axes) {
                            InputDataAnalog(data.analogData.toCollection(ArrayList()).subList(0, targetAction.axes - 1).toTypedArray())
                        } else InputDataAnalog(data.analogData)
                    }
                }
            }
        }
    }
}
