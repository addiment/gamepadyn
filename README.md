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
**Make sure to replace `0.2.0-BETA` with whatever version of the library you have.**
Add the following to the dependencies block of your `/TeamCode/build.gradle`:

```groovy
implementation files("lib/core-0.2.0-BETA.jar")
implementation files("lib/core-0.2.0-BETA-sources.jar")
implementation files("lib/ftc-0.2.0-BETA.aar")
implementation files("lib/ftc-0.2.0-BETA-sources.jar")
```

The following are sample OpModes that showcase basic usage of Gamepadyn.

## Kotlin (preferred)

```kotlin
import com.qualcomm.robotcore.eventloop.opmode.OpMode

import computer.living.gamepadyn.ActionBind
import computer.living.gamepadyn.ActionEnumAnalog1
import computer.living.gamepadyn.ActionEnumAnalog2
import computer.living.gamepadyn.ActionEnumDigital
import computer.living.gamepadyn.ActionMap
import computer.living.gamepadyn.Configuration
import computer.living.gamepadyn.Gamepadyn
import computer.living.gamepadyn.RawInputDigital.*
import computer.living.gamepadyn.RawInputAnalog1.*
import computer.living.gamepadyn.RawInputAnalog2.*
import computer.living.gamepadyn.ftc.InputBackendFtc

import GamepadynKotlinImpl.TestActionDigital.*
import GamepadynKotlinImpl.TestActionAnalog1.*
import GamepadynKotlinImpl.TestActionAnalog2.*
import computer.living.gamepadyn.ActionBindAnalog2to1
import computer.living.gamepadyn.Axis

class GamepadynKotlinImpl : OpMode() {
    enum class TestActionDigital : ActionEnumDigital {
        LAUNCH_DRONE
    }

    enum class TestActionAnalog1 : ActionEnumAnalog1 {
        CLAW,
        ROTATION
    }

    enum class TestActionAnalog2 : ActionEnumAnalog2 {
        MOVEMENT
    }

    private val gamepadyn = Gamepadyn(
        InputBackendFtc(this),
        strict = true,
        ActionMap(
            TestActionDigital.entries,
            TestActionAnalog1.entries,
            TestActionAnalog2.entries
        )
    )

    override fun init() {

        gamepadyn.players[0].configuration = Configuration(
            ActionBind          (LAUNCH_DRONE,    FACE_LEFT             ),
            ActionBind          (MOVEMENT,        STICK_LEFT            ),
            ActionBindAnalog2to1(ROTATION,        STICK_RIGHT,  Axis.X  ),
            ActionBind          (CLAW,            TRIGGER_RIGHT         )
        )

    }

    override fun start() {

        // Get a reference to the player (FTC Player 1)
        val p0 = gamepadyn.getPlayer(0)!!

//        p0.getState(LAUNCH_DRONE).active
//        p0.getState(MOVEMENT).x
//        p0.getState(MOVEMENT).y
//        p0.getState(ROTATION).x

        // Get the event corresponding to LAUNCH_DRONE and add a lambda function as a listener to it.
        p0.getEvent(LAUNCH_DRONE) {
            telemetry.addLine("Button ${if (it()) "pressed" else "released"}!")
            telemetry.update()
        }

        // Usually, analog events should be replaced with state checks, but both work.
        p0.getEvent(MOVEMENT) {
            telemetry.addLine("Movement input: (${it.x}, ${it.y})")
            telemetry.update()
        }

    }

    override fun loop() {
        gamepadyn.update()
    }
}
```

## Java

```Java
package computer.living.gamepadyn.test;

import computer.living.gamepadyn.ActionBind;
import computer.living.gamepadyn.Configuration;
import computer.living.gamepadyn.Gamepadyn;
import computer.living.gamepadyn.ActionMap;
import computer.living.gamepadyn.Player;
import computer.living.gamepadyn.ActionEnumDigital;
import computer.living.gamepadyn.ActionEnumAnalog1;
import computer.living.gamepadyn.ActionEnumAnalog2;

import static computer.living.gamepadyn.RawInputDigital.*;
import static computer.living.gamepadyn.RawInputAnalog1.*;
import static computer.living.gamepadyn.RawInputAnalog2.*;
import static computer.living.gamepadyn.test.GamepadynJavaImpl.TestActionDigital.*;
import static computer.living.gamepadyn.test.GamepadynJavaImpl.TestActionAnalog1.*;
import static computer.living.gamepadyn.test.GamepadynJavaImpl.TestActionAnalog2.*;

import computer.living.gamepadyn.ftc.InputBackendFtc;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class GamepadynJavaImpl extends OpMode {

  enum TestActionDigital implements ActionEnumDigital {
    LAUNCH_DRONE
  }

  enum TestActionAnalog1 implements ActionEnumAnalog1 {
    CLAW
  }

  enum TestActionAnalog2 implements ActionEnumAnalog2 {
    MOVEMENT,
    ROTATION
  }

  final Gamepadyn<TestActionDigital, TestActionAnalog1, TestActionAnalog2> gamepadyn = new Gamepadyn<>(new InputBackendFtc(this),
          true,
          new ActionMap<>(
                  TestActionDigital.values(),
                  TestActionAnalog1.values(),
                  TestActionAnalog2.values()
          )
  );

  @Override
  public void init() {
    //noinspection RedundantSuppression DataFlowIssue
    gamepadyn.getPlayer(0).setConfiguration(new Configuration<>(
            new ActionBind<>(LAUNCH_DRONE, FACE_LEFT),
            new ActionBind<>(MOVEMENT, STICK_LEFT),
            new ActionBind<>(CLAW, TRIGGER_RIGHT)
    ));
  }

  @Override
  public void start() {

    // There's a bit of boilerplate here because of how Java treats nullability.
    // Gamepadyn was designed for Kotlin, but built to also work with Java.
    // It's much easier in Kotlin.

    // Get a reference to the player (FTC Player 1)
    Player<TestActionDigital, TestActionAnalog1, TestActionAnalog2> p0 = gamepadyn.getPlayer(0);
    assert p0 != null;

    // Get the event corresponding to LAUNCH_DRONE and add a lambda function as a listener to it.
    p0.getEventDigital(LAUNCH_DRONE, it -> {
      telemetry.addLine("Button " + ((it.active) ? "pressed" : "released") + "!");
      telemetry.update();
    });

    // Usually, analog events should be replaced with state checks, but both work.
    p0.getEventAnalog2(MOVEMENT, it -> {
      telemetry.addLine("Movement input: (${it.x}, ${it.y})");
      telemetry.update();
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
