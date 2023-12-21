@file:Suppress("MemberVisibilityCanBePrivate")

package computer.living.gamepadyn

import computer.living.gamepadyn.InputType.*

class Player<TD, TA, TAA, TAAA> internal constructor(
    internal val parent: Gamepadyn<TD, TA, TAA, TAAA>,
    internal var rawGamepad: InputBackend.RawGamepad
)
        where TD : ActionEnumDigital,
              TA : ActionEnumAnalog1,
              TAA : ActionEnumAnalog2,
              TAAA : ActionEnumAnalog3,
              TD : Enum<TD>,
              TA : Enum<TA>,
              TAA : Enum<TAA>,
              TAAA : Enum<TAAA>
{
    /**
     * The current state of every action tracked by the Player.
     */
    internal var state: Map<InputType, Map<ActionEnum>> = parent.actions.entries.associate {
        // these statements proves why Kotlin is a top-tier language. or maybe it just proves that my code is bad? idk
        when (it.value!!.type) {
            ANALOG -> {
                if (it.value!!.axes <= 0) throw Exception("Provided an analog action with 0 or less axes!")
                return@associate (it.key to (if (it.value!!.axes == 1) InputDataAnalog(0f) else InputDataAnalog(
                    0f,
                    *FloatArray(it.value!!.axes - 1).toTypedArray()
                )))
            }

            DIGITAL -> return@associate (it.key to InputDataDigital())
        }
    }.toMutableMap()

        /**
     * These two maps have to be separate due to Kotlin's rules on generics.
     * This doesn't really affect the user, but here it means that you need to put a bit more work into type checking.
     * In short, generics cannot be checked at runtime unless reified, but you can't reify class generic parameters.
     * If we had 1 map with both analog and digital events, we would need to create a mechanism for type-checking at runtime.
     * Previously, this came in the form of a "dataType" parameter on the ActionEvent. It no longer exists.
     *
     *
     * As much as I'd like for us to have everything work perfectly at runtime and compile-time, we have to make compromises.
     */

    internal val eventsDigital: Map<TD, ActionEvent<InputDataDigital>> =
        parent.actions.entries.filter { it.value!!.type == DIGITAL }
            .associate { it.key to ActionEvent() }

    internal val eventsAnalog: Map<TA, ActionEvent<InputDataAnalog1>> =
        parent.actions.entries.filter { it.value!!.type == ANALOG }
            .associate { it.key to ActionEvent() }

    /**
     * The player's configuration.
     */
    var configuration: Configuration<T>? = null

    fun getEvent(action: TD): ActionEvent<InputDataDigital>? = eventsDigital[action]
    fun getEvent(action: TA): ActionEvent<InputDataAnalog1>? = eventsAnalog[action]

    /**
     * Returns the current state of the provided action (if valid) and `null` if the state doesn't exist or hasn't been updated.
     */
    fun getState(action: T): InputData? = state[action]
    fun getStateAnalog(action: T): InputDataAnalog? {
        val s = state[action]
        return if (s != null && s is InputDataAnalog) s; else null
    }
    fun getStateDigital(action: T): InputDataDigital? {
        val s = state[action]
        return if (s != null && s is InputDataDigital) s; else null
    }

}