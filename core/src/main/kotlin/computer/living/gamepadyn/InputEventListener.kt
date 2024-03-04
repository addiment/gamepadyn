package computer.living.gamepadyn

import computer.living.gamepadyn.Event.EventData

/**
 * Functional interface for Gamepadyn event listeners. See [Event]
 * @see InputEventListener.onStateChange
 */
@FunctionalInterface
fun interface InputEventListener<T : InputData, TD, TA, TAA>
        where TD : Enum<TD>,
              TD : ActionEnumDigital,
              TA : Enum<TA>,
              TA : ActionEnumAnalog1,
              TAA : Enum<TAA>,
              TAA : ActionEnumAnalog2
{
    /**
     * Called when an action's state changes.
     * @param ev The relevant event data.
     */
    fun onStateChange(ev: EventData<T, TD, TA, TAA>)
}