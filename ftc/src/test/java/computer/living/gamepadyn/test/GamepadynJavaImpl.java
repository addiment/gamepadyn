package computer.living.gamepadyn.test;

import computer.living.gamepadyn.Axis;
import computer.living.gamepadyn.Configuration;
import computer.living.gamepadyn.Gamepadyn;
import computer.living.gamepadyn.Player;
import computer.living.gamepadyn.ActionEnumDigital;
import computer.living.gamepadyn.ActionEnumAnalog1;
import computer.living.gamepadyn.ActionEnumAnalog2;

import static computer.living.gamepadyn.RawInputDigital.*;
import static computer.living.gamepadyn.RawInputAnalog1.*;
import static computer.living.gamepadyn.RawInputAnalog2.*;
import static computer.living.gamepadyn.test.GamepadynJavaImpl.TestActionDigital.*;
import static computer.living.gamepadyn.test.GamepadynJavaImpl.TestActionAnalog1.*;
import static computer.living.gamepadyn.test.GamepadynJavaImpl.TestActionAnalog2.*;

import computer.living.gamepadyn.ftc.InputBackendFtc;
import kotlin.Unit;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class GamepadynJavaImpl extends OpMode {

    enum TestActionDigital implements ActionEnumDigital {
        LAUNCH_DRONE
    }

    enum TestActionAnalog1 implements ActionEnumAnalog1 {
        CLAW,
        ROTATION
    }

    enum TestActionAnalog2 implements ActionEnumAnalog2 {
        MOVEMENT,
    }

    final Gamepadyn<TestActionDigital, TestActionAnalog1, TestActionAnalog2> gamepadyn = Gamepadyn.create(
        TestActionDigital.class,
        TestActionAnalog1.class,
        TestActionAnalog2.class,
        new InputBackendFtc(this),
        true
    );

    @Override
    public void init() {
        Player<TestActionDigital, TestActionAnalog1, TestActionAnalog2> p0 = gamepadyn.getPlayer(0);
        assert p0 != null;

        p0.configuration = new Configuration<>(bindPipe -> {
            bindPipe.action(LAUNCH_DRONE,     it -> it.input(FACE_LEFT));
            bindPipe.action(MOVEMENT,         it -> it.input(STICK_LEFT));
            bindPipe.action(CLAW,             it -> it.input(TRIGGER_RIGHT));
            bindPipe.action(ROTATION,         it -> it.split(it.input(STICK_RIGHT), Axis.X));

            return null;
        });
    }

    @Override
    public void start() {

        // Get a reference to the player (FTC Player 1)
        Player<TestActionDigital, TestActionAnalog1, TestActionAnalog2> p0 = gamepadyn.getPlayer(0);
        assert p0 != null;

        // Get the event corresponding to LAUNCH_DRONE and add a lambda function as a listener to it.
        p0.addListenerDigital(LAUNCH_DRONE, ev -> {
            telemetry.addLine("Button " + ((ev.data.active) ? "pressed" : "released") + "!");
            telemetry.update();
            return Unit.INSTANCE;
        });

        // Usually, analog events should be replaced with state checks, but both work.
        p0.addListenerAnalog2(MOVEMENT, ev -> {
            telemetry.addLine("Movement input: (" + ev.data.x + ", " + ev.data.y + ")");
            telemetry.update();
        });

    }

    @Override
    public void loop() {
        gamepadyn.update();
        telemetry.update();
    }
}
