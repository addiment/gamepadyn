# Issues with Gamepadyn

This is as of version `0.3.0-BETA`.

## Conflation of Player, Gamepadyn, and Event

Originally added for convenience, `Player`s have methods to retrieve `Event`s and add events
to them directly without needing to call `.getEvent(THING).addListener { /* ... */ }`.
This has led to a fairly bloated API, where the `Event` class seems all but useless outside of
**REMOVING** EVENT LISTENERS,
a use case I never considered important enough to add shorthand methods for???
Either way, 

## Misuse of operator functions

I love shorthand. I love brevity. However, clarity is (debatably) **MORE** important than
unintuitive shortcuts. One particular instance of this in Gamepadyn is the `.invoke()` operator
as used in the `InputData` class. This does not "invoke the data," rather,
in some cases (hence why this is a problem) it returns the primitive value that it wraps.

## Inconsistent usage of getters/setters/properties

Kotlin's properties are very powerful, we probably should use them more.

## Poor documentation

This is mostly due to Gamepadyn (in its current state) having
1. a highly volatile API, and
2. a single user/developer/maintainer 
   - ...who are the same person
   - ...and the one writing this.

### API

Typos, weird inconsistent writing style, etc. Needs work overall.

### Implementation

Same with API.