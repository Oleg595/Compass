package modules.screens.solve.sub_screen

import react.Props
import react.StateInstance

enum class SolveSubScreen {
    COMPARE,
    RANGE_CRITERIA_VALUES
}

external interface SolveSubScreenProps: Props {
    var solveSubScreen: StateInstance<SolveSubScreen>
}
