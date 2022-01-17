package objects

import android.content.Context
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.*
import android.graphics.Bitmap
import androidx.documentfile.provider.DocumentFile
import java.io.File
import android.util.Base64
import java.io.ByteArrayOutputStream


class RequestFactory {

    private var ftpClient : FTPClient? = null

    suspend fun launchRequest(context: Context, toSendFile: ToSendFile): Exception? {

        try {

            val (isConnect, connectException) = ftpSetUp(toSendFile)

            var sourceFile: DocumentFile
            var input: InputStream?

            for (uri in toSendFile.filePaths) {

                sourceFile = DocumentFile.fromSingleUri(context, uri)!!

                if (sourceFile.exists()) {

                    input = context.contentResolver.openInputStream(uri)
                    ftpClient!!.storeFile(toSendFile.interName, input)
                    input!!.close()
                }
            }

            for (bitmap in toSendFile.bitmapPictures) {

                val file = File(bitMapToString(bitmap)!!)

                if (file.exists()) {

                    input = FileInputStream(file)
                    ftpClient!!.storeFile(toSendFile.interName, input)
                    input.close()

                }
            }
            ftpClient!!.logout()
            ftpClient!!.disconnect()

            if (!isConnect) {

                return connectException
            }
        } catch (e: Exception) {

            return e
        }
        return null
    }

    private fun ftpSetUp(toSendFile: ToSendFile): Pair<Boolean, Exception?> {

        ftpClient = FTPClient()
        var bool = false

        try {

            ftpClient!!.connect(toSendFile.server, toSendFile.port)
            bool = ftpClient!!.login(toSendFile.user, toSendFile.pass)
            ftpClient!!.setFileType(FTP.BINARY_FILE_TYPE)
        }
        catch (e: Exception)
        {
            return Pair(bool, e)
        }
        return Pair(bool, null)
    }

    private fun bitMapToString(bitmap: Bitmap): String? {

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 60, outputStream)
        val bytes = outputStream.toByteArray()

        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }
}