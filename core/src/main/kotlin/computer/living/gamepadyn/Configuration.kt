package computer.living.gamepadyn

import kotlin.reflect.KClass
import kotlinx.serialization.Serializable

/**
 * A serializable control configuration for a player.
 * It's really just a collection of [Bind]s.
 */
class Configuration<TD, TA, TAA> private constructor()
        where TD : ActionEnumDigital,
              TA : ActionEnumAnalog1,
              TAA : ActionEnumAnalog2,
              TD : Enum<TD>,
              TA : Enum<TA>,
              TAA : Enum<TAA> {

    /**
     * @param config The configuration of the binds. You provide a lambda function that takes an instance of a builder class as its only parameter. It returns nothing; you just call functions to mutate the provided value.
     */
    constructor(config: BindPipeBuilder<TD, TA, TAA>.() -> Unit) : this() {
        val builder = BindPipeBuilder<TD, TA, TAA>()
        config(builder)
        digital = builder.digitalPipes
        analog1 = builder.analog1Pipes
        analog2 = builder.analog2Pipes
    }

    var digital: ArrayList<Bind<TD, BindPipeBool>> = arrayListOf()
    var analog1: ArrayList<Bind<TA, BindPipeFloat>> = arrayListOf()
    var analog2: ArrayList<Bind<TAA, BindPipeVector>> = arrayListOf()

    fun serialize(
        digitalClazz: KClass<TD>,
        analog1Clazz: KClass<TA>,
        analog2Clazz: KClass<TAA>
    ) {
//        config.binds
//        Class.forName()
    }

    data class LoadResult<TD, TA, TAA>(
        val digital: KClass<TD>,
        val analog1: KClass<TA>,
        val analog2: KClass<TAA>,
        val configuration: Configuration<TD, TA, TAA>
    ) where TD : ActionEnumDigital,
            TA : ActionEnumAnalog1,
            TAA : ActionEnumAnalog2,
            TD : Enum<TD>,
            TA : Enum<TA>,
            TAA : Enum<TAA>

    companion object {
        @Throws(Exception::class)
        fun deserialize(json: String): LoadResult<*, *, *> {
            TODO("implement, need a JSON parser among other things")
//            Class.forName("TODO fixme computer.living.idk")
        }
    }

}