# Version History


## v0.2.0-BETA
- Added shorthand for `ActionEvent.addListener` using the `invoke` operator.
  - In Kotlin, `ActionEvent.addListener { /* ... */ }` can now be called with `ActionEvent { /* ... */ }`.
  - See the README for an updated example.
- Added shorthand for `InputData` using the `invoke` and `get` operators.
  - The intent of this change was to make raw input data easier to access.
- Renamed `ActionEvent.addJListener` to `ActionEvent.addListener` to remain in line with its Kotlin API counterpart. 
- Added error messages to all assertions.
- `JavaThunks` has been reworked. I am not satisfied with the current state, but haven't yet thought of a way to improve it.
  - Notably, you are no longer required to make a map separately from initialization, and can use the `Gamepadyn` constructor with `vararg` instead.
- Added factory methods to `InputDescriptor` (aka. `GDesc`), replacing the constructor which has previously led to some issues with default arguments.
  - The intent of this change was to prevent accidentally calling the constructor with invalid arguments that can't be checked at compile time without an annotation processor.
  - Calling `InputDescriptor` with `(DIGITAL, 1)` or `(ANALOG)` is far too easy, leading to runtime errors. `GDesc.digital()` and `GDesc.analog()` are able to state most requirements at compile-time.

### Goals

- Make it harder to get references to things that shouldn't have references taken of
- Enforce more compile-time constraints

## v0.1.0-BETA

- First version!