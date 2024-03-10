package computer.living.gamepadyn.test;

import static computer.living.gamepadyn.RawInputAnalog1.*;
import static computer.living.gamepadyn.RawInputDigital.*;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import computer.living.gamepadyn.Axis;
import computer.living.gamepadyn.Configuration;
import computer.living.gamepadyn.Gamepadyn;
import computer.living.gamepadyn.Player;
import computer.living.gamepadyn.ftc.InputBackendFtc;

public class ExampleTest extends OpMode {
    // Construct the initial Gamepadyn instance.
    // Kotlin's compiler infers all type parameters.
    final Gamepadyn<ActionDigital, ActionAnalog1, ActionAnalog2> gamepadyn = Gamepadyn.create(
        // our user-defined actions
        ActionDigital.class,
        ActionAnalog1.class,
        ActionAnalog2.class,
        // The FTC backend, constructed with a reference to this OpMode
        new InputBackendFtc(this)
    );

    @Override
    public void init() {
        Player<ActionDigital, ActionAnalog1, ActionAnalog2> p0 = gamepadyn.getPlayer(0);
        assert p0 != null;

        p0.configuration = new Configuration<>(bindPipe -> {

            bindPipe.action(ActionDigital.DO_STUFF, builder -> builder.input(FACE_RIGHT));

            // a needlessly complicated bind, for pushing the limits of the system.
            bindPipe.action(ActionAnalog2.MOVEMENT,
                it -> it.join(
                    it.add(
                        it.input(TRIGGER_RIGHT),
                        it.multiply(it.input(TRIGGER_LEFT), it.constant(-1f))
                    ),
                    // if neither are pressed, inherit Y
                    it.branch(
                        it.xor(it.input(FACE_UP), it.input(FACE_DOWN)),
                        it.branch(
                            it.input(FACE_UP),
                            it.constant(1f),
                            it.constant(-1f)
                        ),
                        it.split(it.previousState, Axis.Y)
                    )
                )
            );
            // needed to comply with Kotlin's Unit conversion
            return null;

        });
    }


    @Override
    public void loop() {
        // Update the state
        gamepadyn.update();
    }
}
