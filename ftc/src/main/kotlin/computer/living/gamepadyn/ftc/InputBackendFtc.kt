package computer.living.gamepadyn.ftc

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.Gamepad
import computer.living.gamepadyn.InputBackend
import computer.living.gamepadyn.InputData
import computer.living.gamepadyn.InputDataAnalog1
import computer.living.gamepadyn.InputDataAnalog2
import computer.living.gamepadyn.InputDataDigital
import computer.living.gamepadyn.RawInput
import computer.living.gamepadyn.RawInputAnalog1
import computer.living.gamepadyn.RawInputAnalog2
import computer.living.gamepadyn.RawInputDigital
import java.util.UUID

/**
 * Pre-made FTC implementation of a Gamepadyn [InputBackend]
 */
class InputBackendFtc(private val opMode: OpMode) : InputBackend {

    private val gamepads: Array<RawGamepadFtc> = arrayOf(RawGamepadFtc(opMode.gamepad1), RawGamepadFtc(opMode.gamepad2))

    private var lastUpdateTime = opMode.time

    override fun getGamepads(): Array<out InputBackend.RawGamepad> {
        return gamepads.copyOf()
    }

    override fun hasUpdated(): Boolean = (lastUpdateTime == opMode.time)

    override fun update() {
        lastUpdateTime = opMode.time
    }

    class RawGamepadFtc(private val gamepad: Gamepad) : InputBackend.RawGamepad {
        @Suppress("MemberVisibilityCanBePrivate")
        internal val id: UUID = UUID.randomUUID()

        override fun getId(): UUID = this.id

        override fun getState(input: RawInputDigital): InputDataDigital = when (input) {
            RawInputDigital.FACE_DOWN          -> InputDataDigital(gamepad.a)
            RawInputDigital.FACE_A             -> InputDataDigital(gamepad.a)
            RawInputDigital.FACE_B             -> InputDataDigital(gamepad.b)
            RawInputDigital.FACE_RIGHT         -> InputDataDigital(gamepad.b)
            RawInputDigital.FACE_LEFT          -> InputDataDigital(gamepad.x)
            RawInputDigital.FACE_X             -> InputDataDigital(gamepad.x)
            RawInputDigital.FACE_UP            -> InputDataDigital(gamepad.y)
            RawInputDigital.FACE_Y             -> InputDataDigital(gamepad.y)
            RawInputDigital.FACE_CROSS         -> InputDataDigital(gamepad.cross)
            RawInputDigital.FACE_CIRCLE        -> InputDataDigital(gamepad.circle)
            RawInputDigital.FACE_SQUARE        -> InputDataDigital(gamepad.square)
            RawInputDigital.FACE_TRIANGLE      -> InputDataDigital(gamepad.triangle)
            RawInputDigital.BUMPER_LEFT        -> InputDataDigital(gamepad.left_bumper)
            RawInputDigital.BUMPER_RIGHT       -> InputDataDigital(gamepad.right_bumper)
            RawInputDigital.DPAD_UP            -> InputDataDigital(gamepad.dpad_up)
            RawInputDigital.DPAD_DOWN          -> InputDataDigital(gamepad.dpad_down)
            RawInputDigital.DPAD_LEFT          -> InputDataDigital(gamepad.dpad_left)
            RawInputDigital.DPAD_RIGHT         -> InputDataDigital(gamepad.dpad_right)
            RawInputDigital.STICK_LEFT_BUTTON  -> InputDataDigital(gamepad.left_stick_button)
            RawInputDigital.STICK_RIGHT_BUTTON -> InputDataDigital(gamepad.right_stick_button)
        }

        override fun getState(input: RawInputAnalog1): InputDataAnalog1 = when (input) {
            RawInputAnalog1.TRIGGER_LEFT       -> InputDataAnalog1(gamepad.left_trigger)
            RawInputAnalog1.TRIGGER_RIGHT      -> InputDataAnalog1(gamepad.right_trigger)
        }
        override fun getState(input: RawInputAnalog2): InputDataAnalog2 = when (input) {
            RawInputAnalog2.STICK_LEFT         -> InputDataAnalog2(-gamepad.left_stick_x, -gamepad.left_stick_y)
            RawInputAnalog2.STICK_RIGHT        -> InputDataAnalog2(-gamepad.right_stick_x, -gamepad.right_stick_y)
        }

        override fun getState(): Map<RawInput, InputData> {
            return mapOf(
                RawInputDigital.FACE_DOWN          to InputDataDigital(gamepad.a),
                RawInputDigital.FACE_A             to InputDataDigital(gamepad.a),
                RawInputDigital.FACE_B             to InputDataDigital(gamepad.b),
                RawInputDigital.FACE_RIGHT         to InputDataDigital(gamepad.b),
                RawInputDigital.FACE_LEFT          to InputDataDigital(gamepad.x),
                RawInputDigital.FACE_X             to InputDataDigital(gamepad.x),
                RawInputDigital.FACE_UP            to InputDataDigital(gamepad.y),
                RawInputDigital.FACE_Y             to InputDataDigital(gamepad.y),
                RawInputDigital.FACE_CROSS         to InputDataDigital(gamepad.cross),
                RawInputDigital.FACE_CIRCLE        to InputDataDigital(gamepad.circle),
                RawInputDigital.FACE_SQUARE        to InputDataDigital(gamepad.square),
                RawInputDigital.FACE_TRIANGLE      to InputDataDigital(gamepad.triangle),
                RawInputDigital.BUMPER_LEFT        to InputDataDigital(gamepad.left_bumper),
                RawInputDigital.BUMPER_RIGHT       to InputDataDigital(gamepad.right_bumper),
                RawInputDigital.DPAD_UP            to InputDataDigital(gamepad.dpad_up),
                RawInputDigital.DPAD_DOWN          to InputDataDigital(gamepad.dpad_down),
                RawInputDigital.DPAD_LEFT          to InputDataDigital(gamepad.dpad_left),
                RawInputDigital.DPAD_RIGHT         to InputDataDigital(gamepad.dpad_right),
                RawInputDigital.STICK_LEFT_BUTTON  to InputDataDigital(gamepad.left_stick_button),
                RawInputDigital.STICK_RIGHT_BUTTON to InputDataDigital(gamepad.right_stick_button),
                RawInputAnalog2.STICK_LEFT         to InputDataAnalog2(-gamepad.left_stick_x, -gamepad.left_stick_y),
                RawInputAnalog2.STICK_RIGHT        to InputDataAnalog2(-gamepad.right_stick_x, -gamepad.right_stick_y),
                RawInputAnalog1.TRIGGER_LEFT       to InputDataAnalog1(gamepad.left_trigger),
                RawInputAnalog1.TRIGGER_RIGHT      to InputDataAnalog1(gamepad.right_trigger)
            )
        }
    }

}

