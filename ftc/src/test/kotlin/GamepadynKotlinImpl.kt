import com.qualcomm.robotcore.eventloop.opmode.OpMode

import computer.living.gamepadyn.ActionBind
import computer.living.gamepadyn.ActionEnumAnalog1
import computer.living.gamepadyn.ActionEnumAnalog2
import computer.living.gamepadyn.ActionEnumDigital
import computer.living.gamepadyn.ActionMap
import computer.living.gamepadyn.Configuration
import computer.living.gamepadyn.Gamepadyn
import computer.living.gamepadyn.RawInputDigital.*
import computer.living.gamepadyn.RawInputAnalog1.*
import computer.living.gamepadyn.RawInputAnalog2.*
import computer.living.gamepadyn.ftc.InputBackendFtc

import GamepadynKotlinImpl.TestActionDigital
import GamepadynKotlinImpl.TestActionDigital.*
import GamepadynKotlinImpl.TestActionAnalog1
import GamepadynKotlinImpl.TestActionAnalog1.*
import GamepadynKotlinImpl.TestActionAnalog2
import GamepadynKotlinImpl.TestActionAnalog2.*

class GamepadynKotlinImpl : OpMode() {
    enum class TestActionDigital : ActionEnumDigital {
        LAUNCH_DRONE
    }

    enum class TestActionAnalog1 : ActionEnumAnalog1 {
        CLAW
    }

    enum class TestActionAnalog2 : ActionEnumAnalog2 {
        MOVEMENT,
        ROTATION
    }

    private val gamepadyn = Gamepadyn(
        InputBackendFtc(this),
        strict = true,
        ActionMap(
            TestActionDigital.entries,
            TestActionAnalog1.entries,
            TestActionAnalog2.entries
        )
    )

    override fun init() {

        gamepadyn.players[0].configuration = Configuration(
            ActionBind(LAUNCH_DRONE,    FACE_X),
            ActionBind(MOVEMENT,        STICK_LEFT),
            ActionBind(CLAW,            TRIGGER_RIGHT)
        )
    }

    override fun start() {

        // Get a reference to the player (FTC Player 1)
        val p0 = gamepadyn.getPlayer(0)!!

        // Get the event corresponding to LAUNCH_DRONE and add a lambda function as a listener to it.
        p0.getEvent(LAUNCH_DRONE)!! {
            telemetry.addLine("Button ${if (it.active) "pressed" else "released"}!")
        }

    }

    override fun loop() {
        gamepadyn.update()
        telemetry.update()
    }
}
