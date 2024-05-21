package modules.screens.solve.button

import csstype.NamedColor
import csstype.pct
import csstype.px
import csstype.rgba
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.button

external interface CompareButtonProps: Props {
    var compareButtonText: String
    var action: () -> Unit
}

val CompareSolveButton = FC<CompareButtonProps> { props ->
    button {
        css {
            marginTop = 15.px
            width = 100.pct
            height = 20.px
            color = NamedColor.white
            backgroundColor = rgba(192, 185, 28, 1.0)
            border = 1.px
            borderRadius = 3.px
        }
        onClick = { props.action() }
        +props.compareButtonText
    }
}
