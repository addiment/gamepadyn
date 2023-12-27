package computer.living.gamepadyn

import kotlin.reflect.KClass
enum class InputType(
    val kClass: KClass<*>,
    val jClass: Class<*>
){
    DIGITAL (InputDataDigital::class),
    ANALOG1 (InputDataAnalog1::class),
    ANALOG2 (InputDataAnalog2::class);

    constructor(kClass: KClass<*>) : this(kClass, kClass.java)
}