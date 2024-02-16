package computer.living.gamepadyn

import java.util.function.BiConsumer
import java.util.function.Consumer

/**
 * Represents an event to which listeners are added and called when the event is triggered.
 */
class Event<T: InputData, TD, TA, TAA> internal constructor()
    where TD : ActionEnumDigital,
          TA : ActionEnumAnalog1,
          TAA : ActionEnumAnalog2,
          TD : Enum<TD>,
          TA : Enum<TA>,
          TAA : Enum<TAA>
{
    /**
     * A set of all listeners (lambdas) to this event.
     */
    private val listeners = mutableSetOf<((T, Player<TD, TA, TAA>) -> Unit)>()
    /**
     * A set of all Java listeners (lambdas) to this event.
     * Java lambdas are implementations of functional interfaces,
     * but Kotlin does stuff differently.
     * We don't really need to know the bytecode differences
     * because we can just have an array of Java's SAM interfaces.
     */
    private val javaListeners = mutableSetOf<BiConsumer<T, Player<TD, TA, TAA>>>()

    /**
     * Adds a callback for the event.
     * @return true if it was added and false if it was already there before
     */
    /* @JvmSynthetic */ fun addListener(listener: ((T, Player<TD, TA, TAA>) -> Unit)): Boolean = listeners.add(listener)
    /**
     * Java-specific overload.
     * @see addListener
     */
    fun addListener(listener: BiConsumer<T, Player<TD, TA, TAA>>): Boolean = javaListeners.add(listener)

    /**
     * Alias for [addListener]
     */
    /* @JvmSynthetic */ operator fun invoke(listener: ((T, Player<TD, TA, TAA>) -> Unit)): Boolean = listeners.add(listener)
    /**
     * Alias for [addListener]
     */
    operator fun invoke(listener: BiConsumer<T, Player<TD, TA, TAA>>): Boolean = javaListeners.add(listener)


    /**
     * Removes an already-present callback for the event.
     * @return true if it was removed and false if it wasn't a listener before
     */
//    @JvmSynthetic
    fun removeListener(listener: ((T, Player<TD, TA, TAA>) -> Unit)): Boolean = listeners.remove(listener)
    /**
     * Java-specific overload.
     * @see removeListener
     */
    fun removeListener(listener: BiConsumer<T, Player<TD, TA, TAA>>): Boolean = javaListeners.remove(listener)

    /**
     * Removes all listeners from the event.
     */
    fun clearListeners() { listeners.clear(); javaListeners.clear() }

    /**
     * Broadcasts an event to all listeners.
     */
    internal fun trigger(data: T, player: Player<TD, TA, TAA>) {
        for (e in listeners) e.invoke(data, player)
        for (e in javaListeners) e.accept(data, player)
    }

}