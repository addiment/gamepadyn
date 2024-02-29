package computer.living.gamepadyn

/**
 * Binds the state of one Analog1 axis to a single axis of an Analog2 action.
 * This can be "layered" if [zeroOtherAxis] is false (i.e. to bind two analog1 triggers to one analog2 action)
 */
open class ActionBindAnalog1To2<O> (
    targetAction: O,
    input: RawInputAnalog1,
    /**
     * Which axis of the output to mutate.
     */
    var targetAxis: Axis = Axis.X,
    /**
     * If true, the unused axis will be set to 0f.
     * If false, the unused axis won't be modified.
     */
    var zeroOtherAxis: Boolean = false
) : ActionBind<O>(targetAction, input)
        where O : Enum<O>,
              O : ActionEnumAnalog2
{
    override fun transform(
        inputState: InputData,
        targetActionState: InputData,
        delta: Double
    ) : InputData = if (inputState is InputDataAnalog1 && targetActionState is InputDataAnalog2) {
        // swizzling
        when (targetAxis) {
            Axis.X -> InputDataAnalog2(inputState.x, if (zeroOtherAxis) 0f else targetActionState.y)
            Axis.Y -> InputDataAnalog2(if (zeroOtherAxis) 0f else targetActionState.x, inputState.x)
        }
    } else targetActionState
}

/**
 * Takes one axis of an Analog2 input and maps it onto an Analog1 action.
 */
open class ActionBindAnalog2To1<O> (
    targetAction: O,
    input: RawInputAnalog2,
    /**
     * Which axis to use.
     */
    var sourceAxis: Axis = Axis.X
) : ActionBind<O>(targetAction, input)
        where O : Enum<O>,
              O : ActionEnumAnalog1
{
    override fun transform(
        inputState: InputData,
        targetActionState: InputData,
        delta: Double
    ) : InputData = if (inputState is InputDataAnalog2 && targetActionState is InputDataAnalog1) {
        InputDataAnalog1(when (sourceAxis) {
            Axis.X -> inputState.x
            Axis.Y -> inputState.y
        })
    } else targetActionState
}

/**
 * If a digital
 */
class ActionBindAnalog1Threshold<TD>(
    targetAction: TD,
    input: RawInputAnalog1,
    /**
     * The threshold to compare the input to.
     */
    private val threshold: Float,
    /**
     * If the threshold isn't met and [keepState] is true, it will just output the previous state (instead of `true`).
     */
    private val keepState: Boolean = false
) : ActionBind<TD>(
    targetAction, input
)
    where TD : Enum<TD>,
          TD : ActionEnumDigital
{
    override fun transform(
        inputState: InputData,
        targetActionState: InputData,
        delta: Double
    ): InputData = when {
        // if the types don't match
        (inputState !is InputDataAnalog1) -> targetActionState
        // if the threshold is met
        (inputState.x > threshold) -> InputDataDigital(true)
        // if the threshold isn't met and
        keepState -> targetActionState
        else -> InputDataDigital(false)
    }
}

/**
 * If the analog input is above a certain threshold, output a certain analog value.
 */
class ActionBindAnalog1SnapToAnalog1<TA>(
    targetAction: TA,
    input: RawInputAnalog1,
    /**
     * The value to output if the input is above a certain threshold.
     */
    private val activeValue: Float = 1f,
    /**
     * The value to output if the input is not above a certain threshold. If [Float.NaN], it will output the previous state (good for actions with multiple binds).
     */
    private val inactiveValue: Float = Float.NaN,
    /**
     * The threshold to compare the input to.
     */
    private val threshold: Float = 0.5f,
) : ActionBind<TA>(
    targetAction, input
)
    where TA : Enum<TA>,
          TA : ActionEnumAnalog1
{
    override fun transform(
        inputState: InputData,
        targetActionState: InputData,
        delta: Double
    ): InputData = when {
        (inputState !is InputDataAnalog1) -> targetActionState
        (inputState.x > threshold) -> InputDataAnalog1(activeValue)
        inactiveValue.isNaN() -> targetActionState
        else -> InputDataAnalog1(inactiveValue)
    }
}