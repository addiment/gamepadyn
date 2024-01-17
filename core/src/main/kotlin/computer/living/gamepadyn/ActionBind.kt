package computer.living.gamepadyn

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
     * By default, this function returns [inputState] if [inputState] and [targetActionState] are the same type,
     * returning [targetActionState] otherwise.
     *
     * @param inputState the current state of the input ([ActionBind.input]).
     * @param targetActionState the current state of the target action ([ActionBind.targetAction]).
     * @param delta the time since last update (ms)
     * @return the new value of the [targetAction]. To maintain the state, simply return [targetActionState].
     */
    open fun transform(inputState: InputData, targetActionState: InputData, delta: Double) : InputData =
        if (inputState::class == targetActionState::class) inputState
        else targetActionState
}