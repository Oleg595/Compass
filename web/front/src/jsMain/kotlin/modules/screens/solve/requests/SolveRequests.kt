package modules.screens.solve.requests

import context.UserContext
import kotlinx.browser.window
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromDynamic
import org.example.AlternativeComparsionEntity
import org.w3c.fetch.RequestInit
import react.StateInstance
import react.useContext
import react.useEffectOnce
import react.useState
import kotlin.js.json

fun getAlternativeComparsions(): StateInstance<List<AlternativeComparsionEntity>?> {
    val dataContext = useContext(UserContext).dataContext
    val result = useState<List<AlternativeComparsionEntity>>()
    useEffectOnce {
        window.fetch(
            "http://localhost:8081/compass/findComparsionAlternatives",
            RequestInit(
                method = "POST",
                headers = json("Content-Type" to "application/json"),
                body = Json.encodeToString(dataContext)
            )
        ).then {
            it.json().then {
                val comparsions = Json.decodeFromDynamic<List<AlternativeComparsionEntity>>(it)
                result.component2().invoke(comparsions)
            }
        }
    }
    return result
}
