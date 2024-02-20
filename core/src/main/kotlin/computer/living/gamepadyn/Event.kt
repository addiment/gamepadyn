package computer.living.gamepadyn

//operator fun <TD, TA, TAA> Event.EventData<InputDataDigital, TD, TA, TAA>.invoke()
//        where TD : ActionEnumDigital,
//              TA : ActionEnumAnalog1,
//              TAA : ActionEnumAnalog2,
//              TD : Enum<TD>,
//              TA : Enum<TA>,
//              TAA : Enum<TAA> = this.data()
//operator fun <TD, TA, TAA> Event.EventData<InputDataAnalog1, TD, TA, TAA>.invoke()
//        where TD : ActionEnumDigital,
//              TA : ActionEnumAnalog1,
//              TAA : ActionEnumAnalog2,
//              TD : Enum<TD>,
//              TA : Enum<TA>,
//              TAA : Enum<TAA> = this.data()
//operator fun <TD, TA, TAA> Event.EventData<InputDataAnalog2, TD, TA, TAA>.invoke()
//        where TD : ActionEnumDigital,
//              TA : ActionEnumAnalog1,
//              TAA : ActionEnumAnalog2,
//              TD : Enum<TD>,
//              TA : Enum<TA>,
//              TAA : Enum<TAA> = this.data()

/**
 * Represents an event to which listeners are added and called when the event is triggered.
 *
 * TODO: I'm on the fence about whether [EventData.player] should be a [Player] reference or an integer.
 *      A reference is a lot more convenient and fail-safe, but adds a lot of resistance with generic arguments.
 *      An integer index, on the other hand, has less generics, but is still annoying.
 *      On a related note, I think the Event class should be sealed and replaced with subclasses,
 *      because then we stick with the philosophy of "different types for different things."
 *      It also would mean that we can add custom shorthand (a la `.invoke()`) to make API use
 *      even easier. When I started writing extension functions for *my own library*,
 *      I realized I was probably doing something wrong from a design stanpoint.
 */
class Event<T, TD, TA, TAA> internal constructor()
        where T : InputData,
              TD : ActionEnumDigital,
              TA : ActionEnumAnalog1,
              TAA : ActionEnumAnalog2,
              TD : Enum<TD>,
              TA : Enum<TA>,
              TAA : Enum<TAA>
{
    /**
     * TODO: should there be a field for the previous state? [ActionBind]s have one.
     *      on that note, what about a field for *which action it was?*
     *      all of those could be helpful, and now that we have a class specifically for event data,
     *      we can basically add whatever we need.
     *      It's not fundamental for the functionality of event listeners, but it would be convenient.
     *      The only way you can get previous data currently is by storing it yourself,
     *      which seems more like a hack than anything else.
     */
    data class EventData<T, TD, TA, TAA>(
        /**
         * The new state of the action.
         */
        @JvmField
        val data: T,
        /**
         * The [Player] whose state changed.
         */
        @JvmField
        val player: Player<TD, TA, TAA>
    )
            where T : InputData,
                  TD : ActionEnumDigital,
                  TA : ActionEnumAnalog1,
                  TAA : ActionEnumAnalog2,
                  TD : Enum<TD>,
                  TA : Enum<TA>,
                  TAA : Enum<TAA>

    /**
     * A set of all listeners (lambdas) to this event.
     */
    private val listeners = mutableSetOf<((EventData<T, TD, TA, TAA>) -> Unit)>()

    /**
     * A set of all Java listeners (lambdas) to this event.
     * Java lambdas are implementations of functional interfaces,
     * but Kotlin does stuff differently.
     * We don't really need to know the bytecode differences
     * because we can just have an array of Java's SAM interfaces.
     */
    private val javaListeners = mutableSetOf<InputEventListener<T, TD, TA, TAA>>()

    /**
     * Adds a callback for the event.
     * @return true if it was added and false if it was already there before
     */
    fun addListener(listener: ((EventData<T, TD, TA, TAA>) -> Unit)): Boolean = listeners.add(listener)
    /**
     * Java-specific overload.
     * @see addListener
     */
    fun addListener(listener: InputEventListener<T, TD, TA, TAA>): Boolean = javaListeners.add(listener)

    /**
     * Removes an already-present callback for the event.
     * @return true if it was removed and false if it wasn't a listener before
     */
    fun removeListener(listener: ((EventData<T, TD, TA, TAA>) -> Unit)): Boolean = listeners.remove(listener)
    /**
     * Java-specific overload.
     * @see removeListener
     */
    fun removeListener(listener: InputEventListener<T, TD, TA, TAA>): Boolean = javaListeners.remove(listener)

    /**
     * Removes all listeners from the event. This includes both Kotlin functions AND Java functional interfaces.
     */
    fun clearListeners() { listeners.clear(); javaListeners.clear() }

    /**
     * Broadcasts an event to all listeners. For internal use only (inside of the Gamepadyn class)
     */
    internal fun trigger(data: T, player: Player<TD, TA, TAA>) {
        for (e in listeners) e.invoke(EventData(data, player))
        for (e in javaListeners) e.onStateChange(EventData(data, player))
    }

}