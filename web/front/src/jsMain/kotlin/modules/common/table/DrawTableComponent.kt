@file:Suppress("UNUSED_EXPRESSION")

package modules.common.table

import csstype.Auto
import csstype.Border
import csstype.LineStyle
import csstype.NamedColor
import csstype.TextAlign
import csstype.px
import csstype.rem
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML
import tanstack.react.table.renderCell
import tanstack.react.table.renderHeader
import tanstack.table.core.Table
import kotlin.js.Json

external interface DrawTableComponentProps: Props {
    var table: Table<Json>
}

val DrawTableComponent = FC<DrawTableComponentProps> { props ->
    ReactHTML.table {
        css {
            marginTop = Auto.auto
            marginBottom = Auto.auto
            marginLeft = Auto.auto
            marginRight = Auto.auto
        }
        ReactHTML.thead {
            props.table.getHeaderGroups().forEach { headerGroup ->
                ReactHTML.tr {
                    key = headerGroup.id
                    headerGroup.headers.forEach { header ->
                        ReactHTML.th {
                            css {
                                textAlign = TextAlign.start
                                padding = 0.4.rem
                                border = Border(1.px, LineStyle.solid, NamedColor.black)
                                fontSize = 14.px
                                borderColor = NamedColor.black
                            }
                            key = header.id
                            if (header.isPlaceholder) null
                            else +renderHeader(header)
                        }
                    }
                }
            }
        }
        ReactHTML.tbody {
            props.table.getRowModel().rows.forEach { row ->
                ReactHTML.tr {
                    key = row.id
                    row.getVisibleCells().forEach { cell ->
                        ReactHTML.td {
                            css {
                                textAlign = TextAlign.start
                                padding = 0.4.rem
                                border = Border(1.px, LineStyle.solid, NamedColor.black)
                                fontSize = 14.px
                            }
                            key = cell.id
                            +renderCell(cell)
                        }
                    }
                }
            }
        }
    }
}
