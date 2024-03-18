package computer.living.gamepadyn

import kotlin.math.sign

/**
 * Experimental bind system to allow for better serialization.
 * After all, it's not like people keep track of state in their binds, right???? please????
 * All jokes aside, I'm aiming to have this done by Gamepadyn 0.4.0
 */
class Configuration<TD, TA, TAA> (config: BindPipeBuilder<TD, TA, TAA>.() -> Unit)
        where TD : ActionEnumDigital,
              TA : ActionEnumAnalog1,
              TAA : ActionEnumAnalog2,
              TD : Enum<TD>,
              TA : Enum<TA>,
              TAA : Enum<TAA>
{
    internal class Bind<T, TP>(@JvmSynthetic internal val action: T, @JvmSynthetic internal val pipe: TP)
            where T : ActionEnum,
                  T : Enum<T>,
                  TP: BindPipe

    @JvmSynthetic
    internal var digital: ArrayList<Bind<TD, BindPipeBool>>
    @JvmSynthetic
    internal var analog1: ArrayList<Bind<TA, BindPipeFloat>>
    @JvmSynthetic
    internal var analog2: ArrayList<Bind<TAA, BindPipeVector>>

    sealed class BindPipe {
        // TODO: implement this. and just do it with recursion, it will make everything miles easier
        @JvmSynthetic
        internal open fun <TD, TA, TAA> eval(
            gamepadyn: Gamepadyn<TD, TA, TAA>,
            rawInput: Map<RawInput, InputData>,
            previousStateDigital: Map<TD, InputDataDigital>,
            previousStateAnalog1: Map<TA, InputDataAnalog1>,
            previousStateAnalog2: Map<TAA, InputDataAnalog2>
        ): InputData
                where TD : ActionEnumDigital,
                      TA : ActionEnumAnalog1,
                      TAA : ActionEnumAnalog2,
                      TD : Enum<TD>,
                      TA : Enum<TA>,
                      TAA : Enum<TAA>
        {
            class Iter(
                val recipe: BindPipe = this,
                val parent: Iter?,
                val children: ArrayList<Iter> = arrayListOf(),
                var res: InputData? = null
            )

            val root = Iter(this, null)
            //  "stack = head.parent" will pop
            var stack: Iter? = root

            loop@ do {
                val head = stack!!
                when (head.recipe) {
                    is And -> {
                        val a = head.children.getOrNull(0)
                        if (a == null) {
                            stack = Iter(head.recipe.a, head)
                            head.children.add(stack)
                            continue@loop
                        }
                        val b = head.children.getOrNull(1)
                        if (b == null) {
                            stack = Iter(head.recipe.b, head)
                            head.children.add(stack)
                            continue@loop
                        }

                        head.res = InputDataDigital((a.res as InputDataDigital).active && (b.res as InputDataDigital).active)
                        stack = head.parent
                    }
                    is ConstantBool -> {
                        head.res = InputDataDigital(head.recipe.x)
                        stack = head.parent
                    }
                    is EqualFloat -> {
                        val a = head.children.getOrNull(0)
                        if (a == null) {
                            stack = Iter(head.recipe.a, head)
                            head.children.add(stack)
                            continue@loop
                        }
                        val b = head.children.getOrNull(1)
                        if (b == null) {
                            stack = Iter(head.recipe.b, head)
                            head.children.add(stack)
                            continue@loop
                        }

                        head.res = InputDataDigital((a.res as InputDataAnalog1).x == (b.res as InputDataAnalog1).x)
                        stack = head.parent
                    }
                    is EqualVector -> {
                        val a = head.children.getOrNull(0)
                        if (a == null) {
                            stack = Iter(head.recipe.a, head)
                            head.children.add(stack)
                            continue@loop
                        }
                        val b = head.children.getOrNull(1)
                        if (b == null) {
                            stack = Iter(head.recipe.b, head)
                            head.children.add(stack)
                            continue@loop
                        }

                        val ares = (a.res as InputDataAnalog2)
                        val bres = (b.res as InputDataAnalog2)
                        head.res = InputDataDigital(ares.x == bres.x && ares.y == bres.y)
                        stack = head.parent
                    }
                    is Gt -> {
                        val a = head.children.getOrNull(0)
                        if (a == null) {
                            stack = Iter(head.recipe.a, head)
                            head.children.add(stack)
                            continue@loop
                        }
                        val b = head.children.getOrNull(1)
                        if (b == null) {
                            stack = Iter(head.recipe.b, head)
                            head.children.add(stack)
                            continue@loop
                        }

                        head.res = InputDataDigital((a.res as InputDataAnalog1).x > (b.res as InputDataAnalog1).x)
                        stack = head.parent
                    }
                    is GtEq -> {
                        val a = head.children.getOrNull(0)
                        if (a == null) {
                            stack = Iter(head.recipe.a, head)
                            head.children.add(stack)
                            continue@loop
                        }
                        val b = head.children.getOrNull(1)
                        if (b == null) {
                            stack = Iter(head.recipe.b, head)
                            head.children.add(stack)
                            continue@loop
                        }

                        head.res = InputDataDigital((a.res as InputDataAnalog1).x >= (b.res as InputDataAnalog1).x)
                        stack = head.parent
                    }
                    is InputBool -> {
                        head.res = (rawInput[head.recipe.rawInput] as InputDataDigital)
                        stack = head.parent
                    }
                    is Lt -> {
                        val a = head.children.getOrNull(0)
                        if (a == null) {
                            stack = Iter(head.recipe.a, head)
                            head.children.add(stack)
                            continue@loop
                        }
                        val b = head.children.getOrNull(1)
                        if (b == null) {
                            stack = Iter(head.recipe.b, head)
                            head.children.add(stack)
                            continue@loop
                        }

                        head.res = InputDataDigital((a.res as InputDataAnalog1).x < (b.res as InputDataAnalog1).x)
                        stack = head.parent
                    }
                    is LtEq -> {
                        val a = head.children.getOrNull(0)
                        if (a == null) {
                            stack = Iter(head.recipe.a, head)
                            head.children.add(stack)
                            continue@loop
                        }
                        val b = head.children.getOrNull(1)
                        if (b == null) {
                            stack = Iter(head.recipe.b, head)
                            head.children.add(stack)
                            continue@loop
                        }

                        head.res = InputDataDigital((a.res as InputDataAnalog1).x <= (b.res as InputDataAnalog1).x)
                        stack = head.parent
                    }
                    is Not -> TODO()
                    is Or -> TODO()
                    is PreviousStateMarkerBool<*> -> TODO()
                    is Xor -> TODO()
                    is AbsFloat -> TODO()
                    is AddFloat -> TODO()
                    is BranchFloat -> TODO()

                    is ConstantFloat -> {
                        head.res = InputDataAnalog1(head.recipe.x)
                        stack = head.parent
                    }
                    is DivideFloat -> TODO()
                    is InputFloat -> {
                        head.res = (rawInput[head.recipe.rawInput] as InputDataAnalog1)
                        stack = head.parent
                    }
                    is Length -> TODO()
                    is MultiplyFloat -> TODO()
                    is PowerFloat -> TODO()
                    is PreviousStateMarkerFloat<*> -> TODO()
                    is SignFloat -> TODO()
                    is Split -> TODO()
                    is SubtractFloat -> TODO()

                    is AbsVector -> TODO()
                    is AddVector -> TODO()
                    is AddVectorFloat -> TODO()
                    is BranchVector -> TODO()
                    is ConstantVector -> {
                        head.res = InputDataAnalog2(head.recipe.x, head.recipe.y)
                        stack = head.parent
                    }
                    is DivideVector -> TODO()
                    is DivideVectorFloat -> TODO()
                    is InputVector -> {
                        head.res = (rawInput[head.recipe.rawInput] as InputDataAnalog2)
                        stack = head.parent
                    }
                    is Join -> TODO()
                    is MultiplyVector -> TODO()
                    is MultiplyVectorFloat -> TODO()
                    is PowerVector -> TODO()
                    is PowerVectorFloat -> TODO()
                    is PreviousStateMarkerVector<*> -> TODO()
                    is SignVector -> {
                        val x = head.children.getOrNull(0)
                        if (x == null) {
                            stack = Iter(head.recipe.x, head)
                            head.children.add(stack)
                            continue@loop
                        }
                        val xres = x.res as InputDataAnalog2
                        head.res = InputDataAnalog2(xres.x.sign, xres.y.sign)
                        stack = head.parent
                    }
                    is SubtractVector -> TODO()
                    is SubtractVectorFloat -> TODO()
                    is Swizzle -> TODO()
                }
            } while (root.res == null)

            return root.res!!
        }
    }

    sealed class BindPipeBool: BindPipe()
    sealed class BindPipeFloat: BindPipe()
    sealed class BindPipeVector: BindPipe()

    internal class PreviousStateMarkerBool<TD>: BindPipeBool()
            where TD : ActionEnumDigital,
                  TD : Enum<TD>
    internal class PreviousStateMarkerFloat<TA>: BindPipeFloat()
            where TA : ActionEnumAnalog1,
                  TA : Enum<TA>
    internal class PreviousStateMarkerVector<TAA>: BindPipeVector()
        where TAA : ActionEnumAnalog2,
              TAA : Enum<TAA>

    sealed interface BindPipeConstant

    data class ConstantBool(
        internal val x: Boolean
    ) : BindPipeBool(), BindPipeConstant

    data class ConstantFloat(
        internal val x: Float
    ) : BindPipeFloat(), BindPipeConstant

    data class ConstantVector(
        internal val x: Float,
        internal val y: Float
    ) : BindPipeVector(), BindPipeConstant

    data class AddFloat(
        internal val a: BindPipeFloat,
        internal val b: BindPipeFloat
    ) : BindPipeFloat()

    data class SubtractFloat(
        internal val a: BindPipeFloat,
        internal val b: BindPipeFloat
    ) : BindPipeFloat()

    data class MultiplyFloat(
        internal val a: BindPipeFloat,
        internal val b: BindPipeFloat
    ) : BindPipeFloat()

    data class DivideFloat(
        internal val a: BindPipeFloat,
        internal val b: BindPipeFloat
    ) : BindPipeFloat()

    data class PowerFloat(
        internal val a: BindPipeFloat,
        internal val b: BindPipeFloat
    ) : BindPipeFloat()

    data class SignFloat(
        internal val x: BindPipeFloat
    ) : BindPipeFloat()

    data class AbsFloat(
        internal val x: BindPipeFloat
    ) : BindPipeFloat()

    data class BranchFloat(
        internal val condition: BindPipeBool,
        internal val then: BindPipeFloat,
        internal val other: BindPipeFloat
    ) : BindPipeFloat()

    data class AddVector(
        internal val a: BindPipeVector,
        internal val b: BindPipeVector
    ) : BindPipeVector()

    data class SubtractVector(
        internal val a: BindPipeVector,
        internal val b: BindPipeVector
    ) : BindPipeVector()

    data class MultiplyVector(
        internal val a: BindPipeVector,
        internal val b: BindPipeVector
    ) : BindPipeVector()

    data class DivideVector(
        internal val a: BindPipeVector,
        internal val b: BindPipeVector
    ) : BindPipeVector()

    data class PowerVector(
        internal val a: BindPipeVector,
        internal val b: BindPipeVector
    ) : BindPipeVector()

    data class SignVector(
        internal val x: BindPipeVector
    ) : BindPipeVector()

    data class AbsVector(
        internal val x: BindPipeVector
    ) : BindPipeVector()

    data class BranchVector(
        internal val condition: BindPipeBool,
        internal val then: BindPipeVector,
        internal val other: BindPipeVector
    ) : BindPipeVector()

    data class AddVectorFloat(
        internal val a: BindPipeVector,
        internal val b: BindPipeFloat
    ) : BindPipeVector()

    data class SubtractVectorFloat(
        internal val a: BindPipeVector,
        internal val b: BindPipeFloat
    ) : BindPipeVector()

    data class MultiplyVectorFloat(
        internal val a: BindPipeVector,
        internal val b: BindPipeFloat
    ) : BindPipeVector()

    data class DivideVectorFloat(
        internal val a: BindPipeVector,
        internal val b: BindPipeFloat
    ) : BindPipeVector()

    data class PowerVectorFloat(
        internal val a: BindPipeVector,
        internal val b: BindPipeFloat
    ) : BindPipeVector()

    data class InputBool(
        internal val rawInput: RawInputDigital
    ) : BindPipeBool()

    data class InputFloat(
        internal val rawInput: RawInputAnalog1
    ) : BindPipeFloat()

    data class InputVector(
        internal val rawInput: RawInputAnalog2
    ) : BindPipeVector()

    data class Length(
        internal val vec: BindPipeVector
    ) : BindPipeFloat()

    data class Split(
        internal val vec: BindPipeVector,
        internal val component: Axis
    ) : BindPipeFloat()

    data class Swizzle(
        internal val vec: BindPipeVector,
        internal val x: Axis,
        internal val y: Axis
    ) : BindPipeVector()

    data class Join(
        internal val x: BindPipeFloat,
        internal val y: BindPipeFloat
    ) : BindPipeVector()

    data class Not(
        internal val x: BindPipeBool
    ) : BindPipeBool()

    data class And(
        internal val a: BindPipeBool,
        internal val b: BindPipeBool
    ) : BindPipeBool()

    data class Or(
        internal val a: BindPipeBool,
        internal val b: BindPipeBool
    ) : BindPipeBool()

    data class Xor(
        internal val a: BindPipeBool,
        internal val b: BindPipeBool
    ) : BindPipeBool()

    data class EqualFloat(
        internal val a: BindPipeFloat,
        internal val b: BindPipeFloat
    ) : BindPipeBool()

    data class EqualVector(
        internal val a: BindPipeVector,
        internal val b: BindPipeVector
    ) : BindPipeBool()

    data class Lt(
        internal val a: BindPipeFloat,
        internal val b: BindPipeFloat
    ) : BindPipeBool()

    data class Gt(
        internal val a: BindPipeFloat,
        internal val b: BindPipeFloat
    ) : BindPipeBool()

    data class LtEq(
        internal val a: BindPipeFloat,
        internal val b: BindPipeFloat
    ) : BindPipeBool()

    data class GtEq(
        internal val a: BindPipeFloat,
        internal val b: BindPipeFloat
    ) : BindPipeBool()

    companion object {
        class BindPipeBuilder<TD, TA, TAA> internal constructor()
                where TD : ActionEnumDigital,
                      TA : ActionEnumAnalog1,
                      TAA : ActionEnumAnalog2,
                      TD : Enum<TD>,
                      TA : Enum<TA>,
                      TAA : Enum<TAA>
        {
            internal val digitalPipes = arrayListOf<Bind<TD, BindPipeBool>>()
            internal val analog1Pipes = arrayListOf<Bind<TA, BindPipeFloat>>()
            internal val analog2Pipes = arrayListOf<Bind<TAA, BindPipeVector>>()

            class BindPipeBuilderDigital<TD> internal constructor(): BindPipeBuilderExpression()
                    where TD : ActionEnumDigital,
                          TD : Enum<TD>
            {
                @JvmField val previousState: BindPipeBool = PreviousStateMarkerBool<TD>()
            }
            class BindPipeBuilderAnalog1<TA> internal constructor(): BindPipeBuilderExpression()
                    where TA : ActionEnumAnalog1,
                          TA : Enum<TA>
            {
                @JvmField val previousState: BindPipeFloat = PreviousStateMarkerFloat<TA>()
            }
            class BindPipeBuilderAnalog2<TAA> internal constructor(): BindPipeBuilderExpression()
                    where TAA : ActionEnumAnalog2,
                          TAA : Enum<TAA>
            {
                @JvmField val previousState: BindPipeVector = PreviousStateMarkerVector<TAA>()
            }

            @JvmName("actionDigital")
            fun action(action: TD, bindPipe: BindPipeBuilderDigital<TD>.() -> BindPipeBool) {
                digitalPipes.add(Bind(action, BindPipeBuilderDigital<TD>().bindPipe()))
            }
            @JvmName("actionAnalog1")
            fun action(action: TA, bindPipe: BindPipeBuilderAnalog1<TA>.() -> BindPipeFloat) {
                analog1Pipes.add(Bind(action, BindPipeBuilderAnalog1<TA>().bindPipe()))
            }
            @JvmName("actionAnalog2")
            fun action(action: TAA, bindPipe: BindPipeBuilderAnalog2<TAA>.() -> BindPipeVector) {
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
                fun subtract(a: BindPipeFloat, b: BindPipeFloat): BindPipeFloat =
                    SubtractFloat(a, b)

                fun subtract(a: BindPipeVector, b: BindPipeVector): BindPipeVector =
                    SubtractVector(a, b)

                fun subtract(a: BindPipeVector, b: BindPipeFloat): BindPipeVector =
                    SubtractVectorFloat(a, b)

                fun subtract(a: BindPipeFloat, b: BindPipeVector): BindPipeVector =
                    SubtractVectorFloat(b, a)

                fun multiply(a: BindPipeFloat, b: BindPipeFloat): BindPipeFloat =
                    MultiplyFloat(a, b)

                fun multiply(a: BindPipeVector, b: BindPipeVector): BindPipeVector =
                    MultiplyVector(a, b)

                fun multiply(a: BindPipeVector, b: BindPipeFloat): BindPipeVector =
                    MultiplyVectorFloat(a, b)

                fun multiply(a: BindPipeFloat, b: BindPipeVector): BindPipeVector =
                    MultiplyVectorFloat(b, a)

                fun divide(a: BindPipeFloat, b: BindPipeFloat): BindPipeFloat = DivideFloat(a, b)
                fun divide(a: BindPipeVector, b: BindPipeVector): BindPipeVector =
                    DivideVector(a, b)

                fun divide(a: BindPipeVector, b: BindPipeFloat): BindPipeVector =
                    DivideVectorFloat(a, b)

                fun divide(a: BindPipeFloat, b: BindPipeVector): BindPipeVector =
                    DivideVectorFloat(b, a)

                fun power(a: BindPipeFloat, b: BindPipeFloat): BindPipeFloat = PowerFloat(a, b)
                fun power(a: BindPipeVector, b: BindPipeVector): BindPipeVector = PowerVector(a, b)
                fun power(a: BindPipeVector, b: BindPipeFloat): BindPipeVector =
                    PowerVectorFloat(a, b)

                fun power(a: BindPipeFloat, b: BindPipeVector): BindPipeVector =
                    PowerVectorFloat(b, a)

                fun sign(x: BindPipeFloat): BindPipeFloat = SignFloat(x)
                fun sign(x: BindPipeVector): BindPipeVector = SignVector(x)
                fun abs(x: BindPipeFloat): BindPipeFloat = AbsFloat(x)
                fun abs(x: BindPipeVector): BindPipeVector = AbsVector(x)
                fun branch(
                    condition: BindPipeBool,
                    then: BindPipeFloat,
                    other: BindPipeFloat
                ): BindPipeFloat = BranchFloat(condition, then, other)

                fun branch(
                    condition: BindPipeBool,
                    then: BindPipeVector,
                    other: BindPipeVector
                ): BindPipeVector = BranchVector(condition, then, other)

                fun input(rawInput: RawInputDigital): BindPipeBool = InputBool(rawInput)
                fun input(rawInput: RawInputAnalog1): BindPipeFloat = InputFloat(rawInput)
                fun input(rawInput: RawInputAnalog2): BindPipeVector = InputVector(rawInput)
                fun length(vec: BindPipeVector): BindPipeFloat = Length(vec)
                fun split(vec: BindPipeVector, component: Axis): BindPipeFloat =
                    Split(vec, component)

                fun swizzle(vec: BindPipeVector, x: Axis, y: Axis): BindPipeVector =
                    Swizzle(vec, x, y)

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
                fun gtEq(a: BindPipeFloat, b: BindPipeFloat): BindPipeBool = gtEq(a, b)
            }
        }
    }

    init {
        val builder = BindPipeBuilder<TD, TA, TAA>()
        builder.config()
        digital = builder.digitalPipes
        analog1 = builder.analog1Pipes
        analog2 = builder.analog2Pipes
    }
}