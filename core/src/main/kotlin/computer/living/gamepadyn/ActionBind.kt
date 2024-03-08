package computer.living.gamepadyn

import kotlin.math.pow
import kotlin.reflect.KClass

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
    ): InputData =
        if (inputState::class == targetActionState::class) inputState else targetActionState
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
        @Suppress("SpellCheckingInspection")
        @JvmStatic fun fromString(name: String): KClass<*>? {
            return when (name) {
                "addf" -> AddFloat::class
                "subf" -> SubtractFloat::class
                "mulf" -> MultiplyFloat::class
                "divf" -> DivideFloat::class
                "expf" -> ExponentiateFloat::class
                "brkf" -> BreakFloat::class
                "conf" -> ConditionalFloat::class

                "swzv" -> SwizzleVec2::class
                "conv" -> ConditionalVec2::class

                "notb" -> NotBool::class

                else -> null
            }
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






