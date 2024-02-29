package computer.living.gamepadyn

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