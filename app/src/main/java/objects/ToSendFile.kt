package objects

import android.graphics.Bitmap
import android.net.Uri

class ToSendFile(_user : User) {

    var userFolder: String = String()
    var interName : String = String()

    var server : String = "RGDEPANNAGE.synology.me"
    var port : Int = 1021

    var user : User = _user

    var filePaths : ArrayList<Uri> = ArrayList()

    var index : Int = 0

    fun resetFilePaths() { filePaths = ArrayList() }

    fun resetIndex() { index = 0 }

    fun removeFilePath(filePath : Uri) { filePaths.remove(filePath) }
}