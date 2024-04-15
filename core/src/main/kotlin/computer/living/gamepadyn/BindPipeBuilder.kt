package computer.living.gamepadyn

import kotlinx.serialization.Serializable

/**
 * A pair of a [BindPipe] and an action.
 */
@Serializable
data class Bind<T, TP>(
    val action: T, val pipe: TP
) where T : ActionEnum, T : Enum<T>, TP : BindPipe

class BindPipeBuilder<TD, TA, TAA> internal constructor() where TD : ActionEnumDigital, TA : ActionEnumAnalog1, TAA : ActionEnumAnalog2, TD : Enum<TD>, TA : Enum<TA>, TAA : Enum<TAA> {
    @JvmSynthetic
    internal val digitalPipes = arrayListOf<Bind<TD, BindPipeBool>>()

    @JvmSynthetic
    internal val analog1Pipes = arrayListOf<Bind<TA, BindPipeFloat>>()

    @JvmSynthetic
    internal val analog2Pipes = arrayListOf<Bind<TAA, BindPipeVector>>()

    class BindPipeBuilderDigital<TD> constructor() :
        BindPipeBuilderExpression() where TD : ActionEnumDigital, TD : Enum<TD> {
        @JvmField
        val previousState: BindPipeBool = PreviousStateMarkerBool<TD>()
    }

    class BindPipeBuilderAnalog1<TA> constructor() :
        BindPipeBuilderExpression() where TA : ActionEnumAnalog1, TA : Enum<TA> {
        @JvmField
        val previousState: BindPipeFloat = PreviousStateMarkerFloat<TA>()
    }

    class BindPipeBuilderAnalog2<TAA> constructor() :
        BindPipeBuilderExpression() where TAA : ActionEnumAnalog2, TAA : Enum<TAA> {
        @JvmField
        val previousState: BindPipeVector = PreviousStateMarkerVector<TAA>()
    }

    //            @JvmName("actionDigital")
    fun actionDigital(action: TD, bindPipe: BindPipeBuilderDigital<TD>.() -> BindPipeBool) {
        digitalPipes.add(Bind(action, BindPipeBuilderDigital<TD>().bindPipe()))
    }

    //            @JvmName("actionAnalog1")
    fun actionAnalog1(action: TA, bindPipe: BindPipeBuilderAnalog1<TA>.() -> BindPipeFloat) {
        analog1Pipes.add(Bind(action, BindPipeBuilderAnalog1<TA>().bindPipe()))
    }

    //            @JvmName("actionAnalog2")
    fun actionAnalog2(action: TAA, bindPipe: BindPipeBuilderAnalog2<TAA>.() -> BindPipeVector) {
        analog2Pipes.add(Bind(action, BindPipeBuilderAnalog2<TAA>().bindPipe()))
    }

    sealed class BindPipeBuilderExpression {
        fun constant(x: Boolean) = ConstantBool(x)
        fun constant(x: Float) = ConstantFloat(x)
        fun constant(x: Float, y: Float) = ConstantVector(x, y)
        fun add(a: BindPipeFloat, b: BindPipeFloat): BindPipeFloat = AddFloat(a, b)
        fun add(a: BindPipeVector, b: BindPipeVector): BindPipeVector = AddVector(a, b)
        fun add(a: BindPipeVector, b: BindPipeFloat): BindPipeVector = AddVectorFloat(a, b)
        fun add(a: BindPipeFloat, b: BindPipeVector): BindPipeVector = AddVectorFloat(b, a)
        fun subtract(a: BindPipeFloat, b: BindPipeFloat): BindPipeFloat = SubtractFloat(a, b)

        fun subtract(a: BindPipeVector, b: BindPipeVector): BindPipeVector = SubtractVector(a, b)

        fun subtract(a: BindPipeVector, b: BindPipeFloat): BindPipeVector =
            SubtractVectorFloat(a, b)

        fun subtract(a: BindPipeFloat, b: BindPipeVector): BindPipeVector =
            SubtractVectorFloat(b, a)

        fun multiply(a: BindPipeFloat, b: BindPipeFloat): BindPipeFloat = MultiplyFloat(a, b)

        fun multiply(a: BindPipeVector, b: BindPipeVector): BindPipeVector = MultiplyVector(a, b)

        fun multiply(a: BindPipeVector, b: BindPipeFloat): BindPipeVector =
            MultiplyVectorFloat(a, b)

        fun multiply(a: BindPipeFloat, b: BindPipeVector): BindPipeVector =
            MultiplyVectorFloat(b, a)

        fun divide(a: BindPipeFloat, b: BindPipeFloat): BindPipeFloat = DivideFloat(a, b)
        fun divide(a: BindPipeVector, b: BindPipeVector): BindPipeVector = DivideVector(a, b)

        fun divide(a: BindPipeVector, b: BindPipeFloat): BindPipeVector = DivideVectorFloat(a, b)

        // TODO: division is non-commutative, so it requires another overload. implement it
        fun divide(a: BindPipeFloat, b: BindPipeVector): BindPipeVector = DivideVectorFloat(b, a)

        /**
         * @return [a] to the power of [b].
         */
        fun power(a: BindPipeFloat, b: BindPipeFloat): BindPipeFloat = PowerFloat(a, b)

        /**
         * @return [a] to the power of [b]. This is executed per-component.
         */
        fun power(a: BindPipeVector, b: BindPipeVector): BindPipeVector = PowerVector(a, b)

        /**
         * @return [a] to the power of [b]. This is executed per-component, with each component of [a] raised to the [b].
         */
        fun power(a: BindPipeVector, b: BindPipeFloat): BindPipeVector = PowerVectorFloat(a, b)

        // TODO: exponentiation is non-commutative, so it requires another overload. implement it
//        fun power(a: BindPipeFloat, b: BindPipeVector): BindPipeVector = PowerVectorFloat(b, a)

        fun sign(x: BindPipeFloat): BindPipeFloat = SignFloat(x)
        fun sign(x: BindPipeVector): BindPipeVector = SignVector(x)
        fun abs(x: BindPipeFloat): BindPipeFloat = AbsFloat(x)
        fun abs(x: BindPipeVector): BindPipeVector = AbsVector(x)
        fun branch(
            condition: BindPipeBool, then: BindPipeFloat, other: BindPipeFloat
        ): BindPipeFloat = BranchFloat(condition, then, other)

        fun branch(
            condition: BindPipeBool, then: BindPipeVector, other: BindPipeVector
        ): BindPipeVector = BranchVector(condition, then, other)

        fun input(rawInput: RawInputDigital): BindPipeBool = InputBool(rawInput)
        fun input(rawInput: RawInputAnalog1): BindPipeFloat = InputFloat(rawInput)
        fun input(rawInput: RawInputAnalog2): BindPipeVector = InputVector(rawInput)
        fun length(vec: BindPipeVector): BindPipeFloat = Length(vec)
        fun split(vec: BindPipeVector, component: Axis): BindPipeFloat = Split(vec, component)

        fun swizzle(vec: BindPipeVector, x: Axis, y: Axis): BindPipeVector = Swizzle(vec, x, y)

        fun join(x: BindPipeFloat, y: BindPipeFloat): BindPipeVector = Join(x, y)
        fun not(x: BindPipeBool): BindPipeBool = Not(x)
        fun and(a: BindPipeBool, b: BindPipeBool): BindPipeBool = And(a, b)
        fun or(a: BindPipeBool, b: BindPipeBool): BindPipeBool = Or(a, b)
        fun xor(a: BindPipeBool, b: BindPipeBool): BindPipeBool = Xor(a, b)
        fun equal(a: BindPipeFloat, b: BindPipeFloat): BindPipeBool = EqualFloat(a, b)
        fun equal(a: BindPipeVector, b: BindPipeVector): BindPipeBool = EqualVector(a, b)
        fun lt(a: BindPipeFloat, b: BindPipeFloat): BindPipeBool = Lt(a, b)
        fun gt(a: BindPipeFloat, b: BindPipeFloat): BindPipeBool = Gt(a, b)
        fun ltEq(a: BindPipeFloat, b: BindPipeFloat): BindPipeBool = LtEq(a, b)
        fun gtEq(a: BindPipeFloat, b: BindPipeFloat): BindPipeBool = GtEq(a, b)
    }
}