package computer.living.gamepadyn.test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static computer.living.gamepadyn.RawInputDigital.*;
import static computer.living.gamepadyn.RawInputAnalog1.*;
import static computer.living.gamepadyn.RawInputAnalog2.*;
import static computer.living.gamepadyn.test.JthunksKt.println;
import computer.living.gamepadyn.*;
import kotlin.Pair;

import org.junit.jupiter.api.Test;

import static computer.living.gamepadyn.test.JavaSimulatedInputUnitTest.TestActionDigital.*;
import static computer.living.gamepadyn.test.JavaSimulatedInputUnitTest.TestActionAnalog1.*;
import static computer.living.gamepadyn.test.JavaSimulatedInputUnitTest.TestActionAnalog2.*;

import java.util.concurrent.atomic.AtomicInteger;

class JavaSimulatedInputUnitTest {

    enum TestActionDigital implements ActionEnumDigital {
        DIGITAL_ACTION
    }

    enum TestActionAnalog1 implements ActionEnumAnalog1 {
        ANALOG_1D_ACTION
    }

    enum TestActionAnalog2 implements ActionEnumAnalog2 {
        ANALOG_2D_ACTION
    }

    @Test
    public void testMainJava() {
        println("!!! Gamepadyn test: " + this.getClass().getSimpleName() + " !!!");
        InputBackendTesting sysTest = new InputBackendTesting();
        InputBackendTesting.manipulableStateDigital = false;
        InputBackendTesting.manipulableStateAnalog2d = new Pair<>(0f, 0f);

        Gamepadyn<TestActionDigital, TestActionAnalog1, TestActionAnalog2> gamepadyn = Gamepadyn.create(
            TestActionDigital.class,
            TestActionAnalog1.class,
            TestActionAnalog2.class,

            sysTest,
            true
        );

        println("Player count: " + gamepadyn.getPlayers().size());

        AtomicInteger stateChangeCount = new AtomicInteger();
        int expectedStateChangeCount = 0;

        println("Player count: ${gamepadyn.players.size}");

        Player<TestActionDigital, TestActionAnalog1, TestActionAnalog2> p0 = gamepadyn.getPlayer(0);
        assert p0 != null;

        gamepadyn.update();

        p0.setBinds(new Configuration<>(
            new ActionBind<>(DIGITAL_ACTION,      FACE_DOWN),
            new ActionBind<>(ANALOG_1D_ACTION,    TRIGGER_RIGHT),
            new ActionBind<>(ANALOG_2D_ACTION,    STICK_RIGHT)
        ));

        gamepadyn.update();

        p0.getEventDigital(DIGITAL_ACTION).addListener(ev -> {
            println("Debug action ran! (new value: " + ev.data.active + ")");
            stateChangeCount.getAndIncrement();
        });

        p0.getEventAnalog1(ANALOG_1D_ACTION).addListener(ev -> {
            println("Analog 1D action ran! (new value: (" + ev.data.x + ")");
            stateChangeCount.getAndIncrement();
        });

        p0.getEventAnalog2(ANALOG_2D_ACTION).addListener(ev -> {
            println("Analog 2D action ran! (new value: (" + ev.data.x + ", " + ev.data.y + ")");
            stateChangeCount.getAndIncrement();
        });

        assertFalse(p0.getStateDigital(DIGITAL_ACTION).active);
        InputDataAnalog2 s1 = p0.getStateAnalog2(ANALOG_2D_ACTION);
        assertEquals(0f, s1.x);
        assertEquals(0f, s1.y);

        gamepadyn.update();

        assertFalse(p0.getStateDigital(DIGITAL_ACTION).active);
        InputDataAnalog2 s2 = p0.getStateAnalog2(ANALOG_2D_ACTION);
        assertEquals(0f, s2.x);
        assertEquals(0f, s2.y);

        InputBackendTesting.manipulableStateDigital = true;
        expectedStateChangeCount++;

        gamepadyn.update();
        assertTrue(p0.getStateDigital(DIGITAL_ACTION).active);
        InputDataAnalog2 s3 = p0.getStateAnalog2(ANALOG_2D_ACTION);
        assertEquals(0f, s3.x);
        assertEquals(0f, s3.y);

        gamepadyn.update();

        InputBackendTesting.manipulableStateAnalog2d = new Pair<>(0f, 1f);
        expectedStateChangeCount++;

        assertTrue(p0.getStateDigital(DIGITAL_ACTION).active);
        gamepadyn.update();

        InputDataAnalog2 s4 = p0.getStateAnalog2(ANALOG_2D_ACTION);
        assertEquals(0f, s4.x);
        assertEquals(1f, s4.y);

        InputBackendTesting.manipulableStateDigital = true;
        InputDataAnalog2 s5 = p0.getStateAnalog2(ANALOG_2D_ACTION);
        assertEquals(0f, s5.x);
        assertEquals(1f, s5.y);

        gamepadyn.update();
        assertTrue(p0.getStateDigital(DIGITAL_ACTION).active);
        gamepadyn.update();
        assertTrue(p0.getStateDigital(DIGITAL_ACTION).active);

        InputBackendTesting.manipulableStateDigital = false;
        expectedStateChangeCount++;
        InputBackendTesting.manipulableStateAnalog2d = new Pair<>(1f, 0f);
        expectedStateChangeCount++;

        gamepadyn.update();
        assertFalse(p0.getStateDigital(DIGITAL_ACTION).active);
        InputDataAnalog2 s6 = p0.getStateAnalog2(ANALOG_2D_ACTION);
        assertEquals(1f, s6.x);
        assertEquals(0f, s6.y);

        InputBackendTesting.manipulableStateDigital = true;
        gamepadyn.update();
        expectedStateChangeCount++;
        assertTrue(p0.getStateDigital(DIGITAL_ACTION).active);


        assertEquals(expectedStateChangeCount, stateChangeCount.get());
    }

}