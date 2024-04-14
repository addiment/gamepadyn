package computer.living.gamepadyn

import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sqrt

/**
 * A serializable control configuration for a player. In reality, it's really just a collection of [Bind]s,
 * @param config The configuration of the binds. You provide a lambda function that takes an instance of a builder class as its only parameter. It returns nothing; you just call functions to mutate the provided value.
 */
class Configuration<TD, TA, TAA>(config: BindPipeBuilder<TD, TA, TAA>.() -> Unit)
        where TD : ActionEnumDigital,
              TA : ActionEnumAnalog1,
              TAA : ActionEnumAnalog2,
              TD : Enum<TD>,
              TA : Enum<TA>,
              TAA : Enum<TAA> {

    var digital: ArrayList<Bind<TD, BindPipeBool>>
    var analog1: ArrayList<Bind<TA, BindPipeFloat>>
    var analog2: ArrayList<Bind<TAA, BindPipeVector>>

    init {
        val builder = BindPipeBuilder<TD, TA, TAA>()
        builder.config()
        digital = builder.digitalPipes
        analog1 = builder.analog1Pipes
        analog2 = builder.analog2Pipes
    }
}