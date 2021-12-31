package objects

import android.net.Uri

class ToSendFile {

    var interName : String = String()

    var server : String
    var port : Int
    var user : String
    var pass : String

    var filePaths : ArrayList<Uri> = ArrayList()

    constructor(_server : String, _port : Int, _user : String, _pass : String) {

        server = _server
        port = _port
        user = _user
        pass = _pass
    }
}