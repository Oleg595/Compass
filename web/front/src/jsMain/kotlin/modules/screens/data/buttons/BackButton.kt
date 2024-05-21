package modules.screens.data.buttons

import context.UserContext
import csstype.ClassName
import csstype.NamedColor
import csstype.Position
import csstype.pct
import csstype.px
import csstype.rgba
import emotion.react.css
import kotlinx.js.jso
import modules.screens.data.sub_screen.DataSubScreen
import react.FC
import react.Props
import react.StateSetter
import react.dom.html.ReactHTML
import react.useContext

external interface BackButtonProps: Props {
    var backSubScreen: DataSubScreen
    var subScreenSetter: StateSetter<DataSubScreen>
}

val BackButton = FC<BackButtonProps> { props ->
    ReactHTML.button {
        css {
            width = 100.px
            height = 25.px
            border = 1.px
            borderRadius = 3.px
            fontSize = 16.px
            color = NamedColor.white
            backgroundColor = rgba(192, 185, 28, 1.0)
            top = 92.pct
            left = 20.pct
            position = Position.fixed
            active {
                backgroundColor = rgba(97, 103, 127, 1.0)
            }
        }
        onClick = {
            props.subScreenSetter(props.backSubScreen)
        }
        +"Назад"
        ReactHTML.i {
            className = ClassName("fa-solid fa-backward")
            style = jso {
                color = NamedColor.white
                marginLeft = 10.px
            }
        }
    }
}
