package objects

import com.google.gson.Gson
import java.io.*

class POJOFactory(_filePath: String) {

    private val filePath : String = "$_filePath/user.json"

    fun writeJSONToFile(user: User) {

        val gson = Gson()

        val jsonString : String = gson.toJson(user)

        val file= File(filePath)

        file.writeText(jsonString)
    }

    fun readJSONFromFile() : User {

        val gson = Gson()

        val bufferedReader : BufferedReader = File(filePath).bufferedReader()
        val inputString = bufferedReader.use { it.readText() }

        return gson.fromJson(inputString, User::class.java)
    }

    fun doesJSONExist() : Boolean {

        if (File(filePath).isFile) return true
        return false
    }

    /*fun deleteJSON() {

        if (doesJSONExist()) File(filePath).delete()
    }*/
}