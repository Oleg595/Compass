package modules.screens.data.sub_screen

import context.UserContext
import context.UserData
import kotlinx.browser.window
import kotlinx.js.jso
import modules.common.table.DrawTableComponent
import modules.common.table.createColumnForAlternativeName
import modules.common.table.createColumnsForAllCriterias
import modules.screens.data.buttons.BackButton
import modules.screens.data.buttons.DownloadButton
import modules.screens.data.buttons.NextButton
import org.example.AlternativeEntity
import org.example.CriteriaEntity
import react.FC
import react.StateSetter
import react.create
import react.dom.aria.ariaLabel
import react.dom.html.InputType
import react.dom.html.ReactHTML.input
import react.useContext
import react.useEffectOnce
import react.useState
import tanstack.react.table.useReactTable
import tanstack.table.core.ColumnDefTemplate
import tanstack.table.core.ColumnFiltersState
import tanstack.table.core.RowSelectionState
import tanstack.table.core.SortingState
import tanstack.table.core.StringOrTemplateHeader
import tanstack.table.core.getCoreRowModel
import tanstack.table.core.getFilteredRowModel
import tanstack.table.core.getPaginationRowModel
import tanstack.table.core.getSortedRowModel
import kotlin.collections.set
import kotlin.js.Json

private fun createAlternative(jsonValue: Json, criterias: List<CriteriaEntity>): AlternativeEntity {
    val altCriterias = mutableMapOf<String, String>()
    criterias.forEach { criteria ->
        val criteriaName = criteria.name
        val value = jsonValue[criteriaName] as String
        criteria.addValue(value)
        altCriterias[criteriaName] = value
    }
    return AlternativeEntity(
        id = jsonValue["id"].toString().toInt(), name = jsonValue["name"] as String, altCriterias
    )
}

private fun deleteById(
    id: Int, alternatives: MutableList<AlternativeEntity>, userData: UserData
) {
    val alternative = alternatives.firstOrNull { alt -> alt.id == id }
    alternative?.criteriaToValue?.forEach { (name, value) ->
        val criteria = userData.dataContext.getCriteriaByNameOrNull(name) ?: return@forEach
        if (!alternatives.any { alt -> alt.criteriaToValue[name] == value }) {
            if (value != null) criteria.removeValue(value)
        }
    }
    alternatives.remove(alternative)
}

private fun setAlternatives(stateSetter: StateSetter<Array<Json>>) {
    useEffectOnce {
        window.fetch("http://localhost:8081/data/getAlternatives", jso {
            method = "get"
            headers = {
                "Content-Type" to "application/json"
            }
        })
            .then {
                it.json().then { alts -> stateSetter.invoke(alts as Array<Json>) }
            }
    }
}

val ChooseAlternativeScreen = FC<DataSubScreenProps> { properties ->
    val userContext = useContext(UserContext)
    val alternativesState = useState(arrayOf<Json>())
    setAlternatives(alternativesState.component2())
    userContext.isDataChoosen = false
    val cols = mutableListOf(
        jso {
            id = "select"
            header = StringOrTemplateHeader("")
            cell = ColumnDefTemplate { row ->
                val (isChecked, setChecked) = useState(userContext.dataContext.alts.any { alt ->
                    alt.id == (row.row.original["id"].toString().toInt())
                })
                input.create {
                    type = InputType.checkbox
                    checked = isChecked
                    onChange = { _ ->
                        if (isChecked) {
                            deleteById(row.row.original["id"] as Int, userContext.dataContext.alts, userContext)
                        } else {
                            val alt = createAlternative(row.row.original, userContext.dataContext.criterias)
                            userContext.dataContext.alts.add(alt)
                        }
                        setChecked(!isChecked)
                    }
                    ariaLabel = "Select row"
                }
            }
        },
        createColumnForAlternativeName()
    )
    cols.addAll(createColumnsForAllCriterias())

    val (_, setSorting) = useState<SortingState>()
    val (_, setColumnFilters) = useState<ColumnFiltersState>()
    val (_, setRowSelection) = useState<RowSelectionState>()
    val alternativeTable = useReactTable<Json>(jso {
        data = alternativesState.component1()
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
        title = "Выберите альтернативы"
    }
    +DrawTableComponent.create {
        table = alternativeTable
    }
    +NextButton.create {
        subScreenSetter = properties.subScreenSetter
        nextSubScreen = DataSubScreen.VIEW_DATA
    }
    +DownloadButton.create {
        subScreenSetter = properties.subScreenSetter
    }
    +BackButton.create {
        subScreenSetter = properties.subScreenSetter
        backSubScreen = DataSubScreen.CHOOSE_CRITERIA
    }
}
