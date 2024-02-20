package computer.living.gamepadyn;

/**
 * Functional interface for Gamepadyn event listeners. See {@link Event}
 * @see InputEventListener#onStateChange
 * @param <T>
 * @param <TD>
 * @param <TA>
 * @param <TAA>
 */
@FunctionalInterface
public interface InputEventListener<
    T extends InputData,
    TD extends Enum<TD> & ActionEnumDigital,
    TA extends Enum<TA> & ActionEnumAnalog1,
    TAA extends Enum<TAA> & ActionEnumAnalog2>
{
    /**
     * Called when an action's state changes.
     * @param ev The relevant event data.
     */
    void onStateChange(Event.EventData<T, TD, TA, TAA> ev);
}