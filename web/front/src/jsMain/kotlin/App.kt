import browser.document
import context.UserContext
import context.UserData
import csstype.BoxSizing
import csstype.Overflow
import csstype.Position
import csstype.pct
import csstype.px
import csstype.rgba
import csstype.vh
import emotion.react.css
import enum.Screen
import modules.Sidebar
import modules.screens.DataScreen
import modules.screens.ResultScreen
import modules.screens.SolveScreen
import react.FC
import react.Props
import react.ReactElement
import react.create
import react.dom.client.createRoot
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span
import react.useState

private fun getCurrentScreen(currentScreen: Screen): ReactElement<*> {
    return when (currentScreen) {
        Screen.INPUT_DATA -> DataScreen.create()
        Screen.SOLVE_TASK -> SolveScreen
        Screen.RESULT -> ResultScreen
    }
}

private val userData = UserData()

private val App = FC<Props> {
    UserContext.Provider {
        value = userData
        div {
            val curScreen = useState(Screen.INPUT_DATA)
            css {
                boxSizing = BoxSizing.borderBox
                width = 100.pct
                height = 100.vh
                background = rgba(246, 246, 246, 1.0)
                position = Position.fixed
                top = 0.px
                left = 0.px
                overflow = Overflow.hidden
            }
            +Sidebar.create {
                currentScreen = curScreen
            }
            span {
                css {
                    width = 5.pct
                    height = 100.pct
                }
            }
            +getCurrentScreen(curScreen.component1())
        }
    }
}

fun main() {
    createRoot(document.getElementById("root")!!).render(App.create())
}
