package objects

import com.google.gson.Gson
import java.io.*

class POJOFactory(_filePath: String) {

    private val filePath : String = _filePath

    fun writeJSONToFile(ID : String, Password : String) {

        val login = Login(ID, Password)
        val gson = Gson()

        val jsonString : String = gson.toJson(login)

        val file= File(filePath)

        file.writeText(jsonString)
    }

    fun readJSONFromFile() : Login {

        val gson = Gson()

        val bufferedReader : BufferedReader = File(filePath).bufferedReader()
        val inputString = bufferedReader.use { it.readText() }

        return gson.fromJson(inputString, Login::class.java)
    }

    fun doesJSONExist() : Boolean {

        if (File(filePath).isFile) return true
        return false
    }

    fun deleteJSON() {

        if (doesJSONExist()) File(filePath).delete()
    }
}