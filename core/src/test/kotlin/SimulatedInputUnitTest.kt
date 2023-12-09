import computer.living.gamepadyn.InputType.ANALOG
import computer.living.gamepadyn.InputType.DIGITAL
import SimulatedInputUnitTest.TestAction.DEBUG_ACTION
import SimulatedInputUnitTest.TestAction.ANALOG_2D_ACTION
import computer.living.gamepadyn.ActionBind
import computer.living.gamepadyn.Configuration
import computer.living.gamepadyn.GDesc
import computer.living.gamepadyn.Gamepadyn
import computer.living.gamepadyn.RawInput
import kotlin.test.Test
import kotlin.test.assertEquals

class SimulatedInputUnitTest {

    enum class TestAction {
        DEBUG_ACTION,
        ANALOG_2D_ACTION
    }

    private val actions: Map<TestAction, GDesc> = mapOf(
        DEBUG_ACTION to GDesc(DIGITAL),
        ANALOG_2D_ACTION to GDesc(ANALOG, 2)
    )

    @Test
    fun main() {

        val sysTest = InputBackendTesting()
        InputBackendTesting.manipulableStateDigital = false
        InputBackendTesting.manipulableStateAnalog2d = Pair(0f, 0f)

        val gamepadyn = Gamepadyn(sysTest, strict = true, actions)

        var stateChangeCount = 0
        var expectedStateChangeCount = 0

        val p0 = gamepadyn.players[0]

        gamepadyn.update()

        gamepadyn.players[0].configuration = Configuration(
            ActionBind(RawInput.FACE_A, DEBUG_ACTION),
            ActionBind(RawInput.STICK_LEFT, ANALOG_2D_ACTION)
        )

        gamepadyn.update()

        p0.getEventDigital(DEBUG_ACTION)!!.addListener {
            println("Debug action ran! (new value: ${it.digitalData})")
            stateChangeCount++
        }

        p0.getEventAnalog(ANALOG_2D_ACTION)!!.addListener {
            println("Analog 2D action ran! (new value: ${it.analogData})")
            stateChangeCount++
        }

        assertEquals(false, p0.getStateDigital(DEBUG_ACTION)?.digitalData)
        val s1 = p0.getStateAnalog(ANALOG_2D_ACTION)!!
        assertEquals(0f, s1.analogData[0])
        assertEquals(0f, s1.analogData[1])

        gamepadyn.update()

        assertEquals(false, p0.getStateDigital(DEBUG_ACTION)?.digitalData)
        val s2 = p0.getStateAnalog(ANALOG_2D_ACTION)!!
        assertEquals(0f, s2.analogData[0])
        assertEquals(0f, s2.analogData[1])

        InputBackendTesting.manipulableStateDigital = true
        expectedStateChangeCount++

        gamepadyn.update()
        assertEquals(true, p0.getStateDigital(DEBUG_ACTION)?.digitalData)
        val s3 = p0.getStateAnalog(ANALOG_2D_ACTION)!!
        assertEquals(0f, s3.analogData[0])
        assertEquals(0f, s3.analogData[1])

        gamepadyn.update()

        InputBackendTesting.manipulableStateAnalog2d = Pair(0f, 1f)
        expectedStateChangeCount++

        assertEquals(true, p0.getStateDigital(DEBUG_ACTION)?.digitalData)
        gamepadyn.update()

        val s4 = p0.getStateAnalog(ANALOG_2D_ACTION)!!
        assertEquals(0f, s4.analogData[0])
        assertEquals(1f, s4.analogData[1])

        InputBackendTesting.manipulableStateDigital = true
        val s5 = p0.getStateAnalog(ANALOG_2D_ACTION)!!
        assertEquals(0f, s5.analogData[0])
        assertEquals(1f, s5.analogData[1])

        gamepadyn.update()
        assertEquals(true, p0.getStateDigital(DEBUG_ACTION)?.digitalData)
        gamepadyn.update()
        assertEquals(true, p0.getStateDigital(DEBUG_ACTION)?.digitalData)

        InputBackendTesting.manipulableStateDigital = false
        InputBackendTesting.manipulableStateAnalog2d = Pair(1f, 0f)
        expectedStateChangeCount++
        expectedStateChangeCount++

        gamepadyn.update()
        assertEquals(false, p0.getStateDigital(DEBUG_ACTION)?.digitalData)
        val s6 = p0.getStateAnalog(ANALOG_2D_ACTION)!!
        assertEquals(1f, s6.analogData[0])
        assertEquals(0f, s6.analogData[1])

        InputBackendTesting.manipulableStateDigital = true
        gamepadyn.update()
        expectedStateChangeCount++
        assertEquals(true, p0.getStateDigital(DEBUG_ACTION)?.digitalData)


        assertEquals(expectedStateChangeCount, stateChangeCount)
//        assert(stateChangeCount == 2, "BAD!!!")

    }

}