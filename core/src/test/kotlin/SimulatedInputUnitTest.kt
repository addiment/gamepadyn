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

        val gamepadyn = Gamepadyn(sysTest, strict = true, actions)

        var stateChangeCount = 0

        val p0 = gamepadyn.players[0]

        gamepadyn.update()

        gamepadyn.players[0].configuration = Configuration(
            ActionBind(RawInput.FACE_A, DEBUG_ACTION)
        )

        gamepadyn.update()

        p0.getEventDigital(DEBUG_ACTION)!!.addListener {
            println("Debug action ran!")
            stateChangeCount++
        }

        assertEquals(false, p0.getStateDigital(DEBUG_ACTION)?.digitalData)

        gamepadyn.update()

        assertEquals(false, p0.getStateDigital(DEBUG_ACTION)?.digitalData)

        // one state change
        InputBackendTesting.manipulableStateDigital = true

        gamepadyn.update()
        assertEquals(true, p0.getStateDigital(DEBUG_ACTION)?.digitalData)
        gamepadyn.update()
        assertEquals(true, p0.getStateDigital(DEBUG_ACTION)?.digitalData)

        // two state changes
        InputBackendTesting.manipulableStateDigital = false

        gamepadyn.update()
        assertEquals(false, p0.getStateDigital(DEBUG_ACTION)?.digitalData)
        gamepadyn.update()
        assertEquals(false, p0.getStateDigital(DEBUG_ACTION)?.digitalData)

        // three state changes
        InputBackendTesting.manipulableStateDigital = true

        gamepadyn.update()
        assertEquals(true, p0.getStateDigital(DEBUG_ACTION)?.digitalData)

        assertEquals(3, stateChangeCount)
//        assert(stateChangeCount == 2, "BAD!!!")

    }

}