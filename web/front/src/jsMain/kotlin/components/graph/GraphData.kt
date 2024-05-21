package components.graph

import csstype.IntegerType
import csstype.NamedColor

data class GraphNode (
    @JsName("id")
    val id: IntegerType,
    @JsName("label")
    val label: String,
    @JsName("title")
    val title: String,
    @JsName("fixed")
    val fixed: Boolean
)

data class GraphEdge (
    @JsName("id")
    val id: String,
    @JsName("from")
    val from: IntegerType,
    @JsName("to")
    val to: IntegerType
)

data class GraphComponent (
    @JsName("nodes")
    val nodes: Array<GraphNode>,
    @JsName("edges")
    val edges: Array<GraphEdge>
)

data class GraphHierarchicalOptions (
    @JsName("enabled")
    val enabled: Boolean,
    @JsName("sortMethod")
    val sortMethod: String,
    @JsName("shakeTowards")
    val shakeTowards: String,
    @JsName("direction")
    val direction: String,
    @JsName("levelSeparation")
    val levelSeparation: Int,
)

data class GraphLayoutOptions (
    @JsName("hierarchical")
    val hierarchical: GraphHierarchicalOptions
)

data class GraphEdgesOptions (
    @JsName("color")
    val color: NamedColor
)

data class GraphInteractionOptions (
    @JsName("dragNodes")
    val dragNodes: Boolean,
    @JsName("dragView")
    val dragView: Boolean
)

data class GraphPhysicsOptions (
    @JsName("enabled")
    val enabled: Boolean
)

data class GraphOptions (
    @JsName("autoResize")
    val autoResize: Boolean,
    @JsName("layout")
    val layoutOptions: GraphLayoutOptions,
    @JsName("edges")
    val edges: GraphEdgesOptions,
    @JsName("interaction")
    val interaction: GraphInteractionOptions,
    @JsName("physics")
    val physics: GraphPhysicsOptions
)

data class GraphEvents (
    @JsName("selectEdge")
    val selectEdge: (id: dynamic) -> Unit
)
