package computer.living.gamepadyn

/**
 * A transformation from inputs to actions.
 *
 * I am not happy with this API as it is, and will be working on developing it further in the future.
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