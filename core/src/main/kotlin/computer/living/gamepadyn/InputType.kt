package computer.living.gamepadyn

import kotlin.reflect.KClass

sealed class InputType(
    val kClass: KClass<*>,
    val jClass: Class<*>
) {
    constructor(kClass: KClass<*>) : this(kClass, kClass.java)
}
class Digital : InputType(InputDataDigital::class)
class Analog1 : InputType(InputDataAnalog1::class)
class Analog2 : InputType(InputDataAnalog2::class)
class Analog3 : InputType(InputDataAnalog3::class)

enum class InputTypeEnum(
    val kClass: KClass<*>,
    val jClass: Class<*>
){
    DIGITAL (Digital::class),
    ANALOG1 (Analog1::class),
    ANALOG2 (Analog2::class),
    ANALOG3 (Analog3::class);

    constructor(kClass: KClass<*>) : this(kClass, kClass.java)
}