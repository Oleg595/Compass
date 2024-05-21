package modules.screens.data.sub_screen

import csstype.Auto
import csstype.FontWeight
import csstype.NamedColor
import csstype.Position
import csstype.pct
import csstype.px
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML

external interface SubScreenHeaderProps: Props {
    var title: String
}

val SubScreenHeader = FC<SubScreenHeaderProps> { props ->
    ReactHTML.header {
        css {
            marginTop = 1.pct
            marginLeft = Auto.auto
            marginRight = Auto.auto
            position = Position.relative
            fontWeight = FontWeight.bold
            fontSize = 18.px
            color = NamedColor.black
        }
        +props.title
    }
}