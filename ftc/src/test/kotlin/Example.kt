import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotor.RunMode.*
import com.qualcomm.robotcore.hardware.Servo
import computer.living.gamepadyn.ActionBind
import computer.living.gamepadyn.ActionEnumAnalog1
import computer.living.gamepadyn.ActionEnumAnalog2
import computer.living.gamepadyn.ActionEnumDigital
import computer.living.gamepadyn.Axis
import computer.living.gamepadyn.BindPipe
import computer.living.gamepadyn.Configuration
import computer.living.gamepadyn.Gamepadyn
import computer.living.gamepadyn.InputData
import computer.living.gamepadyn.InputDataAnalog1
import computer.living.gamepadyn.InputDataAnalog2
import computer.living.gamepadyn.RawInputAnalog1
import computer.living.gamepadyn.RawInputDigital
import computer.living.gamepadyn.ftc.InputBackendFtc
import kotlin.math.pow
import kotlin.reflect.KClass

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
    DO_STUFF,
}

enum class ActionAnalog1 : ActionEnumAnalog1 {
    ROTATION,
}
enum class ActionAnalog2 : ActionEnumAnalog2 {
    MOVEMENT,
}

@TeleOp(name = "Gamepadyn Example")
private class ExampleGamepadynOpMode : OpMode() {
    // Construct the initial Gamepadyn instance.
    // Kotlin's compiler infers all type parameters.
    val gamepadyn = Gamepadyn.create(
        // our user-defined actions
        ActionDigital::class,
        ActionAnalog1::class,
        ActionAnalog2::class,
        // The FTC backend, constructed with a reference to this OpMode
        InputBackendFtc(this)
    )

    fun doStuff() { /* do thing */ }

    override fun init() {
        // Set the configuration of the first player.
        gamepadyn.players[0].configuration = Configuration(
            // Bind the DO_STUFF action to FACE_RIGHT (B on XBOX, Circle on PlayStation, A on Nintendo).
            // This will set the state of the DO_STUFF action to the state of FACE_RIGHT.
            ActionBind(ActionDigital.DO_STUFF, RawInputDigital.FACE_RIGHT)
        )

        // whenever the state of DO_STUFF changes,
        // the code within the curly braces will be invoked.
        gamepadyn.addListener(ActionDigital.DO_STUFF) {
            // the "data" of the event is the new state,
            // so the code in the conditional will be run  if the new state is "true".
            // Basically, it runs the code when you press the button.
            if (it.data()) doStuff()
        }
    }

    override fun loop() {
        // Update the state
        gamepadyn.update()
    }
}

private class BindPipeTest : OpMode() {
    // Construct the initial Gamepadyn instance.
    // Kotlin's compiler infers all type parameters.
    val gamepadyn = Gamepadyn.create(
        // our user-defined actions
        ActionDigital::class,
        ActionAnalog1::class,
        ActionAnalog2::class,
        // The FTC backend, constructed with a reference to this OpMode
        InputBackendFtc(this)
    )

    override fun init() {

        BindPipe.builder(gamepadyn) {

            // the only thing that gets added to scope in receiver functions are non-static (aka. no inner classes)
            // as much as I struggle to write code in rust, it's right about now where I wish I had its enum types.
            BindPipe.BindPipeVec2(
                BindPipe.Join(
                    BindPipe.AddFloat(
                        BindPipe.InputStateFloat(RawInputAnalog1.TRIGGER_RIGHT),
                        BindPipe.MultiplyFloat(
                            RawInputAnalog1.TRIGGER_LEFT,
                            BindPipe.BindPipeFloat(-1f)
                        )
                    )
                )
            )

            digital(ActionAnalog2.MOVEMENT) {
                join(
                    add(
                        input(TRIGGER_RIGHT),
                        multiply(
                            input(TRIGGER_LEFT),
                            -1f
                        )
                    ),
                    add (
                        branch(
                            input(FACE_UP),
                            1f,
                            0f
                        ),
                        branch (
                            input(FACE_DOWN),
                            -1f,
                            0f
                        )
                    )
                )
            }
        }

        BindPipe.BindPipeVec2(
            BindPipe.Join(
                BindPipe.AddFloat(
                    BindPipe.InputStateFloat(RawInputAnalog1.TRIGGER_RIGHT),
                    BindPipe.MultiplyFloat(
                        RawInputAnalog1.TRIGGER_LEFT,
                        BindPipe.BindPipeFloat(-1f)
                    )
                )
            )
        )

    }


    override fun loop() {
        // Update the state
        gamepadyn.update()
    }
}



