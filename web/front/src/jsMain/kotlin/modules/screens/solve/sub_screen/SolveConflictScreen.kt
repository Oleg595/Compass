package modules.screens.solve.sub_screen

import context.UserContext
import csstype.*
import csstype.Position.Companion.sticky
import emotion.react.css
import kotlinx.js.jso
import react.FC
import react.Props
import react.create
import react.dom.html.ReactHTML
import react.useContext

private external interface RuleComponentConflictProps : Props {
    // заменить на RuleEntity
    var rule: String
    var result: String
}

private val RuleComponentConflict = FC<RuleComponentConflictProps> { props ->
    ReactHTML.div {
        css {
            width = 100.pct
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
        }
        ReactHTML.button {
            css {
                border = Border(1.px, LineStyle.solid, NamedColor.black)
                borderRadius = 3.px
                textAlign = TextAlign.center
                color = NamedColor.white
                backgroundColor = rgba(192, 185, 28, 1.0)
                fontSize = 14.px
            }
            onClick = {}
            +props.rule
        }
        ReactHTML.i {
            className = ClassName("fa-solid fa-arrow-down")
            style = jso {
                width = 16.px
                color = NamedColor.black
            }
        }
        ReactHTML.label {
            css {
                color = rgba(97, 103, 127, 1.0)
                fontSize = 14.px
            }
            +props.result
        }
        ReactHTML.i {
            className = ClassName("fa-solid fa-arrow-down")
            style = jso {
                width = 16.px
                color = NamedColor.black
            }
        }
    }
}

external interface SolveConflictScreenProps : SolveSubScreenProps {
//    var rules: List<RuleEntity>
//    var alt1: AlternativeEntity
//    var alt2: AlternativeEntity
}

val SolveConflictScreen = FC<SolveConflictScreenProps> { props ->
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
        +"Разрешите конфликт"
    }
    ReactHTML.div {
        val userContext = useContext(UserContext)
        style = jso {
            width = 100.pct
            height = 100.pct
            position = Position.relative
            border = Border(1.px, LineStyle.solid, NamedColor.black)
            borderRadius = 3.px
            borderColor = NamedColor.black
            overflowX = Auto.auto
            overflowY = Auto.auto
        }
        ReactHTML.div {
            style = jso {
                height = 5.px
            }
        }
        var tempAlt = userContext.dataContext.alts[0]
        val criteriaNames = userContext.dataContext.getCriteriaNames()
        ReactHTML.div {
            css {
                alignItems = AlignItems.center
                display = Display.flex
                flexDirection = FlexDirection.column
            }
            ReactHTML.label {
                css {
                    color = rgba(97, 103, 127, 1.0)
                    fontSize = 14.px
                }
                +tempAlt.toString(criteriaNames)
            }
            ReactHTML.i {
                className = ClassName("fa-solid fa-arrow-down")
                style = jso {
                    width = 16.px
                    color = NamedColor.black
                }
            }
        }
        +RuleComponentConflict.create {
            val criteriaValues = HashMap(tempAlt.criteriaToValue)
            criteriaValues["Количество персонала"] = "201-500"
            criteriaValues["Сумма инвестиций в \$"] = "236361005"
            tempAlt = tempAlt.copy(criteriaToValue = criteriaValues)
            rule = "(Количество персонала: 11-50, Сумма инвестиций в \$: 949600000) эквивалентно (Количество " +
                "персонала: 201-500, Сумма инвестиций" +
                " в \$: 236361005)"
            result = tempAlt.toString(criteriaNames)
        }
        +RuleComponentConflict.create {
            val criteriaValues = HashMap(tempAlt.criteriaToValue)
            criteriaValues["Количество персонала"] = "11-50"
            criteriaValues["Сумма инвестиций в \$"] = "366500000"
            criteriaValues["Количество инвесторов"] = "23"
            tempAlt = tempAlt.copy(criteriaToValue = criteriaValues)
            rule =
                "(Количество персонала: 201-500, Сумма инвестиций в \$: 236361005, Количество инвесторов: 2) лучше " +
                    "(Количество персонала: 11-50, Сумма инвестиций в \$: 366500000, Количество инвесторов: 23)"
            result = tempAlt.toString(criteriaNames)
        }
        +RuleComponentConflict.create {
            val criteriaValues = HashMap(tempAlt.criteriaToValue)
            criteriaValues["Сумма инвестиций в \$"] = "949600000"
            criteriaValues["Количество инвесторов"] = "8"
            tempAlt = tempAlt.copy(criteriaToValue = criteriaValues)
            rule =
                "(Сумма инвестиций в \$: 366500000, Количество инвесторов: 23) лучше " +
                    "(Сумма инвестиций в \$: 949600000, Количество инвесторов: 8)"
            result = tempAlt.toString(criteriaNames)
        }
        ReactHTML.label {
            css {
                color = rgba(97, 103, 127, 1.0)
                fontSize = 14.px
                display = Display.flex
                flexDirection = FlexDirection.column
                alignItems = AlignItems.center
            }
            +tempAlt.toString(criteriaNames)
        }
    }
}
