package computer.living.gamepadyn;

@FunctionalInterface
public interface InputEventListener<
    T extends InputData,
    TD extends Enum<TD> & ActionEnumDigital,
    TA extends Enum<TA> & ActionEnumAnalog1,
    TAA extends Enum<TAA> & ActionEnumAnalog2>
{
    void accept(Event.EventData<T, TD, TA, TAA> it);
}