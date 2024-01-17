package computer.living.gamepadyn

/**
 * Binds the state of one Analog1 axis to a single axis of an Analog2 action.
 * This can be "layered" if [zeroOtherAxis] is false (i.e. to bind two analog1 triggers to one analog2 action)
 */
open class ActionBindAnalog1to2<O> (
    targetAction: O,
    input: RawInputAnalog1,
    /**
     * Which axis to modify.
     */
    val targetAxis: Axis = Axis.X,
    /**
     * If true, the unused axis will be set to 0f.
     * If false, the unused axis won't be modified.
     */
    val zeroOtherAxis: Boolean = false
) : ActionBind<O>(targetAction, input)
        where O : Enum<O>,
              O : ActionEnumAnalog2
{
    override fun transform(inputState: InputData, targetActionState: InputData, delta: Double) : InputData =
        if (inputState is InputDataAnalog1 && targetActionState is InputDataAnalog2) {
            when (targetAxis) {
                Axis.X -> InputDataAnalog2(inputState.x, if (zeroOtherAxis) 0f else targetActionState.y)
                Axis.Y -> InputDataAnalog2(if (zeroOtherAxis) 0f else targetActionState.x, inputState.x)
            }
        } else targetActionState
}

open class ActionBindAnalog2to1<O> (
    targetAction: O,
    input: RawInputAnalog2,
    /**
     * Which axis to use.
     */
    val sourceAxis: Axis = Axis.X
) : ActionBind<O>(targetAction, input)
        where O : Enum<O>,
              O : ActionEnumAnalog1
{
    override fun transform(inputState: InputData, targetActionState: InputData, delta: Double) : InputData =
        if (inputState is InputDataAnalog2 && targetActionState is InputDataAnalog1) {
            InputDataAnalog1(when (sourceAxis) {
                Axis.X -> inputState.x
                Axis.Y -> inputState.y
            })
        } else targetActionState
}