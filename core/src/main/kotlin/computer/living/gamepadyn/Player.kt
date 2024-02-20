@file:Suppress("MemberVisibilityCanBePrivate")

package computer.living.gamepadyn

/**
 * Represents a specific controller.
 */
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
    internal var stateDigital: MutableMap<TD,   InputDataDigital> = parent.actionsDigital.associateWith { InputDataDigital() }.toMutableMap()
    internal var stateAnalog1: MutableMap<TA,   InputDataAnalog1> = parent.actionsAnalog1.associateWith { InputDataAnalog1() }.toMutableMap()
    internal var stateAnalog2: MutableMap<TAA,  InputDataAnalog2> = parent.actionsAnalog2.associateWith { InputDataAnalog2() }.toMutableMap()

    internal var eventsDigital: Map<TD,   Event<InputDataDigital, TD, TA, TAA>> = parent.actionsDigital.associateWith { Event() }
    internal var eventsAnalog1: Map<TA,   Event<InputDataAnalog1, TD, TA, TAA>> = parent.actionsAnalog1.associateWith { Event() }
    internal var eventsAnalog2: Map<TAA,  Event<InputDataAnalog2, TD, TA, TAA>> = parent.actionsAnalog2.associateWith { Event() }

    internal var isEnabled: Boolean = true

    /**
     * The player's configuration.
     */
    var configuration: Configuration<TD, TA, TAA> = Configuration()

    /**
     * Gets a Digital Event
     */
    @JvmName("getEventDigital")
    fun getEvent(action: TD): Event<InputDataDigital, TD, TA, TAA>   = eventsDigital[action]!!

    /**
     * Gets an Analog1 Event
     */
    @JvmName("getEventAnalog1")
    fun getEvent(action: TA): Event<InputDataAnalog1, TD, TA, TAA>   = eventsAnalog1[action]!!

    /**
     * Gets an Analog2 Event
     */
    @JvmName("getEventAnalog2")
    fun getEvent(action: TAA): Event<InputDataAnalog2, TD, TA, TAA>  = eventsAnalog2[action]!!

    /**
     * Adds a listener to the event
     */
    @JvmName("addListenerDigital")
    fun addListener(action: TD, listener: (InputDataDigital, Player<TD, TA, TAA>) -> Unit): Boolean = eventsDigital[action]!!.addListener(listener)

    /**
     * Gets an Analog1 Event and also adds a listener
     */
    @JvmName("addListenerAnalog1")
    fun addListener(action: TA, listener: (InputDataAnalog1, Player<TD, TA, TAA>) -> Unit): Boolean = eventsAnalog1[action]!!.addListener(listener)
    /**
     * Gets an Analog2 Event and also adds a listener
     */
    @JvmName("addListenerAnalog2")
    fun addListener(action: TAA, listener: (InputDataAnalog2, Player<TD, TA, TAA>) -> Unit): Boolean = eventsAnalog2[action]!!.addListener(listener)

    /**
     * Gets a Digital Event and also adds a Java listener
     */
    @JvmName("addListenerDigital")
    fun addListener(action: TD, listener: InputEventListener<InputDataDigital, TD, TA, TAA>): Boolean = eventsDigital[action]!!.addListener(listener)

    /**
     * Gets an Analog1 Event and also adds a Java event listener
     */
    @JvmName("addListenerAnalog1")
    fun addListener(action: TA, listener: InputEventListener<InputDataAnalog1, TD, TA, TAA>): Boolean = eventsAnalog1[action]!!.addListener(listener)

    /**
     * Gets an Analog2 Event and also adds a Java event listener
     */
    @JvmName("addListenerAnalog2")
    fun addListener(action: TAA, listener: InputEventListener<InputDataAnalog2, TD, TA, TAA>): Boolean    = eventsAnalog2[action]!!.addListener(listener)

    /**
     * Returns the state of the digital action provided, or `null` for an invalid action (you can safely use `!!`)
     */
    @JvmName("getStateDigital")
    fun getState(action: TD): InputDataDigital     = stateDigital[action].let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }
    /**
     * Returns the state of the 1D analog action provided, or `null` for an invalid action (you can safely use `!!`)
     */
    @JvmName("getStateAnalog1")
    fun getState(action: TA): InputDataAnalog1     = stateAnalog1[action].let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }
    /**
     * Returns the state of the 2D analog action provided, or `null` for an invalid action (you can safely use `!!`)
     */
    @JvmName("getStateAnalog2")
    fun getState(action: TAA): InputDataAnalog2    = stateAnalog2[action].let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }

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