import computer.living.gamepadyn.InputType.*
import computer.living.gamepadyn.ActionBind
import computer.living.gamepadyn.Configuration
import computer.living.gamepadyn.GDesc
import computer.living.gamepadyn.Gamepadyn
import computer.living.gamepadyn.RawInput
import computer.living.gamepadyn.ftc.InputBackendFtc

import com.qualcomm.robotcore.eventloop.opmode.OpMode

class GamepadynKotlinImpl : OpMode() {

    enum class TestAction {
        MOVEMENT,
        ROTATION,
        CLAW,
        DEBUG_ACTION
    }

    private lateinit var gamepadyn: Gamepadyn<TestAction>


    override fun init() {
        gamepadyn = Gamepadyn(
            InputBackendFtc(this),
            true,
            TestAction.MOVEMENT            to GDesc.analog(2),
            TestAction.ROTATION            to GDesc.analog(1),
            TestAction.CLAW                to GDesc.digital(),
            TestAction.DEBUG_ACTION        to GDesc.digital()
        )

        gamepadyn.players[0].configuration = Configuration(
            ActionBind(RawInput.FACE_A, TestAction.DEBUG_ACTION)
        )
    }

    override fun start() {

        // Get a reference to the player (FTC Player 1)
        val p0 = gamepadyn.players[0]
        // Get the event corresponding to DEBUG_ACTION and add a lambda function as a listener to it.
        p0.getEventDigital(TestAction.DEBUG_ACTION)!!.addListener {
            telemetry.addLine("Button ${if (it.digitalData) "pressed"; else "released"}!")
        }

    }

    override fun loop() {
        gamepadyn.update()
        telemetry.update()
    }
}
