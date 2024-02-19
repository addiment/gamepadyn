import com.qualcomm.robotcore.eventloop.opmode.OpMode

import computer.living.gamepadyn.ActionBind
import computer.living.gamepadyn.ActionEnumAnalog1
import computer.living.gamepadyn.ActionEnumAnalog2
import computer.living.gamepadyn.ActionEnumDigital
import computer.living.gamepadyn.Configuration
import computer.living.gamepadyn.Gamepadyn
import computer.living.gamepadyn.RawInputDigital.*
import computer.living.gamepadyn.RawInputAnalog1.*
import computer.living.gamepadyn.RawInputAnalog2.*
import computer.living.gamepadyn.ftc.InputBackendFtc

import GamepadynKotlinImpl.TestActionDigital.*
import GamepadynKotlinImpl.TestActionAnalog1.*
import GamepadynKotlinImpl.TestActionAnalog2.*
import computer.living.gamepadyn.ActionBindAnalog2To1
import computer.living.gamepadyn.Axis

class GamepadynKotlinImpl : OpMode() {
    enum class TestActionDigital : ActionEnumDigital {
        LAUNCH_DRONE
    }

    enum class TestActionAnalog1 : ActionEnumAnalog1 {
        CLAW,
        ROTATION
    }

    enum class TestActionAnalog2 : ActionEnumAnalog2 {
        MOVEMENT
    }

    private val gamepadyn = Gamepadyn.create(
        TestActionDigital::class,
        TestActionAnalog1::class,
        TestActionAnalog2::class,
        InputBackendFtc(this),
        strict = true,
    )

    override fun init() {

        gamepadyn.players[0].configuration = Configuration(
            ActionBind          (LAUNCH_DRONE,    FACE_LEFT             ),
            ActionBind          (MOVEMENT,        STICK_LEFT            ),
            ActionBindAnalog2To1(ROTATION,        STICK_RIGHT,  Axis.X  ),
            ActionBind          (CLAW,            TRIGGER_RIGHT         )
        )

    }

    override fun start() {

        // Get a reference to the player (FTC Player 1)
        val p0 = gamepadyn.getPlayer(0)!!

//        p0.getState(LAUNCH_DRONE).active
//        p0.getState(MOVEMENT).x
//        p0.getState(MOVEMENT).y
//        p0.getState(ROTATION).x

        // Get the event corresponding to LAUNCH_DRONE and add a lambda function as a listener to it.
        p0.addEventListener(LAUNCH_DRONE) { data, _ ->
            telemetry.addLine("Button ${if (data()) "pressed" else "released"}!")
            telemetry.update()
        }

        // Usually, analog events should be replaced with state checks, but both work.
        p0.addEventListener(MOVEMENT) { data, _ ->
            telemetry.addLine("Movement input: (${data.x}, ${data.y})")
            telemetry.update()
        }

    }

    override fun loop() {
        gamepadyn.update()
    }
}
