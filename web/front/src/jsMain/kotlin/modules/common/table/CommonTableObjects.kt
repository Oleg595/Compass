package modules.common.table

import context.UserContext
import csstype.ClassName
import csstype.rgba
import kotlinx.js.jso
import org.example.AlternativeEntity
import react.create
import react.useContext
import react.useState
import tanstack.react.table.useReactTable
import tanstack.table.core.*
import kotlin.js.Json
import kotlin.js.json

private fun createTextColumn(id: String, header: String): ColumnDef<Json, String> {
    return jso {
        this.id = id
        this.header = StringOrTemplateHeader(header)
        accessorKey = id
        cell = ColumnDefTemplate { source ->
            react.dom.html.ReactHTML.div.create {
                className = ClassName("text-right font-medium")
                style = jso {
                    color = rgba(97, 103, 127, 1.0)
                }
                +source.getValue()
            }
        }
    }
}

fun createColumnForAlternativeName(): ColumnDef<Json, String> {
    return createTextColumn("name", "Альтернатива")
}

fun createColumnsForCriterias(criterias: Collection<String>): List<ColumnDef<Json, String>> {
    return criterias.map { criteria ->
        createTextColumn(criteria, criteria)
    }
}

fun createColumnsForAllCriterias(): List<ColumnDef<Json, String>> {
    val userContext = useContext(UserContext)
    return userContext.dataContext.getCriteriaNames().map { criteria ->
        createTextColumn(criteria, criteria)
    }
}

fun alternativesToTableData(alternatives: List<AlternativeEntity>): List<Json> {
    return alternatives.map { alt ->
        val json = json()
        json["name"] = alt.name
        alt.criteriaToValue.entries.forEach { entry ->
            json.set(propertyName = entry.key, value = entry.value)
        }
        json
    }
}

fun userAlternativesToTableData(): List<Json> {
    val userContext = useContext(UserContext)
    return alternativesToTableData(userContext.dataContext.alts)
}

fun createTable(columns: List<ColumnDef<Json, String>>, data: List<Json>): Table<Json> {
    val (_, setSorting) = useState<SortingState>()
    val (_, setColumnFilters) = useState<ColumnFiltersState>()
    val (_, setRowSelection) = useState<RowSelectionState>()
    return useReactTable(jso {
        this.data = data.toTypedArray()
        this.columns = columns.toTypedArray()
        onSortingChange = { updater -> setSorting.invoke(updater.unsafeCast<SortingState>()) }
        onColumnFiltersChange = { updater -> setColumnFilters.invoke(updater.unsafeCast<ColumnFiltersState>()) }
        getCoreRowModel = getCoreRowModel()
        getPaginationRowModel = getPaginationRowModel()
        getSortedRowModel = getSortedRowModel()
        getFilteredRowModel = getFilteredRowModel()
        onRowSelectionChange = { updater -> setRowSelection.invoke(updater.unsafeCast<RowSelectionState>()) }
    })
}
