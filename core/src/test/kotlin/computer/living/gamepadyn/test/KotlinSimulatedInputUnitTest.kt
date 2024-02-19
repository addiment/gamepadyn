package computer.living.gamepadyn.test

import computer.living.gamepadyn.test.KotlinSimulatedInputUnitTest.TestActionAnalog1.*
import computer.living.gamepadyn.test.KotlinSimulatedInputUnitTest.TestActionAnalog2.ANALOG_2D_ACTION
import computer.living.gamepadyn.test.KotlinSimulatedInputUnitTest.TestActionDigital.DIGITAL_ACTION
import computer.living.gamepadyn.ActionBind
import computer.living.gamepadyn.ActionEnumAnalog1
import computer.living.gamepadyn.ActionEnumAnalog2
import computer.living.gamepadyn.ActionEnumDigital
import computer.living.gamepadyn.Configuration
import computer.living.gamepadyn.Gamepadyn
import computer.living.gamepadyn.RawInputAnalog1.*
import computer.living.gamepadyn.RawInputAnalog2.*
import computer.living.gamepadyn.RawInputDigital.*
import kotlin.test.Test
import kotlin.test.assertEquals

class KotlinSimulatedInputUnitTest {

    enum class TestActionDigital : ActionEnumDigital {
        DIGITAL_ACTION
    }

    enum class TestActionAnalog1 : ActionEnumAnalog1 {
        ANALOG_1D_ACTION
    }

    enum class TestActionAnalog2 : ActionEnumAnalog2 {
        ANALOG_2D_ACTION
    }

    @Test
    fun testMainKotlin() {
        println("!!! Gamepadyn test: ${this::class.simpleName} !!!")
        val sysTest = InputBackendTesting()
        InputBackendTesting.manipulableStateDigital = false
        InputBackendTesting.manipulableStateAnalog2d = Pair(0f, 0f)

        val gamepadyn = Gamepadyn.create(
            TestActionDigital::class,
            TestActionAnalog1::class,
            TestActionAnalog2::class,

            sysTest,
            strict = true
        )

        kotlin.io.println("Player count: ${gamepadyn.players.size}")

        var stateChangeCount = 0
        var expectedStateChangeCount = 0

        kotlin.io.println("Player count: ${gamepadyn.players.size}")

        val p0 = gamepadyn.players[0]

        gamepadyn.update()

        gamepadyn.players[0].configuration = Configuration(
            ActionBind(DIGITAL_ACTION,      FACE_DOWN),
            ActionBind(ANALOG_1D_ACTION,    TRIGGER_RIGHT),
            ActionBind(ANALOG_2D_ACTION,    STICK_RIGHT)
        )

        gamepadyn.update()

        p0.getEvent(DIGITAL_ACTION).addListener { data, _ ->
            kotlin.io.println("Debug action ran! (new value: ${data.active})")
            stateChangeCount++
        }

        p0.getEvent(ANALOG_1D_ACTION).addListener { data, _ ->
            kotlin.io.println("Analog 1D action ran! (new value: (${data.x}))")
            stateChangeCount++
        }

        p0.getEvent(ANALOG_2D_ACTION).addListener { data, _ ->
            kotlin.io.println("Analog 2D action ran! (new value: (${data.x}, ${data.y}))")
            stateChangeCount++
        }

        assertEquals(false, p0.getState(DIGITAL_ACTION).active)
        val s1 = p0.getState(ANALOG_2D_ACTION)
        assertEquals(0f, s1.x)
        assertEquals(0f, s1.y)

        gamepadyn.update()

        assertEquals(false, p0.getState(DIGITAL_ACTION).active)
        val s2 = p0.getState(ANALOG_2D_ACTION)
        assertEquals(0f, s2.x)
        assertEquals(0f, s2.y)

        InputBackendTesting.manipulableStateDigital = true
        expectedStateChangeCount++

        gamepadyn.update()
        assertEquals(true, p0.getState(DIGITAL_ACTION).active)
        val s3 = p0.getState(ANALOG_2D_ACTION)
        assertEquals(0f, s3.x)
        assertEquals(0f, s3.y)

        gamepadyn.update()

        InputBackendTesting.manipulableStateAnalog2d = Pair(0f, 1f)
        expectedStateChangeCount++

        assertEquals(true, p0.getState(DIGITAL_ACTION).active)
        gamepadyn.update()

        val s4 = p0.getState(ANALOG_2D_ACTION)
        assertEquals(0f, s4.x)
        assertEquals(1f, s4.y)

        InputBackendTesting.manipulableStateDigital = true
        val s5 = p0.getState(ANALOG_2D_ACTION)
        assertEquals(0f, s5.x)
        assertEquals(1f, s5.y)

        gamepadyn.update()
        assertEquals(true, p0.getState(DIGITAL_ACTION).active)
        gamepadyn.update()
        assertEquals(true, p0.getState(DIGITAL_ACTION).active)

        InputBackendTesting.manipulableStateDigital = false
        InputBackendTesting.manipulableStateAnalog2d = Pair(1f, 0f)
        expectedStateChangeCount++
        expectedStateChangeCount++

        gamepadyn.update()
        assertEquals(false, p0.getState(DIGITAL_ACTION).active)
        val s6 = p0.getState(ANALOG_2D_ACTION)
        assertEquals(1f, s6.x)
        assertEquals(0f, s6.y)

        InputBackendTesting.manipulableStateDigital = true
        gamepadyn.update()
        expectedStateChangeCount++
        assertEquals(true, p0.getState(DIGITAL_ACTION).active)


        assertEquals(expectedStateChangeCount, stateChangeCount)
    }

}