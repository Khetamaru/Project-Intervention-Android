package com.example.DocteurFTP

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import objects.*
import popUps.MessagePopUp

class LogActivity : AppCompatActivity() {

    private var userIdEditText : EditText? = null
    private var userPasswordEditText : EditText? = null
    private var connexionButton : Button? = null

    private var pojoFactory : POJOFactory? = null
    private var requestFactory = RequestFactory()
    private var user : User = User()

    private val job =  SupervisorJob()
    private val ioScope by lazy { CoroutineScope(job + Dispatchers.IO) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Link Layout
        setContentView(R.layout.activity_log)

        // Link Layout's variables
        userIdEditText = findViewById(R.id.userId)
        userPasswordEditText = findViewById(R.id.userPassword)
        connexionButton = findViewById(R.id.connexionButton)

        // Other variables
        pojoFactory = POJOFactory(cacheDir.absolutePath)

        checkRememberFile()

        connexionButton!!.setOnClickListener {

            AsyncFactory().execSynchronous(
                ioScope,
                ::checkConnexionIsOk,
                ::errorCatch)
        }
    }

    private fun checkRememberFile() {

        if (pojoFactory!!.doesJSONExist()) {

            user = pojoFactory!!.readJSONFromFile()
            setLogs(user)
        }
        else {

            user = User()
        }
    }

    private fun setLogs(user: User) {

        userIdEditText!!.setText(user.id)
        userPasswordEditText!!.setText(user.password)
    }

    private fun rememberFileCreation() {

        user.id = userIdEditText!!.text.toString()
        user.password = userPasswordEditText!!.text.toString()
        pojoFactory!!.writeJSONToFile(user)
    }

    private fun errorCheck() : Boolean {

        when {
            userIdEditText!!.text.toString() == String() -> {

                errorCatch(getString(R.string.userIdError))
                return false
            }
            userPasswordEditText!!.text.toString() == String() -> {

                errorCatch(getString(R.string.userPasswordError))
                return false
            }
        }
        return true
    }

    private suspend fun checkConnexionIsOk() {

        val error = requestFactory.ftpSetUp(
            ToSendFile(user)
        )
        if (error == null && errorCheck()) {

            callBackFunc()
        }
        else {
            errorCatch(error.toString())
        }
    }

    private fun callBackFunc() {

        rememberFileCreation()
        logIn()
    }

    private fun errorCatch(text: String) {
        // Show Error details
        val intent = Intent(this@LogActivity, MessagePopUp::class.java)
        intent.putExtra("pop up text", text)
        startActivity(intent)
    }

    private fun logIn() {

        val intent = Intent(this@LogActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}