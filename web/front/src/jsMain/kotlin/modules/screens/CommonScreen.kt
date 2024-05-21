package modules.screens

import csstype.AlignItems
import csstype.Display
import csstype.FlexDirection
import csstype.NamedColor
import csstype.Overflow
import csstype.Position
import csstype.pct
import csstype.px
import emotion.react.css
import kotlinx.js.jso
import react.FC
import react.dom.html.ReactHTML.div

val CommonScreen = FC<ScreenProps> { props ->
    div {
        css {
            marginLeft = 18.pct
            width = 82.pct
            height = 100.pct
            overflow = Overflow.hidden
        }
        div {
            css {
                width = 95.pct
                height = 85.pct
                top = 15.px
                padding = 20.px
                position = Position.relative
                display = Display.flex
                flexDirection = FlexDirection.column
                alignItems = AlignItems.flexStart
                border = 1.px
                borderRadius = 10.px
                backgroundColor = NamedColor.white
            }
            style = jso {
                overflowY = Overflow.scroll
                overflowX = Overflow.hidden
            }
            +props.child
        }
    }
}
