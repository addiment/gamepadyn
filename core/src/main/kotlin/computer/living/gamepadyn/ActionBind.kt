package computer.living.gamepadyn

import computer.living.gamepadyn.BindPipe.BindPipeDataType.*
import kotlin.math.pow

// TODO: I know I already re-did this entire system once... but would it hurt to do it again?
//      I'm still the only person using this in prod. What if we had a builder-like system?
//      It would be cool to declare binds using a system like Kotlin's std extensions (i.e. `.let`).
open class ActionBind<O> (
    val targetAction: O,
    val input: RawInput,
)
    where O : Enum<O>,
          O : ActionEnum
{

    /**
     * Transforms input data.
     *
     * By default, this will set the [targetAction]'s value to the [inputState] if [inputState] and [targetActionState] are the same type,
     * or setting it to [targetActionState] (aka. not mutating the data) otherwise.
     *
     * @param inputState the current state of the input ([ActionBind.input]).
     * @param targetActionState the current state of the target action ([ActionBind.targetAction]).
     * @param delta the time since last update (ms)
     * @return the new value of the [targetAction]. To maintain the state, simply return [targetActionState].
     */
    open fun transform(
        inputState: InputData,
        targetActionState: InputData,
        delta: Double
    ): InputData = if (inputState::class == targetActionState::class)
        inputState else targetActionState
}


/**
 * Experimental bind system to allow for better serialization.
 * After all, it's not like people keep track of state in their binds, right???? please????
 * All jokes aside, I'm aiming to have this done by Gamepadyn 0.4.0
 */
// TODO: finish declaring operators:
//      * rawinput (rawinput_digital) -> bool
//      * rawinput (rawinput_analog2) -> vec2
//      * make (float, float) -> vec2, joins two floats to make a vec2
//      * greq (float, float) -> float, >=
//      * lteq (float, float) -> float, <=
//      * lt (float, float) -> float, <
//      * gt (float, float) -> float, >
//      * eq (float, float) -> float, ==
sealed class BindPipe {
    internal abstract fun eval(): InputData

    enum class BindPipeDataType {
        BOOL,
        FLOAT,
        VEC2,
        AXIS,
        INPUT_DIGITAL,
        INPUT_ANALOG1,
        INPUT_ANALOG2,
    }

    // my problem with using an enum here is that we don't get to leverage the type system as much
    // see: RawInput.kt (marker interfaces instead of enum properties)
    // but function signatures are complicated!!! I don't want to create something that looks like
    // Java's functional interfaces (i.e. Consumer, BiConsumer, TriConsumer, etc.)
    // but I fear I must
    enum class BindPipeOperator(
        val returnType: BindPipeDataType,
        param1: BindPipeDataType,
        vararg params: BindPipeDataType
    ) {
        ADD_FLOAT(          FLOAT,  FLOAT,  FLOAT),
        SUBTRACT_FLOAT(     FLOAT,  FLOAT,  FLOAT),
        MULTIPLY_FLOAT(     FLOAT,  FLOAT,  FLOAT),
        DIVIDE_FLOAT(       FLOAT,  FLOAT,  FLOAT),
        POWER_FLOAT(        FLOAT,  FLOAT,  FLOAT),
        SIGN_FLOAT(         FLOAT,  FLOAT),
        ABS_FLOAT(          FLOAT,  FLOAT),
        BRANCH_FLOAT(       FLOAT,  BOOL,   FLOAT,  FLOAT),

        ADD_VEC2(           VEC2,   VEC2,   VEC2),
        SUBTRACT_VEC2(      VEC2,   VEC2,   VEC2),
        MULTIPLY_VEC2(      VEC2,   VEC2,   VEC2),
        DIVIDE_VEC2(        VEC2,   VEC2,   VEC2),
        POWER_VEC2(         VEC2,   VEC2,   VEC2),
        SIGN_VEC2(          VEC2,   VEC2),
        ABS_VEC2(           VEC2,   VEC2),
        BRANCH_VEC2(        VEC2,   BOOL,   VEC2,   VEC2),

        ADD_VEC2_FLOAT(     VEC2,   VEC2,   FLOAT),
        SUBTRACT_VEC2_FLOAT(VEC2,   VEC2,   FLOAT),
        MULTIPLY_VEC2_FLOAT(VEC2,   VEC2,   FLOAT),
        DIVIDE_VEC2_FLOAT(  VEC2,   VEC2,   FLOAT),
        POWER_VEC2_FLOAT(   VEC2,   VEC2,   FLOAT),

        INPUT_BOOL(         BOOL,   INPUT_DIGITAL),
        INPUT_FLOAT(        FLOAT,  INPUT_ANALOG1),
        INPUT_VEC2(         VEC2,   INPUT_ANALOG2),

        LENGTH(             FLOAT,  VEC2,   VEC2),
        BREAK(              FLOAT,  VEC2,   AXIS),
        SWIZZLE(            VEC2,   AXIS,   AXIS),
        JOIN(               VEC2,   FLOAT,  FLOAT),

        NOT(                BOOL,   BOOL),
        AND(                BOOL,   BOOL,   BOOL),
        OR(                 BOOL,   BOOL,   BOOL),
        XOR(                BOOL,   BOOL,   BOOL),

        EQUALS(             BOOL,   FLOAT,  FLOAT),
        LT(                 BOOL,   FLOAT,  FLOAT),
        GT(                 BOOL,   FLOAT,  FLOAT),
        LT_EQ(              BOOL,   FLOAT,  FLOAT),
        GT_EQ(              BOOL,   FLOAT,  FLOAT),
        ;

        val params: Array<BindPipeDataType> = arrayOf(param1, *params)
    }

    sealed class BindPipeBool : BindPipe() {
        abstract override fun eval(): InputDataDigital
    }
    sealed class BindPipeFloat : BindPipe() {
        abstract override fun eval(): InputDataAnalog1
    }
    sealed class BindPipeVec2 : BindPipe() {
        abstract override fun eval(): InputDataAnalog2
    }

    class AddFloat(internal val a: BindPipeFloat, internal val b: BindPipeFloat) : BindPipeFloat() {
        override fun eval(): InputDataAnalog1 = InputDataAnalog1(
            a.eval().x + b.eval().x
        )
    }

    class SubtractFloat(internal val a: BindPipeFloat, internal val b: BindPipeFloat) : BindPipeFloat() {
        override fun eval(): InputDataAnalog1 = InputDataAnalog1(
            a.eval().x - b.eval().x
        )
    }

    class MultiplyFloat(internal val a: BindPipeFloat, internal val b: BindPipeFloat) : BindPipeFloat() {
        override fun eval(): InputDataAnalog1 = InputDataAnalog1(
            a.eval().x * b.eval().x
        )
    }
    class DivideFloat(internal val a: BindPipeFloat, internal val b: BindPipeFloat) : BindPipeFloat() {
        override fun eval(): InputDataAnalog1 = InputDataAnalog1(
            a.eval().x / b.eval().x
        )
    }

    class ExponentiateFloat(internal val a: BindPipeFloat, internal val b: BindPipeFloat) : BindPipeFloat() {
        override fun eval(): InputDataAnalog1 = InputDataAnalog1(
            a.eval().x.pow(b.eval().x)
        )
    }

    class SwizzleVec2(internal val v: BindPipeVec2, internal val x: Axis, internal val y: Axis) : BindPipeVec2() {
        override fun eval(): InputDataAnalog2 {
            val data = v.eval()
            return InputDataAnalog2(
                when (x) {
                    Axis.X -> data.x
                    Axis.Y -> data.y
                },
                when (y) {
                    Axis.X -> data.x
                    Axis.Y -> data.y
                }
            )
        }
    }

    class BreakFloat(internal val v: BindPipeVec2, internal val x: Axis) : BindPipeFloat() {
        override fun eval(): InputDataAnalog1 {
            val data = v.eval()
            return InputDataAnalog1(
                when (x) {
                    Axis.X -> data.x
                    Axis.Y -> data.y
                }
            )
        }
    }

    class ConditionalFloat(internal val v: BindPipeBool, internal val onTrue: BindPipeFloat, internal val onFalse: BindPipeFloat) : BindPipeFloat() {
        override fun eval(): InputDataAnalog1 = if (v.eval().active) onTrue.eval() else onFalse.eval()
    }

    class ConditionalVec2(internal val v: BindPipeBool, internal val onTrue: BindPipeVec2, internal val onFalse: BindPipeVec2) : BindPipeVec2() {
        override fun eval(): InputDataAnalog2 = if (v.eval().active) onTrue.eval() else onFalse.eval()
    }

    class NotBool(internal val v: BindPipeBool) : BindPipeBool() {
        override fun eval(): InputDataDigital = InputDataDigital(!v.eval().active)
    }

    class InputStateFloat(internal val input: RawInputAnalog1) : BindPipeFloat() {
        override fun eval(): InputDataAnalog1 = TODO() // we need access to the backend to implement this
    }

    companion object {
//        @Suppress("SpellCheckingInspection")
//        @JvmStatic fun fromString(name: String): KClass<*>? {
//            return when (name) {
//                "addf" -> AddFloat::class
//                "subf" -> SubtractFloat::class
//                "mulf" -> MultiplyFloat::class
//                "divf" -> DivideFloat::class
//                "expf" -> ExponentiateFloat::class
//                "brkf" -> BreakFloat::class
//                "conf" -> ConditionalFloat::class
//
//                "swzv" -> SwizzleVec2::class
//                "conv" -> ConditionalVec2::class
//
//                "notb" -> NotBool::class
//
//                else -> null
//            }
//        }

        class BindPipeBuilder<TD, TA, TAA>
                where TD : ActionEnumDigital,
                  TA : ActionEnumAnalog1,
                  TAA : ActionEnumAnalog2,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA>
        {
            inline fun add(a: BindPipeFloat, b: BindPipeFloat) = AddFloat(a, b)
//            fun input(rawInput: RawInputDigital) = InputStateBool(rawInput)
            fun input(rawInput: RawInputAnalog1) = InputStateFloat(rawInput)
//            fun input(rawInput: RawInputAnalog2) = InputStateVec2(rawInput)
        }

        inline fun <TD, TA, TAA> builder(gamepadyn: Gamepadyn<TD, TA, TAA>, config: BindPipeBuilder<TD, TA, TAA>.() -> Unit): BindPipe
                where TD : ActionEnumDigital,
                      TA : ActionEnumAnalog1,
                      TAA : ActionEnumAnalog2,
                      TD : Enum<TD>,
                      TA : Enum<TA>,
                      TAA : Enum<TAA>
        {
            val builder = BindPipeBuilder<TD, TA, TAA>()
            builder.config()
            return builder
        }
    }
}

//// bind right trigger to right, left trigger to left, face up to forward, face down to backward. stupid, but it's an example.
//
//MOVEMENT {
//    x = add (
//        raw(TRIGGER_RIGHT),
//        mul(
//            raw(TRIGGER_LEFT),
//            -1f
//        )
//    )
//    y = add (
//        if (
//            raw(FACE_UP),
//        1f,
//        0f
//    ),
//    if (
//        raw(FACE_DOWN),
//    -1f,
//    0f
//    )
//    )
//}






