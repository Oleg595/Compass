package modules.screens.data.buttons

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

external interface NextButtonProps: Props {
    var nextSubScreen: DataSubScreen
    var subScreenSetter: StateSetter<DataSubScreen>
}

val NextButton = FC<NextButtonProps> { props ->
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
            left = 85.pct
            position = Position.fixed
            active {
                backgroundColor = rgba(97, 103, 127, 1.0)
            }
        }
        onClick = {
            props.subScreenSetter(props.nextSubScreen)
        }
        +"Далее"
        ReactHTML.i {
            className = ClassName("fa-solid fa-forward")
            style = jso {
                color = NamedColor.white
                marginLeft = 10.px
            }
        }
    }
}
