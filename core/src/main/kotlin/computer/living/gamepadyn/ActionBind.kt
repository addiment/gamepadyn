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

        LENGTH(             FLOAT,  VEC2),
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

    override fun eval(): InputDataAnalog1 = InputDataAnalog1(
        a.eval().x + b.eval().x
    )
    override fun eval(): InputDataAnalog1 = InputDataAnalog1(
        a.eval().x - b.eval().x
    )
    override fun eval(): InputDataAnalog1 = InputDataAnalog1(
        a.eval().x * b.eval().x
    )
        a.eval().x / b.eval().x
    )
    override fun eval(): InputDataAnalog1 = InputDataAnalog1(
        a.eval().x.pow(b.eval().x)
    )
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
    override fun eval(): InputDataAnalog1 {
        val data = v.eval()
        return InputDataAnalog1(
            when (x) {
                Axis.X -> data.x
                Axis.Y -> data.y
            }
        )
    }
    override fun eval(): InputDataAnalog1 = if (v.eval().active) onTrue.eval() else onFalse.eval()
    override fun eval(): InputDataAnalog2 = if (v.eval().active) onTrue.eval() else onFalse.eval()
    override fun eval(): InputDataDigital = InputDataDigital(!v.eval().active)

    companion object {
        class BindPipeBuilder<TD, TA, TAA>
                where TD : ActionEnumDigital,
                  TA : ActionEnumAnalog1,
                  TAA : ActionEnumAnalog2,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA>
        {
            class BindPipeFloat
            class BindPipeBool
            class BindPipeVec2

            fun ADD_FLOAT(          a: FLOAT,   b: FLOAT): FLOAT { }
            fun SUBTRACT_FLOAT(     a: FLOAT,   b: FLOAT): FLOAT { }
            fun MULTIPLY_FLOAT(     a: FLOAT,   b: FLOAT): FLOAT { }
            fun DIVIDE_FLOAT(       a: FLOAT,   b: FLOAT): FLOAT { }
            fun POWER_FLOAT(        a: FLOAT,   b: FLOAT): FLOAT { }
            fun SIGN_FLOAT(         x: FLOAT): FLOAT { }
            fun ABS_FLOAT(          x: FLOAT): FLOAT { }
            fun BRANCH_FLOAT(       a: BOOL,    b: FLOAT,   c: FLOAT): FLOAT { }
            fun ADD_VEC2(           a: VEC2,    b: VEC2): VEC2 { }
            fun SUBTRACT_VEC2(      a: VEC2,    b: VEC2): VEC2 { }
            fun MULTIPLY_VEC2(      a: VEC2,    b:VEC2): VEC2 { }
            fun DIVIDE_VEC2(        a: VEC2,    b:VEC2): VEC2 { }
            fun POWER_VEC2(         a: VEC2,    b:VEC2): VEC2 { }
            fun SIGN_VEC2(          a: VEC2): VEC2 { }
            fun ABS_VEC2(           a: VEC2): VEC2 { }
            fun BRANCH_VEC2(        a: BOOL,    b: VEC2,    c: VEC2): VEC2 { }
            fun ADD_VEC2_FLOAT(     a: VEC2,    b: FLOAT): VEC2 { }
            fun SUBTRACT_VEC2_FLOAT(a: VEC2,    b: FLOAT): VEC2 { }
            fun MULTIPLY_VEC2_FLOAT(a: VEC2,    b: FLOAT): VEC2 { }
            fun DIVIDE_VEC2_FLOAT(  a: VEC2,    b: FLOAT): VEC2 { }
            fun POWER_VEC2_FLOAT(   a: VEC2,    b: FLOAT): VEC2 { }
            fun INPUT_BOOL(         rawInput: INPUT_DIGITAL): BOOL { }
            fun INPUT_FLOAT(        rawInput: INPUT_ANALOG1): FLOAT { }
            fun INPUT_VEC2(         rawInput: INPUT_ANALOG2): VEC2 { }
            fun LENGTH(             a: VEC2): FLOAT { }
            fun BREAK(              a: VEC2,   AXIS): FLOAT { }
            fun SWIZZLE(            AXIS,   AXIS): VEC2 { }
            fun JOIN(               FLOAT,  FLOAT): VEC2 { }
            fun NOT(                BOOL): BOOL { }
            fun AND(                BOOL,   BOOL): BOOL { }
            fun OR(                 BOOL,   BOOL): BOOL { }
            fun XOR(                BOOL,   BOOL): BOOL { }
            fun EQUALS(             FLOAT,  FLOAT): BOOL { }
            fun LT(                 FLOAT,  FLOAT): BOOL { }
            fun GT(                 FLOAT,  FLOAT): BOOL { }
            fun LT_EQ(              FLOAT,  FLOAT): BOOL { }
            fun GT_EQ(              FLOAT,  FLOAT): BOOL { }
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






