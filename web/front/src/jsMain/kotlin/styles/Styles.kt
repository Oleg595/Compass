@file:Suppress(
    "NAME_CONTAINS_ILLEGAL_CHARS",
    "NESTED_CLASS_IN_EXTERNAL_INTERFACE",
)

package styles

import csstype.ClassName
import csstype.FontFamily
import csstype.FontWeight

// language=JavaScript
@JsName("""(/*union*/{inter: 'Inter'}/*union*/)""")
sealed external interface AppFontFamily {
    companion object {
        val inter: FontFamily
    }
}

// language=JavaScript
@JsName("""(/*union*/{medium: 'Medium', semiBold: 'Semi Bold'}/*union*/)""")
sealed external interface AppFontWeight {
    companion object {
        val medium: FontWeight
        val semiBold: FontWeight
    }
}

// language=JavaScript
@JsName("""(/*union*/{data: 'fa-solid fa-database', solve: 'fa-solid fa-pencil', solution: 'fa-solid fa-check', 
    toNext: 'fa-solid fa-forward'}/*union*/)""")
sealed external interface AppIcons {
    companion object {
        val data: ClassName
        val solve: ClassName
        val solution: ClassName
        val toNext: ClassName
    }
}

// language=JavaScript
@JsName("""(/*union*/{screenButton: 'screen button', roundedBorder: 'rounded-md border', wFull: 'w-full'}/*union*/)
""")
sealed external interface AppClassNames {
    companion object {
        val screenButton: ClassName
        val roundedBorder: ClassName
        val wFull: ClassName
    }
}
