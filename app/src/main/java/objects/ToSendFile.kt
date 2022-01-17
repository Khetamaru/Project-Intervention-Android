package objects

import android.graphics.Bitmap
import android.net.Uri

class ToSendFile(_user: String, _pass: String) {

    var interName : String = String()

    var server : String = "empty"
    var port : Int = 0

    var user : String = _user
    var pass : String = _pass

    var filePaths : ArrayList<Uri> = ArrayList()
    var bitmapPictures : ArrayList<Bitmap> = ArrayList()

    var index : Int = 0

    fun indexBitmap() : Int { return index - filePaths.size }
    fun totalSize() : Int { return filePaths.size + bitmapPictures.size }

    fun resetFilePaths() { filePaths = ArrayList() }
    fun resetBitmapPictures() { bitmapPictures = ArrayList() }
    fun resetIndex() { index = 0 }

    fun removeFilePath(filePath : Uri) { filePaths.remove(filePath) }
    fun removeBitmapPicture(bitmapPicture : Bitmap) { bitmapPictures.remove(bitmapPicture) }

    fun areArraysEmpty() : Boolean { return filePaths.size > 0 || bitmapPictures.size > 0 }
    fun isIndexBitmap() : Boolean { return index >= filePaths.size }
}