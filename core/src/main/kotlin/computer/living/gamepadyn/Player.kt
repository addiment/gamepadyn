@file:Suppress("MemberVisibilityCanBePrivate")

package computer.living.gamepadyn

/**
 * Represents a specific controller.
 */
class Player<TD, TA, TAA> internal constructor(
    @JvmSynthetic
    internal val parent: Gamepadyn<TD, TA, TAA>,
    @JvmSynthetic
    internal var rawGamepad: InputBackend.RawGamepad
)
    where TD : ActionEnumDigital,
          TA : ActionEnumAnalog1,
          TAA : ActionEnumAnalog2,
          TD : Enum<TD>,
          TA : Enum<TA>,
          TAA : Enum<TAA>
{
    @JvmSynthetic
    internal var stateDigital: MutableMap<TD,   InputDataDigital> = parent.actionsDigital.associateWith { InputDataDigital() }.toMutableMap()
    @JvmSynthetic
    internal var stateAnalog1: MutableMap<TA,   InputDataAnalog1> = parent.actionsAnalog1.associateWith { InputDataAnalog1() }.toMutableMap()
    @JvmSynthetic
    internal var stateAnalog2: MutableMap<TAA,  InputDataAnalog2> = parent.actionsAnalog2.associateWith { InputDataAnalog2() }.toMutableMap()

    @JvmSynthetic
    internal var eventsDigital: Map<TD,   Event<InputDataDigital, TD, TA, TAA>> = parent.actionsDigital.associateWith { Event() }
    @JvmSynthetic
    internal var eventsAnalog1: Map<TA,   Event<InputDataAnalog1, TD, TA, TAA>> = parent.actionsAnalog1.associateWith { Event() }
    @JvmSynthetic
    internal var eventsAnalog2: Map<TAA,  Event<InputDataAnalog2, TD, TA, TAA>> = parent.actionsAnalog2.associateWith { Event() }

    @JvmSynthetic
    internal var isEnabled: Boolean = true

    /**
     * The player's configuration.
     */
    @JvmField
    var configuration: Configuration<TD, TA, TAA>? = null

    /**
     * @return a Digital Event
     */
    @JvmName("getEventDigital")
    fun getEvent(action: TD): Event<InputDataDigital, TD, TA, TAA>   = eventsDigital[action]!!

    /**
     * @return an Analog1 Event
     */
    @JvmName("getEventAnalog1")
    fun getEvent(action: TA): Event<InputDataAnalog1, TD, TA, TAA>   = eventsAnalog1[action]!!

    /**
     * @return an Analog2 Event
     */
    @JvmName("getEventAnalog2")
    fun getEvent(action: TAA): Event<InputDataAnalog2, TD, TA, TAA>  = eventsAnalog2[action]!!

    /**
     * Adds a listener to an input event.
     */
    @JvmName("addListenerDigital")
    fun addListener(action: TD, listener: (Event.EventData<InputDataDigital, TD, TA, TAA>) -> Unit): Boolean = eventsDigital[action]!!.addListener(listener)

    /**
     * Adds a listener to an input event.
     */
    @JvmName("addListenerAnalog1")
    fun addListener(action: TA, listener: (Event.EventData<InputDataAnalog1, TD, TA, TAA>) -> Unit): Boolean = eventsAnalog1[action]!!.addListener(listener)

    /**
     * Adds a listener to an input event.
     */
    @JvmName("addListenerAnalog2")
    fun addListener(action: TAA, listener: (Event.EventData<InputDataAnalog2, TD, TA, TAA>) -> Unit): Boolean = eventsAnalog2[action]!!.addListener(listener)

    /**
     * Adds a Java event listener to an input event.
     */
    @JvmName("addListenerDigital")
    fun addListener(action: TD, listener: InputEventListener<InputDataDigital, TD, TA, TAA>): Boolean = eventsDigital[action]!!.addListener(listener)

    /**
     * Adds a Java event listener to an input event.
     */
    @JvmName("addListenerAnalog1")
    fun addListener(action: TA, listener: InputEventListener<InputDataAnalog1, TD, TA, TAA>): Boolean = eventsAnalog1[action]!!.addListener(listener)

    /**
     * Adds a Java event listener to an input event.
     */
    @JvmName("addListenerAnalog2")
    fun addListener(action: TAA, listener: InputEventListener<InputDataAnalog2, TD, TA, TAA>): Boolean = eventsAnalog2[action]!!.addListener(listener)

    /**
     * Returns the state of the digital action provided.
     */
    @JvmName("getStateDigital")
    fun getState(action: TD): InputDataDigital     = stateDigital[action].let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }
    /**
     * Returns the state of the 1D analog action provided.
     */
    @JvmName("getStateAnalog1")
    fun getState(action: TA): InputDataAnalog1     = stateAnalog1[action].let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }

    /**
     * Returns the state of the 2D analog action provided.
     */
    @JvmName("getStateAnalog2")
    fun getState(action: TAA): InputDataAnalog2    = stateAnalog2[action].let { it ?: throw Exception("Invalid action! Don't modify your Enum.entries!") }

    /*
     * NOTE: This API is unpleasant in Java.
     * This is because Java's type erasure causes the various getState and
     * getEvent functions to each have the same signature.
     * Kotlin has compiler magic to ensure that the functions have different signatures.
     * In order to make it work for Java, we have to use @JvmName to explicitly
     * differentiate their type parameters.
     */

}