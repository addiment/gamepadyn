import computer.living.gamepadyn.ActionMap
import computer.living.gamepadyn.Gamepadyn

import Dummy.ActionDigital.*
import Dummy.ActionAnalog1.*
import Dummy.ActionAnalog2.*
import computer.living.gamepadyn.ActionBind
import computer.living.gamepadyn.ActionEnum
import computer.living.gamepadyn.ActionEnumAnalog1
import computer.living.gamepadyn.ActionEnumAnalog2
import computer.living.gamepadyn.ActionEnumDigital
import computer.living.gamepadyn.Configuration
import computer.living.gamepadyn.InputDataAnalog1
import computer.living.gamepadyn.InputDataAnalog2
import computer.living.gamepadyn.InputDataDigital
import computer.living.gamepadyn.RawInput
import computer.living.gamepadyn.RawInputDigital
import kotlin.math.sqrt

class Dummy {

    interface SillyGoofy {
        fun colon3()
    }

    enum class ActionDigital : ActionEnumDigital {
        JUMP,
        SHOOT
    }

    enum class ActionAnalog1 : ActionEnumAnalog1 {
        SOMETHING_ONE_DIMENSIONAL
    }
    enum class ActionAnalog2 : ActionEnumAnalog2 {
        MOVEMENT,
        CAMERA
    }

    fun main() {

        var testi = JUMP


        val gamepadyn = Gamepadyn(
            InputBackendTesting(),
            true,
            ActionMap(
                ActionDigital.entries.toSet(),
                ActionAnalog1.entries.toSet(),
                ActionAnalog2.entries.toSet()
            )
        )

        val p0 = gamepadyn.players[0]

        p0.configuration = Configuration(
            ActionBind(RawInputDigital.FACE_A) {
                val out = when (it) {
                    is InputDataDigital -> it.active
                    is InputDataAnalog1 -> it.x > 0.5
                    is InputDataAnalog2 -> sqrt(it.x * it.x + it.y * it.y) > 0.5
                }
                return@ActionBind ActionBind.Transformation(
                    ActionBind.Transformation.Operation.SET,
                    InputDataDigital(out)
                )
            }
        )

        p0.getState(JUMP)?.active
        p0.getState(SOMETHING_ONE_DIMENSIONAL)?.x
        p0.getState(CAMERA)?.x
        p0.getState(CAMERA)?.y
    }
}