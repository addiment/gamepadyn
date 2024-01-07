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
     * Generic face button (= A / CROSS)
     */
    FACE_DOWN           (DIGITAL),
    /**
     * Generic face button (= B / CIRCLE)
     */
    FACE_RIGHT          (DIGITAL),
    /**
     * Generic face button (= X / SQUARE)
     */
    FACE_LEFT           (DIGITAL),
    /**
     * Generic face button (= Y / TRIANGLE)
     */
    FACE_UP             (DIGITAL),

//    FACE_A              (DIGITAL), // XB face button (= DOWN)
//    FACE_B              (DIGITAL), // XB face button (= RIGHT)
//    FACE_X              (DIGITAL), // XB face button (= LEFT)
//    FACE_Y              (DIGITAL), // XB face button (= UP)
//
//    FACE_CROSS          (DIGITAL), // PS face button (= A)
//    FACE_CIRCLE         (DIGITAL), // PS face button (= B)
//    FACE_SQUARE         (DIGITAL), // PS face button (= X)
//    FACE_TRIANGLE       (DIGITAL), // PS face button (= Y)

    BUMPER_LEFT         (DIGITAL),
    BUMPER_RIGHT        (DIGITAL),

    DPAD_UP             (DIGITAL),
    DPAD_DOWN           (DIGITAL),
    DPAD_LEFT           (DIGITAL),
    DPAD_RIGHT          (DIGITAL),

    STICK_LEFT_BUTTON   (DIGITAL),
    STICK_RIGHT_BUTTON  (DIGITAL),
    /**
     * Generic special button (= SHARE)
     */
    SPECIAL_BACK        (DIGITAL), // Generic special button
//    SPECIAL_VIEW        (DIGITAL), // XB special button
//    SPECIAL_SHARE       (DIGITAL), // PS special button (= BACK)
    /**
     * Generic special button (= OPTIONS)
     */
    SPECIAL_START       (DIGITAL), // Generic special button
//    SPECIAL_MENU        (DIGITAL), // XB special button
//    SPECIAL_OPTIONS     (DIGITAL), // PS special button (= BACK)
}

enum class RawInputAnalog1(override val type: InputType) : RawInput {
    TRIGGER_LEFT        (ANALOG1),
    TRIGGER_RIGHT       (ANALOG1)
}

enum class RawInputAnalog2(override val type: InputType) : RawInput {
    STICK_LEFT          (ANALOG2),
    STICK_RIGHT         (ANALOG2),
}