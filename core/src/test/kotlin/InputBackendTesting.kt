import computer.living.gamepadyn.InputBackend
import computer.living.gamepadyn.InputData
import computer.living.gamepadyn.InputDataAnalog1
import computer.living.gamepadyn.InputDataAnalog2
import computer.living.gamepadyn.InputDataDigital
import computer.living.gamepadyn.RawInput
import computer.living.gamepadyn.RawInputDigital
import computer.living.gamepadyn.RawInputAnalog1
import computer.living.gamepadyn.RawInputAnalog2
import java.util.UUID

class InputBackendTesting : InputBackend {

    companion object {
        var manipulableStateDigital: Boolean = false
        var manipulableStateAnalog1d: Float = 0f
        var manipulableStateAnalog2d: Pair<Float, Float> = Pair(0f, 0f)
    }

    override fun getDelta(): Double {
        return 1.0
    }

    class RawGamepadTesting: InputBackend.RawGamepad {

//        @Suppress("MemberVisibilityCanBePrivate")
//        internal val id: UUID = UUID.randomUUID()

        override fun getState(input: RawInputDigital): InputDataDigital = InputDataDigital(manipulableStateDigital)

        override fun getState(input: RawInputAnalog1): InputDataAnalog1 = InputDataAnalog1(manipulableStateAnalog1d)

        override fun getState(input: RawInputAnalog2): InputDataAnalog2 = InputDataAnalog2(manipulableStateAnalog2d.first, manipulableStateAnalog2d.second)

        override fun getState(): Map<RawInput, InputData> = mapOf(
            RawInputDigital.FACE_DOWN           to InputDataDigital(manipulableStateDigital),
            RawInputDigital.FACE_RIGHT          to InputDataDigital(manipulableStateDigital),
            RawInputDigital.FACE_LEFT           to InputDataDigital(manipulableStateDigital),
            RawInputDigital.FACE_UP             to InputDataDigital(manipulableStateDigital),

            RawInputDigital.BUMPER_LEFT         to InputDataDigital(manipulableStateDigital),
            RawInputDigital.BUMPER_RIGHT        to InputDataDigital(manipulableStateDigital),
            RawInputDigital.DPAD_UP             to InputDataDigital(manipulableStateDigital),
            RawInputDigital.DPAD_DOWN           to InputDataDigital(manipulableStateDigital),
            RawInputDigital.DPAD_LEFT           to InputDataDigital(manipulableStateDigital),
            RawInputDigital.DPAD_RIGHT          to InputDataDigital(manipulableStateDigital),
            RawInputDigital.STICK_LEFT_BUTTON   to InputDataDigital(manipulableStateDigital),
            RawInputDigital.STICK_RIGHT_BUTTON  to InputDataDigital(manipulableStateDigital),

            RawInputAnalog2.STICK_LEFT          to InputDataAnalog2(manipulableStateAnalog2d.first, manipulableStateAnalog2d.second),
            RawInputAnalog2.STICK_RIGHT         to InputDataAnalog2(manipulableStateAnalog2d.first, manipulableStateAnalog2d.second),

            RawInputAnalog1.TRIGGER_LEFT        to InputDataAnalog1(manipulableStateAnalog1d),
            RawInputAnalog1.TRIGGER_RIGHT       to InputDataAnalog1(manipulableStateAnalog1d)
        )

    }

    private val gamepads: Array<RawGamepadTesting> = arrayOf(RawGamepadTesting())

    override fun getGamepads(): Array<out InputBackend.RawGamepad> {
        return gamepads.copyOf()
    }

}

