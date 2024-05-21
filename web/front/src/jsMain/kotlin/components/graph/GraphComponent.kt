@file:JsModule("react-graph-vis")
@file:JsNonModule

package components.graph

import react.CSSProperties
import react.ComponentClass
import react.Props

external interface GraphProps: Props {
    var graph: GraphComponent
    var style: CSSProperties
    var options: GraphOptions
    var events: GraphEvents
}

@JsName("default")
external val Graph: ComponentClass<GraphProps>
