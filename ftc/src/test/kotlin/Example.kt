import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotor.RunMode.*
import com.qualcomm.robotcore.hardware.Servo
import computer.living.gamepadyn.ActionBind
import computer.living.gamepadyn.ActionEnumAnalog1
import computer.living.gamepadyn.ActionEnumAnalog2
import computer.living.gamepadyn.ActionEnumDigital
import computer.living.gamepadyn.Configuration
import computer.living.gamepadyn.Gamepadyn
import computer.living.gamepadyn.RawInput
import computer.living.gamepadyn.RawInputDigital
import computer.living.gamepadyn.ftc.InputBackendFtc

private class Test0 : OpMode() {
    override fun init() {}

    override fun loop() {

        if (gamepad1.b) {
            telemetry.addLine("gamepad1 a pressed")
        } else {
            telemetry.addLine("gamepad1 a released")
        }

    }
}

private class Test1 : OpMode() {
    override fun init() {}

    // this would not work in reality, but this is for example
    val motorSpinner: DcMotor = hardwareMap.get(DcMotor::class.java, "spinner")

    override fun loop() {
        motorSpinner.mode = RUN_WITHOUT_ENCODER

        // while the button is being held down
        if (gamepad1.a) {
            // spin the motor
            motorSpinner.power = 1.0
        } else {
            // stop spinning the motor
            motorSpinner.power = 0.0
        }
    }
}

private class Test2 : OpMode() {
    override fun init() {}

    // this would not work in reality, but this is for example
    val motorSpinner: DcMotor = hardwareMap.get(DcMotor::class.java, "spinner")

    var isSpinning: Boolean = false

    override fun loop() {
        motorSpinner.mode = RUN_WITHOUT_ENCODER

        // is the button is being held down
        isSpinning = gamepad1.a

        // set the spinner power
        motorSpinner.power = if (isSpinning) 1.0 else 0.0
    }
}

/**
 * The problem:
 * - 2 more variables per toggled action
 * - Complicated (in English) to consistently AND correctly name state pairs
 *   - "is the pulley up" vs "did we cycle the pulley's up-ness (!)"
 *   - "is the pulley's height (!)" vs "did we cycle the pulley's height"
 *   - "up" is an adverb (i.e. "something(noun) is(verb) up(adverb)")
 *   - "height" is a noun (i.e. "something is at(preposition) height(noun, object) four(numeral)"
 */
private class Test4 : OpMode() {
    override fun init() {}

    // this would not work in reality, but this is for example
    val servoClaw: Servo = hardwareMap.get(Servo::class.java, "claw")

    // state
    var isPulleyUp              = false
    var isClawOpen              = false
    // previous state
    var didCyclePulleyHeight    = false
    var didCycleClawOpen        = false

    override fun loop() {
        // one way of doing it
        if (gamepad1.x && !didCyclePulleyHeight) {
            didCyclePulleyHeight = true
            isPulleyUp = !isPulleyUp
        } else if (!gamepad1.x) didCyclePulleyHeight = false

        // another way (I usually prefer this)
        if (gamepad1.b) {
            if (!didCycleClawOpen) {
                didCycleClawOpen = true
                isClawOpen = !isClawOpen
            }
        } else didCycleClawOpen = false

        // set whether the claw is open
        servoClaw.position = if (isClawOpen) 1.0 else 0.0
    }
}

private class Test5 : OpMode() {
    override fun init() {}

    // this would not work in reality, but this is for example
    val servoClaw: Servo = hardwareMap.get(Servo::class.java, "claw")
    var isArmUp = false
    var didCycleArmUp = false

    override fun loop() {

        if (gamepad1.a) {
            if (!didCycleArmUp) {
                didCycleArmUp = true
                isArmUp = !isArmUp
            }
        } else {
            didCycleArmUp = false
        }

        // set whether the claw is open
        servoClaw.position = if (isArmUp) 1.0 else 0.0
    }
}

enum class ActionDigital : ActionEnumDigital {
    ARM_UP
}

enum class ActionAnalog1 : ActionEnumAnalog1
enum class ActionAnalog2 : ActionEnumAnalog2

private class Test6 : OpMode() {
    var gamepadyn = Gamepadyn.create(
        ActionDigital::class,
        ActionAnalog1::class,
        ActionAnalog2::class,
        InputBackendFtc(this)
    )

    var isArmUp = false

    override fun init() {
        gamepadyn.players[0].configuration = Configuration(
            ActionBind(ActionDigital.ARM_UP, RawInputDigital.FACE_LEFT)
        )

        gamepadyn.addListener(ActionDigital.ARM_UP) {
            if (it.data()) isArmUp = !isArmUp
        }
    }

    // this would not work in reality, but this is for example
    val servoClaw: Servo = hardwareMap.get(Servo::class.java, "claw")

    override fun loop() {
        gamepadyn.update()

        servoClaw.position = if (isArmUp) 1.0 else 0.0
    }
}












