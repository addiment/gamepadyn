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
import computer.living.gamepadyn.RawInputAnalog2
import computer.living.gamepadyn.RawInputDigital
import java.util.UUID

/**
 * Pre-made FTC implementation of a Gamepadyn [InputBackend]
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
//                opMode.hardwareMap.appContext
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

    class RawGamepadFtc(internal val gamepad: Gamepad?) : InputBackend.RawGamepad {
        @Suppress("MemberVisibilityCanBePrivate")
//        internal val id: UUID = UUID.randomUUID()

//        override fun getId(): UUID = this.id

        override fun getState(input: RawInputDigital): InputDataDigital = if (gamepad != null) {
            when (input) {
                RawInputDigital.FACE_DOWN,
                /*RawInputDigital.FACE_A,
                RawInputDigital.FACE_CROSS*/        -> InputDataDigital(gamepad.a /*.cross*/)

                RawInputDigital.FACE_RIGHT,
                /*RawInputDigital.FACE_B,
                RawInputDigital.FACE_CIRCLE*/       -> InputDataDigital(gamepad.b /*.circle*/)

                RawInputDigital.FACE_LEFT,
                /*RawInputDigital.FACE_X,
                RawInputDigital.FACE_SQUARE*/       -> InputDataDigital(gamepad.x /*.square*/)

                RawInputDigital.FACE_UP,
                /*RawInputDigital.FACE_Y,
                RawInputDigital.FACE_TRIANGLE*/     -> InputDataDigital(gamepad.y /*.triangle*/)

                RawInputDigital.BUMPER_LEFT         -> InputDataDigital(gamepad.left_bumper)
                RawInputDigital.BUMPER_RIGHT        -> InputDataDigital(gamepad.right_bumper)

                RawInputDigital.DPAD_UP             -> InputDataDigital(gamepad.dpad_up)
                RawInputDigital.DPAD_DOWN           -> InputDataDigital(gamepad.dpad_down)
                RawInputDigital.DPAD_LEFT           -> InputDataDigital(gamepad.dpad_left)
                RawInputDigital.DPAD_RIGHT          -> InputDataDigital(gamepad.dpad_right)

                RawInputDigital.STICK_LEFT_BUTTON   -> InputDataDigital(gamepad.left_stick_button)
                RawInputDigital.STICK_RIGHT_BUTTON  -> InputDataDigital(gamepad.right_stick_button)

//                RawInputDigital.SPECIAL_LEFT        -> InputDataDigital(gamepad.back)
                RawInputDigital.SPECIAL_BACK        -> InputDataDigital(gamepad.back)
                RawInputDigital.SPECIAL_START       -> InputDataDigital(gamepad.start)
//                RawInputDigital.SPECIAL_OPTIONS     -> InputDataDigital(gamepad.back /*.options*/)
            }
        } else InputDataDigital()

        override fun getState(input: RawInputAnalog1): InputDataAnalog1 = if (gamepad != null) {
            when (input) {
                RawInputAnalog1.TRIGGER_LEFT        -> InputDataAnalog1(gamepad.left_trigger)
                RawInputAnalog1.TRIGGER_RIGHT       -> InputDataAnalog1(gamepad.right_trigger)
            }
        } else InputDataAnalog1()

        override fun getState(input: RawInputAnalog2): InputDataAnalog2 = if (gamepad != null) {
            when (input) {
                RawInputAnalog2.STICK_LEFT -> InputDataAnalog2(
                    gamepad.left_stick_x,
                    if (-gamepad.left_stick_y == -0f) 0f else -gamepad.left_stick_y
                )
                RawInputAnalog2.STICK_RIGHT -> InputDataAnalog2(
                    gamepad.right_stick_x,
                    if (-gamepad.right_stick_y == -0f) 0f else -gamepad.right_stick_y
                )
            }
        } else InputDataAnalog2()

        override fun getState(): Map<RawInput, InputData> = if (gamepad != null)
            mapOf(
                RawInputDigital.FACE_DOWN           to InputDataDigital(gamepad.a),
//                RawInputDigital.FACE_A              to InputDataDigital(gamepad.a),
//                RawInputDigital.FACE_CROSS          to InputDataDigital(gamepad.a),

                RawInputDigital.FACE_RIGHT          to InputDataDigital(gamepad.b),
//                RawInputDigital.FACE_B              to InputDataDigital(gamepad.b),
//                RawInputDigital.FACE_CIRCLE         to InputDataDigital(gamepad.b),

                RawInputDigital.FACE_LEFT           to InputDataDigital(gamepad.x),
//                RawInputDigital.FACE_X              to InputDataDigital(gamepad.x),
//                RawInputDigital.FACE_SQUARE         to InputDataDigital(gamepad.x),

                RawInputDigital.FACE_UP             to InputDataDigital(gamepad.y),
//                RawInputDigital.FACE_TRIANGLE       to InputDataDigital(gamepad.y),
//                RawInputDigital.FACE_Y              to InputDataDigital(gamepad.y),

                RawInputDigital.BUMPER_LEFT         to InputDataDigital(gamepad.left_bumper),
                RawInputDigital.BUMPER_RIGHT        to InputDataDigital(gamepad.right_bumper),

                RawInputDigital.DPAD_UP             to InputDataDigital(gamepad.dpad_up),
                RawInputDigital.DPAD_DOWN           to InputDataDigital(gamepad.dpad_down),
                RawInputDigital.DPAD_LEFT           to InputDataDigital(gamepad.dpad_left),
                RawInputDigital.DPAD_RIGHT          to InputDataDigital(gamepad.dpad_right),

                RawInputDigital.STICK_LEFT_BUTTON   to InputDataDigital(gamepad.left_stick_button),
                RawInputDigital.STICK_RIGHT_BUTTON  to InputDataDigital(gamepad.right_stick_button),

//                RawInputDigital.SPECIAL_LEFT        to InputDataDigital(gamepad.back),
                RawInputDigital.SPECIAL_BACK        to InputDataDigital(gamepad.back),
                RawInputDigital.SPECIAL_START       to InputDataDigital(gamepad.start),
//                RawInputDigital.SPECIAL_OPTIONS     to InputDataDigital(gamepad.back),

                RawInputAnalog2.STICK_LEFT          to InputDataAnalog2(
                    gamepad.left_stick_x,
                    if (-gamepad.left_stick_y == -0f) 0f else -gamepad.left_stick_y
                ),
                RawInputAnalog2.STICK_RIGHT         to InputDataAnalog2(
                    gamepad.right_stick_x,
                    if (-gamepad.right_stick_y == -0f) 0f else -gamepad.right_stick_y
                ),

                RawInputAnalog1.TRIGGER_LEFT        to InputDataAnalog1(gamepad.left_trigger),
                RawInputAnalog1.TRIGGER_RIGHT       to InputDataAnalog1(gamepad.right_trigger)
            )
        else mapOf(
            RawInputDigital.FACE_DOWN               to InputDataDigital(),
//            RawInputDigital.FACE_A                  to InputDataDigital(),
//            RawInputDigital.FACE_CROSS              to InputDataDigital(),

            RawInputDigital.FACE_RIGHT              to InputDataDigital(),
//            RawInputDigital.FACE_B                  to InputDataDigital(),
//            RawInputDigital.FACE_CIRCLE             to InputDataDigital(),

            RawInputDigital.FACE_LEFT               to InputDataDigital(),
//            RawInputDigital.FACE_X                  to InputDataDigital(),
//            RawInputDigital.FACE_SQUARE             to InputDataDigital(),

            RawInputDigital.FACE_UP                 to InputDataDigital(),
//            RawInputDigital.FACE_TRIANGLE           to InputDataDigital(),
//            RawInputDigital.FACE_Y                  to InputDataDigital(),

            RawInputDigital.BUMPER_LEFT             to InputDataDigital(),
            RawInputDigital.BUMPER_RIGHT            to InputDataDigital(),

            RawInputDigital.DPAD_UP                 to InputDataDigital(),
            RawInputDigital.DPAD_DOWN               to InputDataDigital(),
            RawInputDigital.DPAD_LEFT               to InputDataDigital(),
            RawInputDigital.DPAD_RIGHT              to InputDataDigital(),

            RawInputDigital.STICK_LEFT_BUTTON       to InputDataDigital(),
            RawInputDigital.STICK_RIGHT_BUTTON      to InputDataDigital(),

//            RawInputDigital.SPECIAL_LEFT            to InputDataDigital(),
            RawInputDigital.SPECIAL_BACK            to InputDataDigital(),
            RawInputDigital.SPECIAL_START           to InputDataDigital(),
//            RawInputDigital.SPECIAL_OPTIONS         to InputDataDigital(),

            RawInputAnalog2.STICK_LEFT              to InputDataAnalog2(),
            RawInputAnalog2.STICK_RIGHT             to InputDataAnalog2(),

            RawInputAnalog1.TRIGGER_LEFT            to InputDataAnalog1(),
            RawInputAnalog1.TRIGGER_RIGHT           to InputDataAnalog1()
        )
    }
}

