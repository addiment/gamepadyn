package computer.living.gamepadyn.ftc

import android.util.Log
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.Gamepad
import computer.living.gamepadyn.InputBackend
import computer.living.gamepadyn.InputData
import computer.living.gamepadyn.InputDataAnalog1
import computer.living.gamepadyn.InputDataAnalog2
import computer.living.gamepadyn.InputDataDigital
import computer.living.gamepadyn.RawInput
import computer.living.gamepadyn.RawInputAnalog1
import computer.living.gamepadyn.RawInputAnalog1.*
import computer.living.gamepadyn.RawInputAnalog2
import computer.living.gamepadyn.RawInputAnalog2.*
import computer.living.gamepadyn.RawInputDigital
import computer.living.gamepadyn.RawInputDigital.*
import java.util.UUID

/**
 * Pre-made FTC implementation of a Gamepadyn [InputBackend].
 */
class InputBackendFtc(private val opMode: OpMode) : InputBackend {

    companion object {
        const val LOG_TAG = "Gamepadyn (FTC Backend)"
    }

    private var gamepads: Array<RawGamepadFtc> = arrayOf(RawGamepadFtc(null), RawGamepadFtc(null))

    private var lastUpdateTime = opMode.time
    private var delta = 0.0

    override fun getGamepads(): Array<out InputBackend.RawGamepad> {
        if (gamepads[0].gamepad == null || gamepads[1].gamepad == null) {
            try {
                val gp0: Gamepad? = opMode.gamepad1
                val gp1: Gamepad? = opMode.gamepad2
                gamepads = arrayOf(RawGamepadFtc(gp0), RawGamepadFtc(gp1))
            } catch (e: Exception) {
                // yeah, nothing we can really do about it ¯\_(￣▽￣)_/¯
                Log.w(LOG_TAG, "Tried to get access to opMode gamepads, but an exception was thrown (have we initialized yet?)")
            }
        }
        return gamepads.copyOf()
    }

    override fun hasUpdated(): Boolean = (lastUpdateTime == opMode.time)

    override fun update() {
        delta = opMode.time - lastUpdateTime
        lastUpdateTime = opMode.time
    }

    override fun getDelta(): Double {
        return delta
    }

    /**
     * Major differences between Gamepadyn's core control layout and the FTC gamepad access API:
     * 1. joystick axes are corrected to what every other input system on the planet earth uses (aka. THE MATHEMATICALLY STANDARD ONE)
     * 2. face buttons are referred to by their position on the controller rather than their label (some FTC-legal controllers don't even have standard labels!)
     */
    class RawGamepadFtc(internal val gamepad: Gamepad?) : InputBackend.RawGamepad {
        override fun getState(input: RawInputDigital): InputDataDigital = if (gamepad != null) {
            when (input) {
                FACE_DOWN           -> InputDataDigital(gamepad.a)
                FACE_RIGHT          -> InputDataDigital(gamepad.b)
                FACE_LEFT           -> InputDataDigital(gamepad.x)
                FACE_UP             -> InputDataDigital(gamepad.y)

                BUMPER_LEFT         -> InputDataDigital(gamepad.left_bumper)
                BUMPER_RIGHT        -> InputDataDigital(gamepad.right_bumper)

                DPAD_UP             -> InputDataDigital(gamepad.dpad_up)
                DPAD_DOWN           -> InputDataDigital(gamepad.dpad_down)
                DPAD_LEFT           -> InputDataDigital(gamepad.dpad_left)
                DPAD_RIGHT          -> InputDataDigital(gamepad.dpad_right)

                STICK_LEFT_BUTTON   -> InputDataDigital(gamepad.left_stick_button)
                STICK_RIGHT_BUTTON  -> InputDataDigital(gamepad.right_stick_button)

                SPECIAL_BACK        -> InputDataDigital(gamepad.back)
                SPECIAL_START       -> InputDataDigital(gamepad.start)
            }
        } else InputDataDigital()

        override fun getState(input: RawInputAnalog1): InputDataAnalog1 = if (gamepad != null) {
            when (input) {
                TRIGGER_LEFT        -> InputDataAnalog1(gamepad.left_trigger)
                TRIGGER_RIGHT       -> InputDataAnalog1(gamepad.right_trigger)
            }
        } else InputDataAnalog1()

        override fun getState(input: RawInputAnalog2): InputDataAnalog2 = if (gamepad != null) {
            when (input) {
                STICK_LEFT -> InputDataAnalog2(
                    gamepad.left_stick_x,
                    if (-gamepad.left_stick_y == -0f) 0f else -gamepad.left_stick_y
                )
                STICK_RIGHT -> InputDataAnalog2(
                    gamepad.right_stick_x,
                    if (-gamepad.right_stick_y == -0f) 0f else -gamepad.right_stick_y
                )
            }
        } else InputDataAnalog2()

        override fun getState(): Map<RawInput, InputData> = if (gamepad != null)
            mapOf(
                FACE_DOWN           to InputDataDigital(gamepad.a),
                FACE_RIGHT          to InputDataDigital(gamepad.b),
                FACE_LEFT           to InputDataDigital(gamepad.x),
                FACE_UP             to InputDataDigital(gamepad.y),

                BUMPER_LEFT         to InputDataDigital(gamepad.left_bumper),
                BUMPER_RIGHT        to InputDataDigital(gamepad.right_bumper),

                DPAD_UP             to InputDataDigital(gamepad.dpad_up),
                DPAD_DOWN           to InputDataDigital(gamepad.dpad_down),
                DPAD_LEFT           to InputDataDigital(gamepad.dpad_left),
                DPAD_RIGHT          to InputDataDigital(gamepad.dpad_right),

                STICK_LEFT_BUTTON   to InputDataDigital(gamepad.left_stick_button),
                STICK_RIGHT_BUTTON  to InputDataDigital(gamepad.right_stick_button),

                SPECIAL_BACK        to InputDataDigital(gamepad.back),
                SPECIAL_START       to InputDataDigital(gamepad.start),

                TRIGGER_LEFT        to InputDataAnalog1(gamepad.left_trigger),
                TRIGGER_RIGHT       to InputDataAnalog1(gamepad.right_trigger),

                STICK_LEFT          to InputDataAnalog2(
                    gamepad.left_stick_x,
                    if (-gamepad.left_stick_y == -0f) 0f else -gamepad.left_stick_y
                ),
                STICK_RIGHT         to InputDataAnalog2(
                    gamepad.right_stick_x,
                    if (-gamepad.right_stick_y == -0f) 0f else -gamepad.right_stick_y
                )
            )
        else mapOf(
            FACE_DOWN               to InputDataDigital(),
            FACE_RIGHT              to InputDataDigital(),
            FACE_LEFT               to InputDataDigital(),
            FACE_UP                 to InputDataDigital(),

            BUMPER_LEFT             to InputDataDigital(),
            BUMPER_RIGHT            to InputDataDigital(),

            DPAD_UP                 to InputDataDigital(),
            DPAD_DOWN               to InputDataDigital(),
            DPAD_LEFT               to InputDataDigital(),
            DPAD_RIGHT              to InputDataDigital(),

            STICK_LEFT_BUTTON       to InputDataDigital(),
            STICK_RIGHT_BUTTON      to InputDataDigital(),

            SPECIAL_BACK            to InputDataDigital(),
            SPECIAL_START           to InputDataDigital(),

            TRIGGER_LEFT            to InputDataAnalog1(),
            TRIGGER_RIGHT           to InputDataAnalog1(),

            STICK_LEFT              to InputDataAnalog2(),
            STICK_RIGHT             to InputDataAnalog2(),
        )
    }
}

