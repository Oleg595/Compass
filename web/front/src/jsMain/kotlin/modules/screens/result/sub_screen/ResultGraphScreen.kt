package modules.screens.result.sub_screen

import components.graph.*
import context.UserContext
import csstype.NamedColor
import csstype.integer
import csstype.pct
import kotlinx.js.jso
import org.example.AlternativeEntity
import react.FC
import react.Props
import react.create
import react.memo
import react.useContext
import react.useState

private fun generateGraphNodes(alts: List<AlternativeEntity>): Array<GraphNode> {
    return alts.map { alt ->
        GraphNode (
            id = integer(alt.id),
            label = alt.name,
            title = alt.name,
            fixed = false
        )
    }.toTypedArray()
}

private fun generateGraphEdges(): Array<GraphEdge> {
    return setOf(
        3 to 4,
        4 to 1
    ).map {
        GraphEdge(
            id = "${it.first}:${it.second}",
            from = integer(it.first),
            to = integer(it.second)
        )
    }.toTypedArray()
}

external interface ResultGraphScreenProps: Props

val ResultGraphScreen = FC<ResultGraphScreenProps> {
    val modalComponentState = useState(false)
    val edgeState = useState(String())
    +memo(Graph).create {
        val userContext = useContext(UserContext)
        graph = GraphComponent(
            nodes = generateGraphNodes(userContext.dataContext.alts),
            edges = generateGraphEdges()
        )
        style = jso {
            height = 100.pct
            width = 100.pct
        }
        options = GraphOptions(
            autoResize = true,
            layoutOptions = GraphLayoutOptions(
                hierarchical = GraphHierarchicalOptions(
                    enabled = true,
                    sortMethod = "directed",
                    shakeTowards = "roots",
                    direction = "UD",
                    levelSeparation = 200
                )
            ),
            edges = GraphEdgesOptions(
                color = NamedColor.black
            ),
            interaction = GraphInteractionOptions(
                dragNodes = true,
                dragView = true
            ),
            physics = GraphPhysicsOptions(
                enabled = false
            )
        )
        events = GraphEvents(
            selectEdge = { id ->
                val edge = id.edges[0] as String
                if ((id.nodes as Array<*>).isEmpty()) {
                    edgeState.component2().invoke(edge)
                    modalComponentState.component2().invoke(true)
                }
            }
        )
    }
    if (edgeState.component1().isNotEmpty()) {
        +ResultModalComponent.create {
            this.modalState = modalComponentState
            this.chooseEdge = edgeState.component1()
        }
    }
}
