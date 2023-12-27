package computer.living.gamepadyn.test;

import java.util.Objects;

import computer.living.gamepadyn.Gamepadyn;
import computer.living.gamepadyn.ActionMap;
import computer.living.gamepadyn.Player;
import computer.living.gamepadyn.ActionEnumDigital;
import computer.living.gamepadyn.ActionEnumAnalog1;
import computer.living.gamepadyn.ActionEnumAnalog2;

import computer.living.gamepadyn.ftc.InputBackendFtc;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class GamepadynJavaImpl extends OpMode {

    enum TestActionDigital implements ActionEnumDigital {
        LAUNCH_DRONE
    }

    enum TestActionAnalog1 implements ActionEnumAnalog1 {
        CLAW
    }

    enum TestActionAnalog2 implements ActionEnumAnalog2 {
        MOVEMENT,
        ROTATION
    }
    Gamepadyn<TestActionDigital, TestActionAnalog1, TestActionAnalog2> gamepadyn = new Gamepadyn<>(new InputBackendFtc(this),
        true,
        new ActionMap<>(
            TestActionDigital.values(),
            TestActionAnalog1.values(),
            TestActionAnalog2.values()
        )
    );

    @Override
    public void init() { }

    @Override
    public void start() {

        // There's a bit of boilerplate here because of how Java treats nullability.
        // Gamepadyn was designed for Kotlin, but built to also work with Java.
        // It's much easier in Kotlin.

        // Get a reference to the player (FTC Player 1)
        Player<TestActionDigital, TestActionAnalog1, TestActionAnalog2> p0 = gamepadyn.getPlayer(0);
        assert p0 != null;

        // Get the event corresponding to LAUNCH_DRONE and add a lambda function as a listener to it.
        Objects.requireNonNull(p0.getEventDigital(TestActionDigital.LAUNCH_DRONE)).addListener(it -> {
            telemetry.addLine("Button " + ((it.active) ? "pressed" : "released") + "!");
        });

    }

    @Override
    public void loop() {
        gamepadyn.update();
        telemetry.update();
    }
}
