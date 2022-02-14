package objects

class User {

    var id : String? = String()
    var password : String? = String()
    var userFolder : String? = String()
    var shortcutList : ArrayList<String>? = ArrayList()
    var filePath : String = "/dossier partage techs"

    constructor() : super()

    constructor(_id : String?, _password : String?, _userFolder : String?, _shortcutList : ArrayList<String>?, _filePath : String?) : super() {

        id = _id
        password = _password
        userFolder = _userFolder
        shortcutList = _shortcutList
        if (_filePath != null) filePath = _filePath
    }
}