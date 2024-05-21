package modules.screens.data.sub_screen

import context.UserContext
import csstype.Display
import csstype.Position
import csstype.em
import csstype.pct
import csstype.px
import csstype.rgba
import emotion.react.css
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.js.jso
import modules.screens.data.buttons.DownloadButton
import modules.screens.data.buttons.NextButton
import org.example.CriteriaEntity
import react.FC
import react.Props
import react.StateInstance
import react.create
import react.dom.html.InputType
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.useContext
import react.useEffectOnce
import react.useState

private external interface CriteriaCheckboxProps : Props {
    var criteria: CriteriaEntity
}

private val CriteriaCheckbox = FC<CriteriaCheckboxProps> { props ->
    val userContext = useContext(UserContext)
    val (isChecked, setChecked) = useState(
        userContext.dataContext.getCriteriaByNameOrNull(props.criteria.name) != null
    )
    div {
        css {
            display = Display.block
            position = Position.relative
            margin = 1.pct
            width = 100.pct
            paddingLeft = 26.px
        }
        input {
            css {
                width = 18.px
                height = 18.px
                border = 1.px
                borderRadius = 3.px
            }
            type = InputType.checkbox
            checked = isChecked
            onChange = {
                setChecked.invoke(!isChecked)
                if (!isChecked) {
                    userContext.dataContext.addCriteria(props.criteria)
                } else {
                    userContext.dataContext.removeCriteria(props.criteria)
                }
            }
        }
        label {
            css {
                marginLeft = 2.pct
                fontSize = 16.px
                lineHeight = 1.2.em
                color = rgba(97, 103, 127, 1.0)
            }
            +props.criteria.name
        }
    }
}

private fun getCriteriaNames(): StateInstance<Array<String>> {
    val state = useState(emptyArray<String>())
    useEffectOnce {
        window.fetch("http://localhost:8081/data/getCriterias", jso {
            method = "get"
            headers = {
                "Content-Type" to "application/json"
            }
        })
            .then {
                it.json().then { criterias ->
                    state.component2().invoke(criterias as Array<String>)
                }
            }
    }
    return state
}

val ChooseCriteriaScreen = FC<DataSubScreenProps> { props ->
    val userContext = useContext(UserContext)
    userContext.isDataChoosen = false
    val criteriaState = getCriteriaNames()
    +SubScreenHeader.create {
        title = "Выберите значимые критерии"
    }
    criteriaState.component1().forEach { criteriaName ->
        +CriteriaCheckbox.create {
            this.criteria = CriteriaEntity(criteriaName)
        }
    }
    +DownloadButton.create {
        subScreenSetter = props.subScreenSetter
    }
    +NextButton.create {
        subScreenSetter = props.subScreenSetter
        nextSubScreen = DataSubScreen.CHOOSE_ALTERNATIVE
    }
}
