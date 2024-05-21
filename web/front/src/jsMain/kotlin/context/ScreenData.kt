package context

import modules.screens.data.sub_screen.DataSubScreen
import react.createContext

class ScreenData {
    var lastDataSubScreen = DataSubScreen.CHOOSE_CRITERIA
}

val ScreenContext = createContext(ScreenData())
