package computer.living.gamepadyn

/**
 * A transformation from inputs to actions.
 *
 * I am not happy with this API as it is, and will be working on developing it further in the future.
 */
/*
 * TODO: Rework the binding API to require less type casting/assumption in implementations.
 *      The constructor isn't great either, almost all work should be done in the transform function.
 *      Why isn't it a lambda? Because users may want to store persistent state *in the bind.*
 *      That might change though. It's not at the top of my priority list right now.
 */
open class ActionBind<T: Enum<T>>(val input: RawInput, internal val targetAction: T) {

    /**
     * Performs a transformation on the input data.
     *
     * NOTE: the InputData parameter should match the InputDescriptor of the `input` field, but under some circumstances, it may not. You should still assume they will match.
     * @param targetAction The return value of this function must conform with this parameter's descriptor.
     * @return the result of the transformation, or `null` for no data/invalid.
     */
    open fun transform(data: InputData, targetAction: InputDescriptor): InputData? = data

}