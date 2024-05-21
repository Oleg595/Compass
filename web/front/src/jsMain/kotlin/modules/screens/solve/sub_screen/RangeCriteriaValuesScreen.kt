package modules.screens.solve.sub_screen

import context.UserContext
import csstype.*
import csstype.Position.Companion.absolute
import csstype.Position.Companion.relative
import csstype.Position.Companion.sticky
import emotion.react.css
import kotlinx.js.jso
import org.example.CriteriaEntity
import react.FC
import react.StateInstance
import react.dom.html.InputType
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.header
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.useContext
import react.useState

external interface RangeCriteriaValuesScreenProps : SolveSubScreenProps

val RangeCriteriaValuesScreen = FC<RangeCriteriaValuesScreenProps> { props ->
    val userContext = useContext(UserContext)
    val criterias = userContext.dataContext.criterias
    val fillState = useState(false)
    val criteriaState: StateInstance<CriteriaEntity?> =
        if (criterias.isNotEmpty()) useState(criterias.first())
        else useState()
    criteriaState.component1()?.let {
        header {
            css {
                marginTop = 1.pct
                marginLeft = Auto.auto
                marginRight = Auto.auto
                position = sticky
                fontWeight = FontWeight.bold
                fontSize = 18.px
                color = NamedColor.black
            }
            +"Укажите ранги значений для критерия ${criteriaState.component1()!!.name}"
        }
        div {
            css {
                height = 90.pct
                width = 100.pct
                marginTop = 3.pct
                position = sticky
                display = Display.flex
            }
            div {
                css {
                    width = 45.pct
                    left = 0.pct
                    position = sticky
                    backgroundColor = NamedColor.white
                    overflowX = Auto.auto
                    overflowY = Overflow.hidden
                    whiteSpace = WhiteSpace.nowrap
                }
                val criteriaValues = criteriaState.component1()!!.values
                criteriaValues.forEach { value ->
                    div {
                        css {
                            height = 25.px
                            width = 200.px
                            marginTop = 5.px
                            display = Display.block
                            color = rgba(192, 185, 28, 1.0)
                            backgroundColor = NamedColor.white
                        }
                        label {
                            css {
                                marginRight = 2.pct
                                fontSize = 16.px
                                lineHeight = 1.2.em
                                color = rgba(97, 103, 127, 1.0)
                                width = 150.px
                            }
                            +value
                        }
                        input {
                            key = "${criteriaState.component1()!!}:$value"
                            type = InputType.number
                            defaultValue = criteriaState.component1()!!.valueToPrior[value].toString()
                            css {
                                width = 40.px
                                height = 20.px
                                right = 0.px
                                position = relative
                                border = Border(1.px, LineStyle.solid, NamedColor.black)
                                borderRadius = 3.px
                                float = Float.right
                                clear = Clear.both
                            }
                            step = 1.0
                            min = 1.0
                            max = criteriaValues.size
                            onChange = { event ->
                                if (event.target.value.toIntOrNull() != null) {
                                    criteriaState.component1()!!.setPrior(value, event.target.value.toInt())
                                } else {
                                    criteriaState.component1()!!.removePrior(value)
                                }
                                fillState.component2().invoke(criteriaState.component1()!!.allValuesHavePrior())
                            }
                        }
                    }
                }
            }
            div {
                css {
                    width = 40.pct
                    left = 50.pct
                    position = sticky
                    display = Display.block
                    backgroundColor = NamedColor.white
                    overflowX = Overflow.hidden
                    overflowY = Auto.auto
                    whiteSpace = WhiteSpace.nowrap
                }
                criterias.forEach { criteria ->
                    val backgroundColor =
                        if (criteria.allValuesHavePrior()) rgba(192, 185, 28, 1.0) else rgba(97, 103, 127, 1.0)
                    div {
                        button {
                            css {
                                height = 25.px
                                width = 85.pct
                                marginTop = 5.px
                                color = NamedColor.white
                                this.backgroundColor = backgroundColor
                                borderRadius = 3.px
                            }
                            +criteria.name
                            onClick = { event ->
                                criteriaState.component2().invoke(criteria)
                                fillState.component2().invoke(criteria.allValuesHavePrior())
                            }
                        }
                        if (criteriaState.component1()!! == criteria) ReactHTML.i {
                            className = ClassName("fa-regular fa-circle")
                            style = jso {
                                color = backgroundColor
                                marginLeft = 2.pct
                                width = 5.pct
                            }
                        }
                    }
                }
            }
        }
        button {
            css {
                top = 92.pct
                left = 85.pct
                position = absolute
                width = 120.px
                height = 25.px
                backgroundColor = rgba(192, 185, 28, 1.0)
                color = NamedColor.white
                borderRadius = 3.px
                disabled {
                    backgroundColor = rgba(97, 103, 127, 1.0)
                }
            }
            disabled = criterias.any { criteria -> !criteria.allValuesHavePrior() }
            onClick = { event ->
                userContext.dataContext.formRulesByCriteriaValues()
                props.solveSubScreen.component2().invoke(SolveSubScreen.COMPARE)
            }
            +"К вопросам"
        }
    }
}
