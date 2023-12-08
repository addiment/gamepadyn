package computer.living.gamepadyn

import java.util.function.Consumer

/**
 * Represents an event to which listeners are added and called when the event is triggered.
 */
class ActionEvent<T: InputData> internal constructor(/*val type: InputType*/) {
    /**
     * A set of all listeners (lambdas) to this event.
     */
    private val listeners = mutableSetOf<((T) -> Unit)>()
    /**
     * A set of all Java listeners (lambdas) to this event.
     * Java lambdas are implementations of functional interfaces,
     * but Kotlin does stuff differently.
     * We don't really need to know the bytecode differences
     * because we can just have an array of Java's SAM interfaces.
     */
    private val javaListeners = mutableSetOf<Consumer<T>>()

    /**
     * Adds a callback for the event.
     * @return true if it was added and false if it was already there before
     */
    fun addListener(listener: ((T) -> Unit)): Boolean = listeners.add(listener)
    /**
     * Java-specific overload.
     * @see addListener
     */
    fun addJListener(listener: Consumer<T>): Boolean = javaListeners.add(listener)

    /**
     * Removes an already-present callback for the event.
     * @return true if it was removed and false if it wasn't a listener before
     */
    fun removeListener(listener: ((T) -> Unit)): Boolean = listeners.remove(listener)
    /**
     * Java-specific overload.
     * @see removeListener
     */
    fun removeJListener(listener: Consumer<T>): Boolean = javaListeners.remove(listener)

    /**
     * Removes all listeners from the event.
     */
    fun clearListeners() { listeners.clear(); javaListeners.clear() }

    /**
     * Broadcasts an event to all listeners.
     */
    internal fun trigger(data: T) {
        for (e in listeners) e.invoke(data)
        for (e in javaListeners) e.accept(data)
    }

}