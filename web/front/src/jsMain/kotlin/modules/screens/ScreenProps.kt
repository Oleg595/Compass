package modules.screens

import enum.Screen
import react.Props
import react.ReactElement

external interface ScreenProps : Props {
    var screen : Screen
    var child: ReactElement<*>
}
