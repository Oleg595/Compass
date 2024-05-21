package modules.screens.solve.sub_screen

import context.UserContext
import csstype.Auto
import csstype.FontWeight
import csstype.NamedColor
import csstype.Position
import csstype.Position.Companion.absolute
import csstype.Position.Companion.sticky
import csstype.pct
import csstype.px
import csstype.rgba
import emotion.react.css
import kotlinx.browser.window
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromDynamic
import modules.common.table.DrawTableComponent
import modules.common.table.alternativesToTableData
import modules.common.table.createColumnForAlternativeName
import modules.common.table.createColumnsForCriterias
import modules.common.table.createTable
import modules.screens.solve.button.CompareSolveButton
import modules.screens.solve.requests.getAlternativeComparsions
import org.example.AlternativePair
import org.w3c.fetch.RequestInit
import react.FC
import react.Props
import react.StateInstance
import react.create
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span
import react.useContext
import react.useEffectOnce
import react.useState
import kotlin.js.json

private external interface CompareTableProps : Props {
    var alternativePair: AlternativePair
}

private val CompareTable = FC<CompareTableProps> { props ->
    val criterias = props.alternativePair.first.criteriaToValue.keys
    val cols = mutableListOf(createColumnForAlternativeName())
    cols.addAll(createColumnsForCriterias(criterias))
    val data = alternativesToTableData(listOf(props.alternativePair.first, props.alternativePair.second))
    val table = createTable(cols, data)
    +DrawTableComponent.create {
        this.table = table
    }
}

private external interface ButtonListComponentProps : Props {
    var alternative1: String
    var alternative2: String
}

private val ButtonListComponent = FC<ButtonListComponentProps> { props ->
    div {
        css {
            width = 70.pct
            height = 100.pct
            marginLeft = Auto.auto
            marginRight = Auto.auto
            position = Position.relative
        }
        +CompareSolveButton.create {
            compareButtonText = "Альтернатива 1 предпочтительнее альтернативы 2"
            action = {}
        }
        +CompareSolveButton.create {
            compareButtonText = "Альтернатива 2 предпочтительнее альтернативы 1"
            action = {}
        }
        +CompareSolveButton.create {
            compareButtonText = "Альтернативы эквивалентны"
            action = {}
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
private fun setNextComparsion(state: StateInstance<AlternativePair?>) {
    val userContext = useContext(UserContext)
    val dataContext = userContext.dataContext
    useEffectOnce {
        window.fetch(
            "http://localhost:8081/compass/calculateCompareAlternatives?k=${userContext.k}",
            RequestInit(
                method = "POST",
                headers = json("Content-Type" to "application/json"),
                body = Json.encodeToString(dataContext)
            )
        ).then {
                it.json().then {
                    val pair = Json.decodeFromDynamic<AlternativePair>(it)
                    state.component2().invoke(
                        AlternativePair(
                            pair.first.copy(name = "Альтернатива 1"),
                            pair.second.copy(name = "Альтернатива 2")
                        )
                    )
                }
        }
    }
}

external interface CompareSubScreenProps : SolveSubScreenProps

val CompareScreen = FC<CompareSubScreenProps> { props ->
    val comparsions = getAlternativeComparsions()
    val pairState = useState<AlternativePair>()
    setNextComparsion(pairState)
    if (comparsions.component1() != null) {
        if (pairState.component1() != null) {
            ReactHTML.header {
                css {
                    marginTop = 1.pct
                    marginLeft = Auto.auto
                    marginRight = Auto.auto
                    position = sticky
                    fontWeight = FontWeight.bold
                    fontSize = 18.px
                    color = NamedColor.black
                }
                +"Сравните альтернативы"
            }
            span {
                css {
                    marginTop = 10.pct
                }
                +CompareTable.create {
                    alternativePair = pairState.component1()!!
                }
                span {
                    css {
                        marginTop = 10.pct
                    }
                }
                +ButtonListComponent.create {
                    alternative1 = "Альтернатива 1"
                    alternative2 = "Альтернатива 2"
                }
            }
        }
    }
    ReactHTML.button {
        css {
            top = 92.pct
            left = 5.pct
            position = absolute
            width = 150.px
            height = 50.px
            backgroundColor = rgba(192, 185, 28, 1.0)
            color = NamedColor.white
            borderRadius = 3.px
        }
        onClick = { event ->
            props.solveSubScreen.component2().invoke(SolveSubScreen.RANGE_CRITERIA_VALUES)
        }
        +"К ранжированию значений критериев"
    }
}
