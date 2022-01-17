package com.example.project_intervention_android

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import objects.CesarCypher
import objects.Login
import objects.POJOFactory
import popUps.MessagePopUp

class LogActivity : AppCompatActivity() {

    private var userIdEditText : EditText? = null
    private var userPasswordEditText : EditText? = null
    private var rememberCheckBox : CheckBox? = null
    private var connexionButton : Button? = null

    private var pojoFactory : POJOFactory? = null
    private var login : Login = Login()
    private var cesarCypher : CesarCypher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Link Layout
        setContentView(R.layout.activity_log)

        // Link Layout's variables
        userIdEditText = findViewById(R.id.userId)
        userPasswordEditText = findViewById(R.id.userPassword)
        rememberCheckBox = findViewById(R.id.rememberCheckBox)
        connexionButton = findViewById(R.id.connexionButton)

        // Other variables
        pojoFactory = POJOFactory(cacheDir.absolutePath + "/login.json")
        cesarCypher = CesarCypher(getString(R.string.gear))

        checkRememberFile()

        connexionButton!!.setOnClickListener {

            if (!errorCheck()) {

                if (rememberCheckBox!!.isChecked) {

                    rememberFileCreation()
                }
                else {

                    rememberFileDelete()
                }
                logIn(userIdEditText!!.text.toString(), userPasswordEditText!!.text.toString())
            }
        }
    }

    private fun checkRememberFile() {

        if (pojoFactory!!.doesJSONExist()) {

            login = pojoFactory!!.readJSONFromFile()
            login.password = cesarCypher!!.decipher(login.password)
            setLogs(login)
        }
    }

    private fun setLogs(login: Login) {

        userIdEditText!!.setText(login.id)
        userPasswordEditText!!.setText(login.password)
        rememberCheckBox!!.isChecked = true
    }

    private fun rememberFileCreation() {

        pojoFactory!!.writeJSONToFile(userIdEditText!!.text.toString(), cesarCypher!!.cypher(userPasswordEditText!!.text.toString()))
    }

    private fun rememberFileDelete() {

        pojoFactory!!.deleteJSON()
    }

    private fun errorCheck() : Boolean {

        when {
            userIdEditText!!.text.toString() == String() -> {

                errorCatch(getString(R.string.userIdError))
                return true
            }
            userPasswordEditText!!.text.toString() == String() -> {

                errorCatch(getString(R.string.userPasswordError))
                return true
            }
            !checkConnexionIsOk() -> {

                errorCatch(getString(R.string.userConnexionError))
                return true
            }
        }
        return false
    }

    private fun checkConnexionIsOk() : Boolean {

        return true
        TODO("Set up connection with server")
    }

    private fun errorCatch(text: String) {
        // Show Error details
        val intent = Intent(this@LogActivity, MessagePopUp::class.java)
        intent.putExtra("pop up text", text)
        startActivity(intent)
    }

    private fun logIn(id : String, password : String) {

        val intent = Intent(this@LogActivity, MainActivity::class.java)
        intent.putExtra("userId", id)
        intent.putExtra("userPassword", password)
        startActivity(intent)
        finish()
    }
}