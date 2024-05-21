package modules.screens

import enum.Screen
import modules.screens.result.sub_screen.ResultGraphScreen
import react.FC
import react.create

val ResultScreen = FC<ScreenProps> { props ->
    +CommonScreen.create{
        screen = props.screen
        child = ResultGraphScreen.create()
    }
}.create {
    screen = Screen.RESULT
}
