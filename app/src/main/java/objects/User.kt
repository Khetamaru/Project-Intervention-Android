package objects

class Login {

    var id : String = String()
    var password : String = String()

    constructor() : super()

    constructor(_id : String, _password : String) : super() {

        id = _id
        password = _password
    }
}