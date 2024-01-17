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
        fun getState(input: RawInputDigital): InputDataDigital
        fun getState(input: RawInputAnalog1): InputDataAnalog1
        fun getState(input: RawInputAnalog2): InputDataAnalog2

        /**
         * Returns the state of all inputs as a map of RawInput to InputData.
         */
        fun getState(): Map<RawInput, InputData>

        // This was removed because we can't actually guarantee that any given implementation does this.
        // Also, the only reason anyone uses this library is for FTC, but the FTC API doesn't give us a way to do this in the way we wanted.
        // We could fake it, but in all honesty it's easier to assign configurations to indexes.
//        /**
//         * Returns an ID that MUST be unique to a specific gamepad. It MAY correspond to a physical device.
//         *
//         * IDs MAY be persistent between instances, but our primary implementation generates them randomly per-instance.
//         */
//        fun getId(): Long
    }

    /**
     * Returns all currently connected gamepads.
     *
     * The elements in the array MAY be the same between calls, but this is just a method
     */
    fun getGamepads(): Array<out RawGamepad>

    /**
     * Called as soon the Gamepadyn instance runs [Gamepadyn.update] (but ONLY AFTER calling [hasUpdated] and ONLY IF it returns true)
     * This function is public, so it is possible for it to be called when it isn't supposed to.
     * Use this as a soft check for [hasUpdated].
     */
    fun update(): Unit = Unit

    /**
     * Returns the time (in SECONDS) since [hasUpdated] changed to true (aka. a "proper" update)
     */
    fun getDelta(): Double

    /**
     * Returns whether or not any changes in state have occurred since the last call to [Gamepadyn.update].
     * This function exists to "rate limit" event callbacks if the instance is updated multiple times per "frame."
     * If this returns `true`, the Gamepadyn instance calling it will call [update] then precede to update itself.
     */
    fun hasUpdated(): Boolean = false
}