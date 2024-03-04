//@file:JvmName("Serializer")
package computer.living.gamepadyn
////import kotlin.reflect
//
//// as much as I would like to have an "official" way of serializing configs,
//// the current bind system is a bottleneck.
//
///**
// * 100% experimental. Seriously, don't use this.
// */
//inline fun <reified TD, reified TA, reified TAA> serializeConfig(config: Configuration<TD, TA, TAA>)
//        where TD : ActionEnumDigital,
//              TA : ActionEnumAnalog1,
//              TAA : ActionEnumAnalog2,
//              TD : Enum<TD>,
//              TA : Enum<TA>,
//              TAA : Enum<TAA>
//{
//    config.binds
////    Class.forName()
//}