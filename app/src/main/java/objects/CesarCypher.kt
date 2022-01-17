package objects

import android.content.res.Resources
import com.example.project_intervention_android.R

class CesarCypher(_gear : String) {

    private val magicNumber : Int = 17
    private val gear : String = _gear

    fun cypher(str : String) : String {

        var newStr = String()
        var i : Int
        var index : Int

        for (letter in str) {

            i = 0
            index = 0

            for (l in gear) {

                if (letter == l) i = index
                index ++
            }
            i += magicNumber

            while (i >= gear.length)  i -= gear.length
            newStr += gear[i]
        }
        return newStr
    }

    fun decipher(str : String) : String {

        var newStr = String()
        var i : Int
        var index : Int

        for (letter in str) {

            i = 0
            index = 0

            for (l in gear) {

                if (letter == l) i = index
                index ++
            }
            i -= magicNumber

            while (i < 0)  i += gear.length
            newStr += gear[i]
        }
        return newStr
    }
}