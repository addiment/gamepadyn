# Version History


## v0.2.0-BETA
There are probably a lot of things I missed, the codebase is fairly large and the scale of the changes is larger.
- Completely reworked the project to provide near 100% compile-time type checking.
  - This makes everything significantly less error prone,
  - In Kotlin, this makes use significantly more convenient, concise, and intuitive.
    - In Java, this does nothing (sorry not sorry).
      - More specifically, due to type erasure on the JVM,
      overloaded methods with generic arguments require separate names.
      This makes the Java version of the API almost identical what it was previously (but hey, if you're using Gamepadyn in Java, you probably don't like change anyways).
      Despite having different names, both APIs still have type checking.
      - The largest concern with this change is that Java and Kotlin have *different* APIs, which makes porting code harder.
      - The benefits for Kotlin outweigh the downsides for Java (and I am very biased)
- The order of the `ActionBind` constructor has been flipped.
This is somewhat of a small and annoying change,
but the goal is readability. *Putting the action first*
makes understanding a configuration miles easier.

## v0.1.1-BETA
NOTE: this version never made it to main. Most of the changes were adapted to work for the 0.2.0 BETA, and the version number was slightly misleading. 
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