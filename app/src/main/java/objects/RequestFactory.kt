package objects

import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import java.io.*


class RequestFactory {

    private var ftpClient : FTPClient? = null

    fun launchRequest(toSendFile: ToSendFile): String {

        ftpSetUp(toSendFile)

        try {
            var sourceFile: File
            var input: InputStream

            for (uri in toSendFile.filePaths) {

                sourceFile = File(uri.toString())

                if (sourceFile.exists()) {

                    input = FileInputStream(sourceFile)
                    ftpClient!!.storeFile(toSendFile.interName + "/jsonFile.json", input)
                    input.close()
                }
            }
            ftpClient!!.logout()
            ftpClient!!.disconnect()

        } catch (e: Exception) {

            return e.toString()
        }

        return "All Clear"
    }

    private fun ftpSetUp(toSendFile: ToSendFile) {

        ftpClient = FTPClient()

        try {

            ftpClient!!.connect(toSendFile.server, toSendFile.port)
            ftpClient!!.login(toSendFile.user, toSendFile.pass)
            ftpClient!!.setFileType(FTP.BINARY_FILE_TYPE)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}