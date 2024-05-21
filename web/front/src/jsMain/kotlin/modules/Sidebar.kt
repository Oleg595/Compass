package modules

import csstype.*
import emotion.react.css
import enum.Screen
import kotlinx.js.jso
import react.FC
import react.Props
import react.StateInstance
import react.create
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.i
import react.dom.html.ReactHTML.span
import styles.AppClassNames
import styles.AppFontFamily
import styles.AppFontWeight
import styles.AppIcons

private external interface ScreenContainerProps : Props {
    var iconClass: ClassName
    var screen: Screen
    var currentScreenState: StateInstance<Screen>
}

private val createScreenContainer = FC<ScreenContainerProps> { props ->
    div {
        val buttonColor = if (props.currentScreenState.component1() == props.screen)
            rgba(192, 185, 28, 1.0)
        else
            rgba(97, 103, 127, 1.0)
        css {
            height = 8.vh
            width = 100.pct
            position = Position.relative
        }
        i {
            className = props.iconClass
            style = jso {
                color = buttonColor
            }
        }
        span {
            css {
                marginLeft = 10.px
            }
        }
        a {
            css(AppClassNames.screenButton) {
                color = buttonColor
                fontFamily = AppFontFamily.inter
                fontWeight = AppFontWeight.medium
                fontSize = 13.px
            }
            onClick = { props.currentScreenState.component2()(props.screen) }
            +props.screen.screenName
        }
    }
}

external interface SidebarProps: Props {
    var currentScreen: StateInstance<Screen>
}

val Sidebar = FC<SidebarProps> { props ->
    div {
        css {
            width = 16.pct
            height = 100.vh
            background = NamedColor.white
            position = Position.fixed
            overflow = Overflow.hidden
        }
        span {
            css {
                color = rgba(192, 185, 28, 1.0)
                position = Position.relative
                top = 4.vh
                left = 1.vw
                fontSize = 24.px
                fontFamily = AppFontFamily.inter
                fontWeight = AppFontWeight.semiBold
                textAlign = TextAlign.left
            }
            +"КОМПАС"
        }
        div {
            css {
                width = 100.pct
                top = 10.vh
                left = 2.vw
                position = Position.relative
            }
            div {
                +createScreenContainer.create {
                    iconClass = AppIcons.data
                    screen = Screen.INPUT_DATA
                    currentScreenState = props.currentScreen
                }
                +createScreenContainer.create {
                    iconClass = AppIcons.solve
                    screen = Screen.SOLVE_TASK
                    currentScreenState = props.currentScreen
                }
                +createScreenContainer.create {
                    iconClass = AppIcons.solution
                    screen = Screen.RESULT
                    currentScreenState = props.currentScreen
                }
            }
        }
    }
}
