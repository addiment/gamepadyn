package computer.living.gamepadyn

import kotlin.reflect.KClass
enum class InputType(
    val axes: Int,
    val kClass: KClass<*>,
    val jClass: Class<*>
){
    DIGITAL (0, InputDataDigital::class),
    ANALOG1 (1, InputDataAnalog1::class),
    ANALOG2 (2, InputDataAnalog2::class);

    constructor(axes: Int, kClass: KClass<*>) : this(axes, kClass, kClass.java)
}