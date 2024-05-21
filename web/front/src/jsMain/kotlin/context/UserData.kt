package context

import org.example.DataContext
import react.createContext

class UserData {
    var dataContext = DataContext(mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf(), mutableListOf())
    var k = 2
    var isDataChoosen = false
}

val UserContext = createContext<UserData>()
