package modules.screens

import enum.Screen
import modules.screens.solve.sub_screen.CompareScreen
import modules.screens.solve.sub_screen.RangeCriteriaValuesScreen
import modules.screens.solve.sub_screen.SolveConflictScreen
import modules.screens.solve.sub_screen.SolveSubScreen
import react.FC
import react.ReactElement
import react.StateInstance
import react.create
import react.useState

private fun getSubScreen(subScreenState: StateInstance<SolveSubScreen>): ReactElement<*> {
    return when (subScreenState.component1()) {
        SolveSubScreen.COMPARE -> CompareScreen.create {
            solveSubScreen = subScreenState
        }
        SolveSubScreen.RANGE_CRITERIA_VALUES -> RangeCriteriaValuesScreen.create{
            solveSubScreen = subScreenState
        }
    }
}

val SolveScreen = FC<ScreenProps> { props ->
    val subScreenState = useState(SolveSubScreen.RANGE_CRITERIA_VALUES)
    +CommonScreen.create {
        screen = props.screen
//        child = getSubScreen(subScreenState)
        child = SolveConflictScreen.create {
            solveSubScreen = subScreenState
        }
    }
}.create {
    screen = Screen.SOLVE_TASK
}
