package computer.living.gamepadyn

import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sqrt

internal data class BindPipeArgs<TD, TA, TAA>(
    val gamepadyn: Gamepadyn<TD, TA, TAA>,
    val rawInput: Map<RawInput, InputData>,
    val previousState: InputData
)
        where TD : ActionEnumDigital,
              TA : ActionEnumAnalog1,
              TAA : ActionEnumAnalog2,
              TD : Enum<TD>,
              TA : Enum<TA>,
              TAA : Enum<TAA>

/**
 * Superclass that represents a transformer. Pipes will take one or more values and produce a single value.
 */
sealed class BindPipe {
    internal abstract fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputData
            where TD : ActionEnumDigital,
                  TA : ActionEnumAnalog1,
                  TAA : ActionEnumAnalog2,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA>
}

sealed class BindPipeBool : BindPipe() {
    abstract override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataDigital
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA>
}

sealed class BindPipeFloat : BindPipe() {
    abstract override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog1
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA>

}

sealed class BindPipeVector : BindPipe() {
    abstract override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog2
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA>
}

//region Pipe Markers

internal class PreviousStateMarkerBool<TD> : BindPipeBool()
        where TD : ActionEnumDigital,
              TD : Enum<TD> {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataDigital
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = args.previousState as InputDataDigital
}

internal class PreviousStateMarkerFloat<TA> : BindPipeFloat()
        where TA : ActionEnumAnalog1,
              TA : Enum<TA> {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog1
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = args.previousState as InputDataAnalog1
}

internal class PreviousStateMarkerVector<TAA> : BindPipeVector()
        where TAA : ActionEnumAnalog2,
              TAA : Enum<TAA> {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog2
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = args.previousState as InputDataAnalog2
}

sealed interface BindPipeConstant

data class ConstantBool(
    val x: Boolean
) : BindPipeBool(), BindPipeConstant {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataDigital
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = InputDataDigital(this.x)
}

data class ConstantFloat(
    val x: Float
) : BindPipeFloat(), BindPipeConstant {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog1
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = InputDataAnalog1(this.x)
}

data class ConstantVector(
    val x: Float,
    val y: Float
) : BindPipeVector(), BindPipeConstant {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog2
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = InputDataAnalog2(this.x, this.y)
}

data class AddFloat(
    val a: BindPipeFloat,
    val b: BindPipeFloat
) : BindPipeFloat() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog1
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = InputDataAnalog1(a.eval(args).x + b.eval(args).x)
}

data class SubtractFloat(
    val a: BindPipeFloat,
    val b: BindPipeFloat
) : BindPipeFloat() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog1
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = InputDataAnalog1(a.eval(args).x - b.eval(args).x)
}

data class MultiplyFloat(
    val a: BindPipeFloat,
    val b: BindPipeFloat
) : BindPipeFloat() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog1
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = InputDataAnalog1(a.eval(args).x * b.eval(args).x)
}

data class DivideFloat(
    val a: BindPipeFloat,
    val b: BindPipeFloat
) : BindPipeFloat() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog1
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = InputDataAnalog1(a.eval(args).x / b.eval(args).x)
}

data class PowerFloat(
    val a: BindPipeFloat,
    val b: BindPipeFloat
) : BindPipeFloat() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog1
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = InputDataAnalog1(a.eval(args).x.pow(b.eval(args).x))
}

data class SignFloat(
    val x: BindPipeFloat
) : BindPipeFloat() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog1
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = InputDataAnalog1(x.eval(args).x.sign)
}

data class AbsFloat(
    val x: BindPipeFloat
) : BindPipeFloat() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog1
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = InputDataAnalog1(x.eval(args).x.absoluteValue)
}

data class BranchFloat(
    val condition: BindPipeBool,
    val then: BindPipeFloat,
    val other: BindPipeFloat
) : BindPipeFloat() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog1
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> =
        if (condition.eval(args).active) then.eval(args) else other.eval(args)
}

data class AddVector(
    val a: BindPipeVector,
    val b: BindPipeVector
) : BindPipeVector() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog2
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> {

        val a = a.eval(args)
        val b = b.eval(args)
        return InputDataAnalog2(a.x + b.x, a.y + b.y)
    }
}

data class SubtractVector(
    val a: BindPipeVector,
    val b: BindPipeVector
) : BindPipeVector() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog2
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> {

        val a = a.eval(args)
        val b = b.eval(args)
        return InputDataAnalog2(a.x - b.x, a.y - b.y)
    }
}

data class MultiplyVector(
    val a: BindPipeVector,
    val b: BindPipeVector
) : BindPipeVector() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog2
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> {

        val a = a.eval(args)
        val b = b.eval(args)
        return InputDataAnalog2(a.x * b.x, a.y * b.y)
    }
}

data class DivideVector(
    val a: BindPipeVector,
    val b: BindPipeVector
) : BindPipeVector() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog2
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> {

        val a = a.eval(args)
        val b = b.eval(args)
        return InputDataAnalog2(a.x / b.x, a.y / b.y)
    }
}

data class PowerVector(
    val a: BindPipeVector,
    val b: BindPipeVector
) : BindPipeVector() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog2
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> {

        val a = a.eval(args)
        val b = b.eval(args)
        return InputDataAnalog2(a.x.pow(b.x), a.y.pow(b.y))
    }
}

data class SignVector(
    val x: BindPipeVector
) : BindPipeVector() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog2
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> {

        val x = x.eval(args)
        return InputDataAnalog2(x.x.sign, x.y.sign)
    }
}

data class AbsVector(
    val x: BindPipeVector
) : BindPipeVector() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog2
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> {

        val x = x.eval(args)
        return InputDataAnalog2(x.x.absoluteValue, x.y.absoluteValue)
    }
}

data class BranchVector(
    val condition: BindPipeBool,
    val then: BindPipeVector,
    val other: BindPipeVector
) : BindPipeVector() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog2
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> =
        if (condition.eval(args).active) then.eval(args) else other.eval(args)
}

data class AddVectorFloat(
    val a: BindPipeVector,
    val b: BindPipeFloat
) : BindPipeVector() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog2
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> {
        val a = a.eval(args)
        val b = b.eval(args)
        return InputDataAnalog2(a.x + b.x, a.y + b.x)
    }
}

data class SubtractVectorFloat(
    val a: BindPipeVector,
    val b: BindPipeFloat
) : BindPipeVector() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog2
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> {
        val a = a.eval(args)
        val b = b.eval(args)
        return InputDataAnalog2(a.x - b.x, a.y - b.x)
    }
}

data class MultiplyVectorFloat(
    val a: BindPipeVector,
    val b: BindPipeFloat
) : BindPipeVector() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog2
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> {
        val a = a.eval(args)
        val b = b.eval(args)
        return InputDataAnalog2(a.x * b.x, a.y * b.x)
    }
}

data class DivideVectorFloat(
    val a: BindPipeVector,
    val b: BindPipeFloat
) : BindPipeVector() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog2
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> {
        val a = a.eval(args)
        val b = b.eval(args)
        return InputDataAnalog2(a.x / b.x, a.y / b.x)
    }
}

data class PowerVectorFloat(
    val a: BindPipeVector,
    val b: BindPipeFloat
) : BindPipeVector() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog2
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> {
        val a = a.eval(args)
        val b = b.eval(args)
        return InputDataAnalog2(a.x.pow(b.x), a.y.pow(b.x))
    }
}

data class InputBool(
    val rawInput: RawInputDigital
) : BindPipeBool() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataDigital
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = args.rawInput[rawInput] as InputDataDigital
}

data class InputFloat(
    val rawInput: RawInputAnalog1
) : BindPipeFloat() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog1
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = args.rawInput[rawInput] as InputDataAnalog1
}

data class InputVector(
    val rawInput: RawInputAnalog2
) : BindPipeVector() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog2
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = args.rawInput[rawInput] as InputDataAnalog2
}

data class Length(
    val vec: BindPipeVector
) : BindPipeFloat() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog1
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> {
        val vec = vec.eval(args)
        return InputDataAnalog1(sqrt(vec.x * vec.x + vec.y * vec.x))
    }
}

data class Split(
    val vec: BindPipeVector,
    val component: Axis
) : BindPipeFloat() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog1
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = InputDataAnalog1(
        when (component) {
            Axis.X -> vec.eval(args).x
            Axis.Y -> vec.eval(args).y
        }
    )
}

data class Swizzle(
    val vec: BindPipeVector,
    val x: Axis,
    val y: Axis
) : BindPipeVector() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog2
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> {
        val vec = vec.eval(args)

        return InputDataAnalog2(
            when (x) {
                Axis.X -> vec.x
                Axis.Y -> vec.y
            },
            when (y) {
                Axis.X -> vec.x
                Axis.Y -> vec.y
            }
        )
    }
}

data class Join(
    val x: BindPipeFloat,
    val y: BindPipeFloat
) : BindPipeVector() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataAnalog2
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = InputDataAnalog2(x.eval(args).x, y.eval(args).x)
}

data class Not(
    val x: BindPipeBool
) : BindPipeBool() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataDigital
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = InputDataDigital(!x.eval(args).active)
}

data class And(
    val a: BindPipeBool,
    val b: BindPipeBool
) : BindPipeBool() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataDigital
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = InputDataDigital(a.eval(args).active && b.eval(args).active)
}

data class Or(
    val a: BindPipeBool,
    val b: BindPipeBool
) : BindPipeBool() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataDigital
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = InputDataDigital(a.eval(args).active || b.eval(args).active)
}

data class Xor(
    val a: BindPipeBool,
    val b: BindPipeBool
) : BindPipeBool() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataDigital
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> =
        InputDataDigital(a.eval(args).active xor b.eval(args).active)
}

data class EqualFloat(
    val a: BindPipeFloat,
    val b: BindPipeFloat
) : BindPipeBool() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataDigital
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = InputDataDigital(a.eval(args).x == b.eval(args).x)
}

data class EqualVector(
    val a: BindPipeVector,
    val b: BindPipeVector
) : BindPipeBool() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataDigital
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = InputDataDigital(a.eval(args) == b.eval(args))
}

data class Lt(
    val a: BindPipeFloat,
    val b: BindPipeFloat
) : BindPipeBool() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataDigital
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = InputDataDigital(a.eval(args).x < b.eval(args).x)
}

data class Gt(
    val a: BindPipeFloat,
    val b: BindPipeFloat
) : BindPipeBool() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataDigital
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = InputDataDigital(a.eval(args).x > b.eval(args).x)
}

data class LtEq(
    val a: BindPipeFloat,
    val b: BindPipeFloat
) : BindPipeBool() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataDigital
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = InputDataDigital(a.eval(args).x <= b.eval(args).x)
}

data class GtEq(
    val a: BindPipeFloat,
    val b: BindPipeFloat
) : BindPipeBool() {
    override fun <TD, TA, TAA> eval(
        args: BindPipeArgs<TD, TA, TAA>
    ): InputDataDigital
            where TAA : ActionEnumAnalog2,
                  TA : ActionEnumAnalog1,
                  TD : ActionEnumDigital,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA> = InputDataDigital(a.eval(args).x >= b.eval(args).x)
}

//endregion Pipe Markers