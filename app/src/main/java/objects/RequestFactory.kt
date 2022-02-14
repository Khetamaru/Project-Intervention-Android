package objects

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import com.google.common.base.Utf8
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.*
import java.net.InetAddress
import java.util.*


class RequestFactory {

    private var ftpClient = FTPClient()

    suspend fun launchRequest(context: Context, toSendFile: ToSendFile): Exception? {

        try {

            val connectException = ftpSetUp(toSendFile)

            var sourceFile: File
            var input: InputStream?

            ftpClient.sendCommand("OPTS UTF8","ON")

            ftpClient.changeWorkingDirectory("/dossier partage techs")

            toSendFile.userFolder.split("/").forEach {
                folder ->
                    ftpClient.makeDirectory(folder)
                    ftpClient.changeWorkingDirectory(folder)
            }

            toSendFile.interName.split("/").forEach {
                inter ->
                    ftpClient.makeDirectory(inter)
                    ftpClient.changeWorkingDirectory(inter)
            }

            for (uri in toSendFile.filePaths) {

                sourceFile = File(FileUtils(context).getPath(uri)!!)

                if (sourceFile.exists()) {

                    input = FileInputStream(sourceFile)
                    ftpClient.storeFile(sourceFile.name, input)
                    input.close()
                }
            }
            ftpClient.logout()
            ftpClient.disconnect()

            if (connectException != null) {

                return connectException
            }
        } catch (e: Exception) {

            return e
        }
        return null
    }

    fun ftpSetUp(toSendFile: ToSendFile): Exception? {

        try {
            ftpClient.connect(InetAddress.getByName(toSendFile.server), toSendFile.port)
            ftpClient.enterLocalPassiveMode()
            if (!ftpClient.login(toSendFile.user.id, toSendFile.user.password)) {
                throw IllegalStateException(
                    "Login failed. The response from the server is: " +
                            ftpClient.replyString
                )
            }
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE)
        } catch (e: Exception) {
            return e
        }
        return null
    }

    private fun bitMapToString(bitmap: Bitmap): String? {

        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 60, outputStream)
        val bytes = outputStream.toByteArray()

        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }
}