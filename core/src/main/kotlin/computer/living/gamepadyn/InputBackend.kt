package computer.living.gamepadyn

import java.util.UUID

/**
 * Platform implementation of Gamepadyn's backend.
 *
 * FTC programmers should not use this class directly.
 * Instead, use [computer.living.gamepadyn.ftc.InputBackendFtc].
 *
 * Due to the heavy expectations put upon users of the interface,
 * the comments in this class are mostly written with
 * [RFC requirement levels](https://www.ietf.org/rfc/rfc2119.txt)
 * to attempt to reduce confusion.
 */
interface InputBackend {

    /**
     * Represents a gamepad/controller.
     *
     * FTC programmers SHOULD NOT use this class directly.
     * Instead, use [computer.living.gamepadyn.ftc.InputBackendFtc.RawGamepadFtc].
     */
    interface RawGamepad {
        /**
         * This function SHOULD return the current known state of the input referred
         * to by the [input] parameter. If this is impossible,
         * this function MUST return the last known input state,
         * and default to `InputDataDigital(false)` if the state has never been known.
         */
        fun getState(input: RawInputDigital): InputDataDigital
        /**
         * If possible, this function MUST return the current known state of the input referred
         * to by the [input] parameter. If this is impossible,
         * this function MUST return the last known input state,
         * and default to `InputDataDigital(false)` if the state has never been known.
         */
        fun getState(input: RawInputAnalog1): InputDataAnalog1
        /**
         * This function SHOULD return the current known state of the input referred
         * to by the [input] parameter. If this is impossible,
         * this function MUST return the last known input state,
         * and default to `InputDataDigital(false)` if the state has never been known.
         */
        fun getState(input: RawInputAnalog2): InputDataAnalog2

        /**
         * MUST return the last known state of all raw inputs as a map of [RawInput]s to [InputData].
         */
        fun getState(): Map<RawInput, InputData>

        // This was removed because we can't actually guarantee that any given implementation does this.
        // Also, the only real reason anyone uses this library is for FTC,
        // but the FTC API doesn't give us a way to do this in the way we wanted.
        // We could fake it, but in all honesty it's easier to assign configurations to indexes.
//        /**
//         * Returns an ID that MUST be unique to a specific gamepad. It MAY correspond to a physical device.
//         *
//         * IDs MAY be persistent between instances, but our primary implementation generates them randomly per-instance.
//         */
//        fun getId(): Long
    }

    /**
     * This function SHOULD return all currently connected gamepads.
     * If impossible, this function MUST return the last known gamepads,
     * or an empty array if the last known state had no gamepads connected.
     *
     * The elements in the array SHOULD be the same between calls.
     */
    fun getGamepads(): Array<out RawGamepad>

    /**
     * Called by the Gamepadyn instance inside of [Gamepadyn.update]
     * after it has first checked [hasUpdated] (only if it returns true) to signify
     * that it is about to process data from the backend.
     */
    fun update(): Unit = Unit

    // TODO: turn into an abstract getter?
    /**
     * MUST return the time (in **seconds**) since [hasUpdated] was changed to true (aka. a "proper" update)
     */
    fun getDelta(): Double

    // TODO: turn into an abstract getter?
    /**
     * MUST return whether or not any known changes in state have occurred since the
     * last call to [Gamepadyn.update], or `false` if this functionality has not been implemented
     * (or is impossible to implement).
     *
     *
     * This function exists to "rate limit" event callbacks if the instance
     * is updated multiple times per "frame." If this function returns `true`,
     * the Gamepadyn instance calling it SHOULD call [update] then precede to update itself.
     */
    fun hasUpdated(): Boolean = false
}