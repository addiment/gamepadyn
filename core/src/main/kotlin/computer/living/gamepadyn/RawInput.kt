package computer.living.gamepadyn

import computer.living.gamepadyn.InputType.*

sealed interface RawInput {
    val type: InputType
}

/**
 * Represents the inputs of a controller, like buttons and joysticks.
 */
enum class RawInputDigital(override val type: InputType) : RawInput {

    /**
     * Generic face button (= A / Cross)
     */
    FACE_DOWN           (DIGITAL),
    /**
     * Generic face button (= B / Circle)
     */
    FACE_RIGHT          (DIGITAL),
    /**
     * Generic face button (= X / Square)
     */
    FACE_LEFT           (DIGITAL),
    /**
     * Generic face button (= Y / Triangle)
     */
    FACE_UP             (DIGITAL),

    BUMPER_LEFT         (DIGITAL),
    BUMPER_RIGHT        (DIGITAL),

    DPAD_UP             (DIGITAL),
    DPAD_DOWN           (DIGITAL),
    DPAD_LEFT           (DIGITAL),
    DPAD_RIGHT          (DIGITAL),

    STICK_LEFT_BUTTON   (DIGITAL),
    STICK_RIGHT_BUTTON  (DIGITAL),
    /**
     * Generic special button (= SHARE / VIEW)
     */
    SPECIAL_BACK        (DIGITAL), // Generic special button
    /**
     * Generic special button (= OPTIONS / MENU)
     */
    SPECIAL_START       (DIGITAL), // Generic special button
}

enum class RawInputAnalog1(override val type: InputType) : RawInput {
    /**
     * Digital triggers will be pseudo-analog, 0.0 or 1.0 based on their state.
     */
    TRIGGER_LEFT        (ANALOG1),
    /**
     * Digital triggers will be pseudo-analog, 0.0 or 1.0 based on their state.
     */
    TRIGGER_RIGHT       (ANALOG1)
}

/**
 * All analog inputs should be treated with +X as right and +Y as up.
 */
enum class RawInputAnalog2(override val type: InputType) : RawInput {
    STICK_LEFT          (ANALOG2),
    STICK_RIGHT         (ANALOG2),
}