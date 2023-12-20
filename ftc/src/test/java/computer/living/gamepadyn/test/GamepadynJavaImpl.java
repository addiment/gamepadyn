package computer.living.gamepadyn.test;

import computer.living.gamepadyn.JavaThunks.Tak;

import java.util.Objects;

import computer.living.gamepadyn.Gamepadyn;
import computer.living.gamepadyn.Player;
import computer.living.gamepadyn.ftc.InputBackendFtc;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class GamepadynJavaImpl extends OpMode {

    enum TestAction {
        MOVEMENT,
        ROTATION,
        CLAW,
        DEBUG_ACTION
    }

    Gamepadyn<TestAction> gamepadyn;

    @Override
    public void init() {
        gamepadyn = new Gamepadyn<>(new InputBackendFtc(this),
            Tak.analog(TestAction.MOVEMENT, 2),
            Tak.analog(TestAction.ROTATION, 1),
            Tak.digital(TestAction.CLAW),
            Tak.digital(TestAction.DEBUG_ACTION)
        );
    }

    @Override
    public void start() {

        // There's a bit of boilerplate here because of how Java treats nullability.
        // Gamepadyn was designed for Kotlin, but built to also work with Java.
        // It's much easier in Kotlin.

        // Get a reference to the player (FTC Player 1)
        Player<TestAction> p0 = gamepadyn.getPlayer(0);
        assert p0 != null;

        // Get the event corresponding to DEBUG_ACTION and add a lambda function as a listener to it.
        Objects.requireNonNull(p0.getEventDigital(TestAction.DEBUG_ACTION)).addListener(it -> {
                telemetry.addLine("Button " + ((it.digitalData) ? "pressed" : "released") + "!");
        });
    }

    @Override
    public void loop() {
        gamepadyn.update();
        telemetry.update();
    }
}
