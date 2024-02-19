package computer.living.gamepadyn;

import java.util.Objects;

@FunctionalInterface
public interface InputEventListener<
    T extends InputData,
    TD extends Enum<TD> & ActionEnumDigital,
    TA extends Enum<TA> & ActionEnumAnalog1,
    TAA extends Enum<TAA> & ActionEnumAnalog2>
{
    void accept(T data, Player<TD, TA, TAA> player);
}