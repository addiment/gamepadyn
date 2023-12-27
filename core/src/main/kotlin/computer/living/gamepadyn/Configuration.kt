package computer.living.gamepadyn

data class Configuration<TD, TA, TAA>(
    var binds: ArrayList<ActionBind<*>>
)
        where TD : ActionEnumDigital,
              TA : ActionEnumAnalog1,
              TAA : ActionEnumAnalog2,
              TD : Enum<TD>,
              TA : Enum<TA>,
              TAA : Enum<TAA>
{
    constructor(vararg binds: ActionBind<*>) : this(arrayListOf(*binds))
}