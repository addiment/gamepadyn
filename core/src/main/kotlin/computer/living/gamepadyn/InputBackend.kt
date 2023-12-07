package computer.living.gamepadyn

import java.util.UUID

/**
 * Platform implementation of Gamepadyn's backend.
 *
 * FTC programmers should not use this class directly.
 * Instead, use [computer.living.gamepadyn.ftc.InputBackendFtc].
 *
 * Due to the heavy expectations put upon users of the interface,
 * the comments in this class are written with [RFC requirement levels](https://www.ietf.org/rfc/rfc2119.txt)
 * to reduce confusion.
 */
interface InputBackend {

    /**
     * Represents a gamepad/controller.
     *
     * FTC programmers should not use this class directly.
     * Instead, use [computer.living.gamepadyn.ftc.InputBackendFtc.RawGamepadFtc].
     */
    interface RawGamepad {
        /**
         * Returns the state of a specific input. The return type MUST match the expected [InputDescriptor] corresponding to the [RawInput] provided to the function.
         */
        fun getState(input: RawInput): InputData

        /**
         * Returns an ID that MUST be unique to a specific gamepad. It MAY correspond to a physical device.
         *
         * IDs MAY be persistent between instances, but our implementation generates them randomly per-instance.
         */
        fun getId(): UUID
    }

    /**
     * Returns all currently connected gamepads.
     *
     * The elements in the array SHOULD be the same between calls
     * (but if they aren't, you can use [RawGamepad.getId] for verification)
     */
    fun getGamepads(): Array<out RawGamepad>
}