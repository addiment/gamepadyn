import computer.living.gamepadyn.InputBackend
import computer.living.gamepadyn.InputData
import computer.living.gamepadyn.InputDataAnalog
import computer.living.gamepadyn.InputDataDigital
import computer.living.gamepadyn.RawInput
import java.util.UUID

class InputBackendTesting : InputBackend {

    companion object {
        var manipulableStateDigital: Boolean = false
        var manipulableStateAnalog1d: Float = 0f
        var manipulableStateAnalog2d: Pair<Float, Float> = Pair(0f, 0f)
    }

    class RawGamepadTesting: InputBackend.RawGamepad {

        @Suppress("MemberVisibilityCanBePrivate")
        internal val id: UUID = UUID.randomUUID()

        override fun getState(input: RawInput): InputData = when (input) {
            RawInput.FACE_DOWN -> InputDataDigital(manipulableStateDigital)
            RawInput.FACE_A -> InputDataDigital(manipulableStateDigital)
            RawInput.FACE_B -> InputDataDigital(manipulableStateDigital)
            RawInput.FACE_RIGHT -> InputDataDigital(manipulableStateDigital)
            RawInput.FACE_LEFT -> InputDataDigital(manipulableStateDigital)
            RawInput.FACE_X -> InputDataDigital(manipulableStateDigital)
            RawInput.FACE_UP -> InputDataDigital(manipulableStateDigital)
            RawInput.FACE_Y -> InputDataDigital(manipulableStateDigital)
            RawInput.FACE_CROSS -> InputDataDigital(manipulableStateDigital)
            RawInput.FACE_CIRCLE -> InputDataDigital(manipulableStateDigital)
            RawInput.FACE_SQUARE -> InputDataDigital(manipulableStateDigital)
            RawInput.FACE_TRIANGLE -> InputDataDigital(manipulableStateDigital)
            RawInput.BUMPER_LEFT -> InputDataDigital(manipulableStateDigital)
            RawInput.BUMPER_RIGHT -> InputDataDigital(manipulableStateDigital)
            RawInput.DPAD_UP -> InputDataDigital(manipulableStateDigital)
            RawInput.DPAD_DOWN -> InputDataDigital(manipulableStateDigital)
            RawInput.DPAD_LEFT -> InputDataDigital(manipulableStateDigital)
            RawInput.DPAD_RIGHT -> InputDataDigital(manipulableStateDigital)
            RawInput.STICK_LEFT_BUTTON -> InputDataDigital(manipulableStateDigital)
            RawInput.STICK_RIGHT_BUTTON -> InputDataDigital(manipulableStateDigital)
            RawInput.STICK_LEFT -> InputDataAnalog(manipulableStateAnalog2d.first, manipulableStateAnalog2d.second)
            RawInput.STICK_RIGHT -> InputDataAnalog(manipulableStateAnalog2d.first, manipulableStateAnalog2d.second)
            RawInput.TRIGGER_LEFT -> InputDataAnalog(manipulableStateAnalog1d)
            RawInput.TRIGGER_RIGHT -> InputDataAnalog(manipulableStateAnalog1d)
        }

        override fun getId(): UUID = this.id
    }

    private val gamepads: Array<RawGamepadTesting> = arrayOf(RawGamepadTesting())

    override fun getGamepads(): Array<out InputBackend.RawGamepad> {
        return gamepads.copyOf()
    }

}

