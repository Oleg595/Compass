package modules.screens.data.sub_screen

import react.Props
import react.StateSetter

external interface DataSubScreenProps : Props {
    var subScreenSetter: StateSetter<DataSubScreen>
}

enum class DataSubScreen {
    CHOOSE_CRITERIA,
    CHOOSE_ALTERNATIVE,
    VIEW_DATA
}
