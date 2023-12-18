# Gamepadyn: A Multipurpose Control library

[//]: # (![Gamepadyn Logo]&#40;logo_128.png&#41;)

# About

Gamepadyn is currently in **BETA.**
The API is *not quite* stable.
If you have any bug fixes or suggestions, feel free to open an issue or a PR. Thanks!

Gamepadyn has been heavily Inspired by input frameworks like Steam Input,
the Unreal Engine Enhanced Input System,
and other systems for video game input.
Its primary purpose is to separate physical input methods from abstract high-level "actions."

Separation of physical and abstract provides freedom to both users and developers:
- users could easily have a control scheme modified to their preferences
- developers can implement changes to input maps much more easily
- features are more easily encapsulated, which makes modularity easier

Gamepadyn also provides state monitoring,
which allows for easy implementation of event-driven programming patterns.
Basically, it takes a programmer 10 seconds to implement a toggleable action
without adding extra class-wide variables that would otherwise make code harder to read.

# Getting Started for FTC Developers

## Installing

Currently, distribution of this library is done via redistributed .jar and .aar files.
Download them from the releases page (or build them yourself) and copy them into your `/TeamCode/lib` folder.
**Make sure to replace `0.1.0-BETA` with whatever version of the library you have.**
Add the following to the dependencies block of your `/TeamCode/build.gradle`:

```groovy
implementation files("lib/core-0.1.0-BETA.jar")
implementation files("lib/core-0.1.0-BETA-sources.jar")
implementation files("lib/ftc-0.1.0-BETA.aar")
implementation files("lib/ftc-0.1.0-BETA-sources.jar")
```

The following are sample OpModes that showcase basic usage of Gamepadyn.

## Kotlin (preferred)

```kotlin
import computer.living.gamepadyn.InputType.*
import computer.living.gamepadyn.ActionBind
import computer.living.gamepadyn.Configuration
import computer.living.gamepadyn.GDesc
import computer.living.gamepadyn.Gamepadyn
import computer.living.gamepadyn.RawInput
import computer.living.gamepadyn.ftc.InputBackendFtc

import com.qualcomm.robotcore.eventloop.opmode.OpMode

class GamepadynKotlinImpl : OpMode() {

  enum class TestAction {
    MOVEMENT,
    ROTATION,
    CLAW,
    DEBUG_ACTION
  }

  private lateinit var gamepadyn: Gamepadyn<TestAction>


  override fun init() {
    gamepadyn = Gamepadyn(
      InputBackendFtc(this),
      true,
      TestAction.MOVEMENT            to GDesc(ANALOG, 2),
      TestAction.ROTATION            to GDesc(ANALOG, 1),
      TestAction.CLAW                to GDesc(DIGITAL),
      TestAction.DEBUG_ACTION        to GDesc(DIGITAL)
    )

    gamepadyn.players[0].configuration = Configuration(
      ActionBind(RawInput.FACE_A, TestAction.DEBUG_ACTION)
    )
  }

  override fun start() {

    // Get a reference to the player (FTC Player 1)
    val p0 = gamepadyn.players[0]
    // Get the event corresponding to DEBUG_ACTION and add a lambda function as a listener to it.
    p0.getEventDigital(TestAction.DEBUG_ACTION)!!.addListener {
      telemetry.addLine("Button ${if (it.digitalData) "pressed"; else "released"}!")
    }

  }

  override fun loop() {
    gamepadyn.update()
    telemetry.update()
  }
}
```

## Java

```Java
package computer.living.gamepadyn.test;

import java.util.Arrays;
import java.util.Objects;

import computer.living.gamepadyn.Gamepadyn;
import computer.living.gamepadyn.Player;
import computer.living.gamepadyn.Tak;
import computer.living.gamepadyn.ftc.InputBackendFtc;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class GamepadynJavaImpl extends OpMode {

  enum TestAction {
    MOVEMENT,
    ROTATION,
    CLAW,
    DEBUG_ACTION
  }

  Gamepadyn<TestAction> gamepadyn;

  @Override
  public void init() {
    // in Java 9, you can do this more easily.
    gamepadyn = new Gamepadyn<>(new InputBackendFtc(this),
            Tak.makeActionMap(Arrays.asList(
                    Tak.a(TestAction.MOVEMENT, 2),
                    Tak.a(TestAction.ROTATION, 1),
                    Tak.d(TestAction.CLAW),
                    Tak.d(TestAction.DEBUG_ACTION)
            ))
    );
  }

  @Override
  public void start() {

    // There's a bit of boilerplate here because of how Java treats nullability.
    // Gamepadyn was designed for Kotlin, but built to also work with Java.
    // It's much easier in Kotlin.

    // Get a reference to the player (FTC Player 1)
    Player<TestAction> p0 = gamepadyn.getPlayer(0);
    assert p0 != null;

    // Get the event corresponding to DEBUG_ACTION and add a lambda function as a listener to it.
    Objects.requireNonNull(p0.getEventDigital(TestAction.DEBUG_ACTION)).addJListener(it -> {
      telemetry.addLine("Button " + ((it.digitalData) ? "pressed" : "released") + "!");
    });

  }

  @Override
  public void loop() {
    gamepadyn.update();
    telemetry.update();
  }
}
```

# Architecture

In Gamepadyn,
game controllers are represented as instances of the `Player` class.
A `Player` holds a `Configuration` object,
which are effectively an array of `ActionBind`s.
`ActionBind`s are functions which transform `InputData` generated by the input backend into8 Actions.
The Action's data type must match that of the return value of the `ActionBind`

# Goals & Non-Goals

Gamepadyn does not try to do everything.
If it tried, it would be impossibly difficult to develop and maintain,
and it would be awful to actually use.

## Goals

- Provide more ways of compile-time validation
  - We may change the `Gamepadyn` class and its children to take 3 type templates instead of 1: one for a supertype and the others for analog and digital subtypes.
- Provide more pre-made `ActionBind` transformations to prevent rewriting code
