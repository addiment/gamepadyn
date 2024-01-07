@file:Suppress("MemberVisibilityCanBePrivate")

package computer.living.gamepadyn

import java.util.function.Consumer

class Player<TD, TA, TAA> internal constructor(
    internal val parent: Gamepadyn<TD, TA, TAA>,
    internal var rawGamepad: InputBackend.RawGamepad
)
        where TD : ActionEnumDigital,
              TA : ActionEnumAnalog1,
              TAA : ActionEnumAnalog2,
              TD : Enum<TD>,
              TA : Enum<TA>,
              TAA : Enum<TAA>
{
    internal var stateDigital: MutableMap<TD,   InputDataDigital> = parent.actions.digitalActions.associateWith { InputDataDigital() }.toMutableMap()
    internal var stateAnalog1: MutableMap<TA,   InputDataAnalog1> = parent.actions.analog1Actions.associateWith { InputDataAnalog1() }.toMutableMap()
    internal var stateAnalog2: MutableMap<TAA,  InputDataAnalog2> = parent.actions.analog2Actions.associateWith { InputDataAnalog2() }.toMutableMap()

    internal var eventsDigital: Map<TD,   Event<InputDataDigital>> = parent.actions.digitalActions.associateWith { Event() }
    internal var eventsAnalog1: Map<TA,   Event<InputDataAnalog1>> = parent.actions.analog1Actions.associateWith { Event() }
    internal var eventsAnalog2: Map<TAA,  Event<InputDataAnalog2>> = parent.actions.analog2Actions.associateWith { Event() }

    internal var isEnabled: Boolean = true

    /**
     * The player's configuration.
     */
    var configuration: Configuration<TD, TA, TAA> = Configuration()

    /**
     * Gets a Digital Event
     */
    @JvmName("getEventDigital")
    fun getEvent(action: TD): Event<InputDataDigital>   = eventsDigital[action]
        .let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }
    /**
     * Gets an Analog1 Event
     */
    @JvmName("getEventAnalog1")
    fun getEvent(action: TA): Event<InputDataAnalog1>   = eventsAnalog1[action]
        .let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }
    /**
     * Gets an Analog2 Event
     */
    @JvmName("getEventAnalog2")
    fun getEvent(action: TAA): Event<InputDataAnalog2>  = eventsAnalog2[action]
        .let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }

    /**
     * Gets a Digital Event and also adds a Java listener
     */
    @JvmName("getEventDigital")
    fun getEvent(action: TD, listener: (InputDataDigital) -> Unit): Event<InputDataDigital>     = eventsDigital[action]
        .let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }
        .also { it.addListener(listener) }

    /**
     * Gets an Analog1 Event and also adds a listener
     */
    @JvmName("getEventAnalog1")
    fun getEvent(action: TA, listener: (InputDataAnalog1) -> Unit): Event<InputDataAnalog1>     = eventsAnalog1[action]
        .let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }
        .also { it.addListener(listener) }
    /**
     * Gets an Analog2 Event and also adds a listener
     */
    @JvmName("getEventAnalog2")
    fun getEvent(action: TAA, listener: (InputDataAnalog2) -> Unit): Event<InputDataAnalog2>    = eventsAnalog2[action]
        .let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }
        .also { it.addListener(listener) }

    /**
     * Gets a Digital Event and also adds a Java listener
     */
    @JvmName("getEventDigital")
    fun getEvent(action: TD, listener: Consumer<InputDataDigital>): Event<InputDataDigital>     = eventsDigital[action]
        .let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }
        .also { it.addListener(listener) }

    /**
     * Gets an Analog1 Event and also adds a Java event listener
     */
    @JvmName("getEventAnalog1")
    fun getEvent(action: TA, listener: Consumer<InputDataAnalog1>): Event<InputDataAnalog1>     = eventsAnalog1[action]
        .let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }
        .also { it.addListener(listener) }
    /**
     * Gets an Analog2 Event and also adds a Java event listener
     */
    @JvmName("getEventAnalog2")
    fun getEvent(action: TAA, listener: Consumer<InputDataAnalog2>): Event<InputDataAnalog2>    = eventsAnalog2[action]
        .let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }
        .also { it.addListener(listener) }

    /**
     * Returns the state of the digital action provided, or `null` for an invalid action (you can safely use `!!`)
     */
    @JvmName("getStateDigital")
    fun getState(action: TD): InputDataDigital     = stateDigital[action].let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries! ()") }
    /**
     * Returns the state of the 1D analog action provided, or `null` for an invalid action (you can safely use `!!`)
     */
    @JvmName("getStateAnalog1")
    fun getState(action: TA): InputDataAnalog1     = stateAnalog1[action].let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries! ()") }
    /**
     * Returns the state of the 2D analog action provided, or `null` for an invalid action (you can safely use `!!`)
     */
    @JvmName("getStateAnalog2")
    fun getState(action: TAA): InputDataAnalog2    = stateAnalog2[action].let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries! ()") }

    /*
     * NOTE: This API is... *unpleasant* in Java.
     * This is because Java's type erasure causes the getState and
     * getEvent functions to have the same signature.
     * Kotlin does magic to ensure that the functions have different signatures at compile-time.
     * In order to make it work for Java, we have to rename the functions (hence @JvmName).
     * As an example, the following Java sample does not compile:
     */

/*

public class GenericTest<
        TD extends Enum<TD> & ActionEnumDigital,
        TA extends Enum<TA> & ActionEnumAnalog1,
        TAA extends Enum<TAA> & ActionEnumAnalog2>
{
    Event<InputDataDigital> getEvent(TD action) { return new Event<>(); }
    Event<InputDataAnalog1> getEvent(TA action) { return new Event<>(); }
    Event<InputDataAnalog2> getEvent(TAA action) { return new Event<>(); }
}

*/


}