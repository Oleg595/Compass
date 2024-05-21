package modules.screens

import context.ScreenContext
import enum.Screen
import modules.screens.data.sub_screen.ChooseAlternativeScreen
import modules.screens.data.sub_screen.ChooseCriteriaScreen
import modules.screens.data.sub_screen.DataSubScreen
import modules.screens.data.sub_screen.ViewDataScreen
import react.FC
import react.Props
import react.ReactElement
import react.StateInstance
import react.create
import react.useContext
import react.useState

private fun getSubScreen(subScreenState: StateInstance<DataSubScreen>): ReactElement<*> {
    return when (subScreenState.component1()) {
        DataSubScreen.CHOOSE_CRITERIA -> ChooseCriteriaScreen.create {
            subScreenSetter = subScreenState.component2()
        }
        DataSubScreen.CHOOSE_ALTERNATIVE -> ChooseAlternativeScreen.create {
            subScreenSetter = subScreenState.component2()
        }
        DataSubScreen.VIEW_DATA -> ViewDataScreen.create {
            subScreenSetter = subScreenState.component2()
        }
    }
}

val DataScreen = FC<Props> {
    val screenContext = useContext(ScreenContext)
    val subScreenState = useState(screenContext.lastDataSubScreen)
    +CommonScreen.create {
        screenContext.lastDataSubScreen = subScreenState.component1()
        screen = Screen.INPUT_DATA
        child = getSubScreen(subScreenState)
    }
}
