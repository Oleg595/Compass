package modules.screens.result.sub_screen

import components.modal.Modal
import context.UserContext
import csstype.*
import emotion.css.css
import emotion.react.css
import kotlinx.js.jso
import org.example.AlternativeEntity
import react.FC
import react.Props
import react.StateInstance
import react.create
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.i
import react.dom.html.ReactHTML.label
import react.useContext

private fun parseEdgeId(
    edgeId: String,
    alts: Collection<AlternativeEntity>
): Pair<AlternativeEntity, AlternativeEntity> {
    val ids = edgeId.split(":")
    val alt1 = alts.first { alt -> alt.id.toString() == ids.component1() }
    val alt2 = alts.first { alt -> alt.id.toString() == ids.component2() }
    return Pair(alt1, alt2)
}

private val modalCssClassName = css(jso {
    position = Position.absolute
    height = 70.pct
    width = 70.pct
    backgroundColor = NamedColor.white
    color = rgba(97, 103, 127, 1.0)
    textAlign = TextAlign.left
    marginLeft = 15.pct
    marginTop = 5.pct
})

private external interface RuleComponentProps : Props {
    // заменить на RuleEntity
    var rule: String
    var result: String
}

private val RuleComponent = FC<RuleComponentProps> { props ->
    div {
        css {
            width = 100.pct
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
        }
        button {
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
        i {
            className = ClassName("fa-solid fa-arrow-down")
            style = jso {
                width = 16.px
                color = NamedColor.black
            }
        }
        label {
            css {
                color = rgba(97, 103, 127, 1.0)
                fontSize = 14.px
            }
            +props.result
        }
        i {
            className = ClassName("fa-solid fa-arrow-down")
            style = jso {
                width = 16.px
                color = NamedColor.black
            }
        }
    }
}

external interface ResultModalProps : Props {
    var modalState: StateInstance<Boolean>
    var chooseEdge: String
}

val ResultModalComponent = FC<ResultModalProps> { props ->
    +Modal.create {
        className = modalCssClassName
        isOpen = props.modalState.component1()
        onRequestClose = {
            props.modalState.component2().invoke(false)
        }
        contentLabel = "content"
        div {
            val userContext = useContext(UserContext)
            val altPair = parseEdgeId(props.chooseEdge, userContext.dataContext.alts)
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
            div {
                style = jso {
                    height = 5.px
                }
            }
            var tempAlt = altPair.first
            val criteriaNames = userContext.dataContext.getCriteriaNames()
            div {
                css {
                    alignItems = AlignItems.center
                    display = Display.flex
                    flexDirection = FlexDirection.column
                }
                label {
                    css {
                        color = rgba(97, 103, 127, 1.0)
                        fontSize = 14.px
                    }
                    +"Альтернатива ${tempAlt.name} = ${tempAlt.toString(criteriaNames)}"
                }
                i {
                    className = ClassName("fa-solid fa-arrow-down")
                    style = jso {
                        width = 16.px
                        color = NamedColor.black
                    }
                }
            }
            +RuleComponent.create {
                val criteriaValues = HashMap(tempAlt.criteriaToValue)
                criteriaValues["Сумма инвестиций в \$"] = "29500000"
                tempAlt = tempAlt.copy(criteriaToValue = criteriaValues)
                rule = "(Сумма инвестиций в \$: 366500000) эквивалентно (Сумма инвестиций в \$: 29500000)"
                result = tempAlt.toString(criteriaNames)
            }
            +RuleComponent.create {
                val criteriaValues = HashMap(tempAlt.criteriaToValue)
                criteriaValues["Категория"] = "Cybersecurity"
                criteriaValues["Количество инвесторов"] = "1"
                tempAlt = tempAlt.copy(criteriaToValue = criteriaValues)
                rule =
                    "(Категория: FinTech, Количество инвесторов: 23) лучше (Категория: Cybersecurity, Количество инвесторов: 1)"
                result = tempAlt.toString(criteriaNames)
            }
            label {
                css {
                    color = rgba(97, 103, 127, 1.0)
                    fontSize = 14.px
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    alignItems = AlignItems.center
                }
                +"Альтернатива ${altPair.second.name} = ${altPair.second.toString(criteriaNames)}"
            }
        }
        button {
            style = jso {
                bottom = 3.pct
                left = 42.pct
                width = 16.pct
                position = Position.absolute
                textAlign = TextAlign.center
            }
            onClick = {
                props.modalState.component2().invoke(false)
            }
            +"Закрыть окно"
        }
    }
}
