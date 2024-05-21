package modules.screens.data.sub_screen

import context.UserContext
import kotlinx.js.jso
import modules.common.table.DrawTableComponent
import modules.common.table.createColumnForAlternativeName
import modules.common.table.createColumnsForAllCriterias
import modules.common.table.userAlternativesToTableData
import modules.screens.data.buttons.BackButton
import modules.screens.data.buttons.UploadButton
import react.FC
import react.create
import react.useContext
import react.useState
import tanstack.react.table.useReactTable
import tanstack.table.core.ColumnFiltersState
import tanstack.table.core.RowSelectionState
import tanstack.table.core.SortingState
import tanstack.table.core.getCoreRowModel
import tanstack.table.core.getFilteredRowModel
import tanstack.table.core.getPaginationRowModel
import tanstack.table.core.getSortedRowModel
import kotlin.js.Json

val ViewDataScreen = FC <DataSubScreenProps> { props ->
    val userContext = useContext(UserContext)
    userContext.isDataChoosen = true

    val cols = mutableListOf(createColumnForAlternativeName())
    cols.addAll(createColumnsForAllCriterias())
    val (_, setSorting) = useState<SortingState>()
    val (_, setColumnFilters) = useState<ColumnFiltersState>()
    val (_, setRowSelection) = useState<RowSelectionState>()
    val alternativeTable = useReactTable<Json>(jso {
        data = userAlternativesToTableData().toTypedArray()
        columns = cols.toTypedArray()
        onSortingChange = { updater -> setSorting.invoke(updater.unsafeCast<SortingState>()) }
        onColumnFiltersChange = { updater -> setColumnFilters.invoke(updater.unsafeCast<ColumnFiltersState>()) }
        getCoreRowModel = getCoreRowModel()
        getPaginationRowModel = getPaginationRowModel()
        getSortedRowModel = getSortedRowModel()
        getFilteredRowModel = getFilteredRowModel()
        onRowSelectionChange = { updater -> setRowSelection.invoke(updater.unsafeCast<RowSelectionState>()) }
    })

    +SubScreenHeader.create {
        title = "Выбранные значения"
    }
    +DrawTableComponent.create {
        table = alternativeTable
    }
    +UploadButton.create()
    +BackButton.create {
        subScreenSetter = props.subScreenSetter
        backSubScreen = DataSubScreen.CHOOSE_ALTERNATIVE
    }
}
