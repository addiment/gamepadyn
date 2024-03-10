package computer.living.gamepadyn.test

import com.qualcomm.robotcore.eventloop.opmode.OpMode

import computer.living.gamepadyn.ActionEnumAnalog1
import computer.living.gamepadyn.ActionEnumAnalog2
import computer.living.gamepadyn.ActionEnumDigital
import computer.living.gamepadyn.Gamepadyn
import computer.living.gamepadyn.RawInputDigital.*
import computer.living.gamepadyn.RawInputAnalog1.*
import computer.living.gamepadyn.RawInputAnalog2.*
import computer.living.gamepadyn.ftc.InputBackendFtc

import computer.living.gamepadyn.test.GamepadynKotlinImpl.TestActionDigital.*
import computer.living.gamepadyn.test.GamepadynKotlinImpl.TestActionAnalog1.*
import computer.living.gamepadyn.test.GamepadynKotlinImpl.TestActionAnalog2.*
import computer.living.gamepadyn.Axis
import computer.living.gamepadyn.Configuration

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

        gamepadyn.getPlayer(0)!!.configuration = Configuration {
            action(LAUNCH_DRONE)    { input(FACE_LEFT) }
            action(MOVEMENT)        { input(STICK_LEFT) }
            action(CLAW)            { input(TRIGGER_RIGHT) }
            action(ROTATION)        { split(input(STICK_RIGHT), Axis.X) }
        }

    }

    override fun start() {

        // Get a reference to the player (FTC Player 1)
        val p0 = gamepadyn.getPlayer(0)!!

//        p0.getState(LAUNCH_DRONE).active
//        p0.getState(MOVEMENT).x
//        p0.getState(MOVEMENT).y
//        p0.getState(ROTATION).x

        // Get the event corresponding to LAUNCH_DRONE and add a lambda function as a listener to it.
        p0.addListener(LAUNCH_DRONE) {
            telemetry.addLine("Button ${if (it.data()) "pressed" else "released"}!")
            telemetry.update()
        }

        // Usually, analog events should be replaced with state checks, but both work.
        p0.addListener(MOVEMENT) {
            telemetry.addLine("Movement input: (${it.data.x}, ${it.data.y})")
            telemetry.update()
        }

    }

    override fun loop() {
        gamepadyn.update()
    }
}
