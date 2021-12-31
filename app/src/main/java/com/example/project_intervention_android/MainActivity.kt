package com.example.project_intervention_android

import objects.AsyncFactory
import objects.RequestFactory
import objects.ToSendFile
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.coroutines.*
import popUps.MessagePopUp

class MainActivity : AppCompatActivity() {

    // Global variables
    private var selectedFile : Uri? = null
    private var toSendFile : ToSendFile = ToSendFile("", 0, "", "")

    private var interNameText : EditText? = null
    private var imageViewer : ImageView? = null
    private var imageNumber : TextView? = null
    private var imageCross : ImageButton? = null
    private var imageArrowRight : ImageButton? = null
    private var imageArrowLeft : ImageButton? = null
    private var imageRecycleBin : ImageButton? = null

    private val job =  SupervisorJob()
    private val asyncFactory = AsyncFactory()
    private val requestFactory = RequestFactory()

    private val ioScope by lazy { CoroutineScope(job + Dispatchers.IO) }

    private var fileList = ArrayList<Uri>()
    private var indexActualImage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Link Layout
        setContentView(R.layout.activity_main)

        // Link Layout's variables
        val fileSearchButton : Button = findViewById(R.id.FileSearchButton)
        val fileSearchName : TextView = findViewById(R.id.FileSearchText)
        val sendButton : Button = findViewById(R.id.SendButton)
        interNameText = findViewById(R.id.InterventionName)
        imageViewer = findViewById(R.id.ImageViewer)
        imageNumber = findViewById(R.id.ImageNumber)
        imageCross = findViewById(R.id.ImageCross)
        imageArrowRight = findViewById(R.id.ImageArrowRight)
        imageArrowLeft = findViewById(R.id.ImageArrowLeft)
        imageRecycleBin = findViewById(R.id.ImageRecycleBin)

        // Other variables

        // Change Layout
        fileSearchButton.text = getString(R.string.fileSearchButtonText)
        sendButton.text = getString(R.string.sendButton)
        fileSearchName.text = selectedFile?.path

        // On Click Listeners
        fileSearchButton.setOnClickListener {

            // File Explorer Opening
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(Intent.createChooser(intent, "Select Pictures"), 111)
        }
        sendButton.setOnClickListener {

            // Files Sending
            toSendFile.interName = interNameText!!.text.toString()
            toSendFile.filePaths = fileList

            asyncFactory.execSynchronous(this, ioScope, ::sendButtonAsyncFun)
        }

        // Remove all images selected
        imageRecycleBin!!.setOnClickListener {

            val dialogClickListener = DialogInterface.OnClickListener { _, which ->
                when (which) {

                    DialogInterface.BUTTON_POSITIVE -> recycleBinFun()

                    DialogInterface.BUTTON_NEGATIVE -> { }
                }
            }

            val builder = AlertDialog.Builder(this)
                .setTitle(getString(R.string.popUpTitle))
                .setMessage(getString(R.string.popUpAsking))
                .setPositiveButton("YES",dialogClickListener)
                .setNegativeButton("NO",dialogClickListener)
                .setNeutralButton("CANCEL",dialogClickListener)

            val dialog = builder.create()
            dialog.show()
        }

        // Remove the actual viewed image
        imageCross!!.setOnClickListener {

            fileList.remove(fileList[indexActualImage])

            if (indexActualImage > 0 && fileList.size > 0) {

                indexActualImage--
            }
            else if (fileList.size == 0) {

                fileList = ArrayList()
            }
            uiUpdate()
        }

        // Look at the image on the left
        imageArrowLeft!!.setOnClickListener {

            indexActualImage--
            uiUpdate()
        }

        // Look at the image on the right
        imageArrowRight!!.setOnClickListener {

            indexActualImage++
            uiUpdate()
        }
    }

    // Function out for dialog pop up
    private fun recycleBinFun() {

        fileList = ArrayList()
        uiUpdate()
    }

    // Update the different UI buttons before one has been pressed
    private fun uiUpdate() {

        imageNumber!!.text = fileList.size.toString()

        if(fileList.size > 0) {

            imageViewer!!.setImageURI(fileList[indexActualImage])
        }
        else {
            indexActualImage = 0
            imageViewer!!.setImageURI(Uri.EMPTY)
        }

        when (fileList.size) {
            0 -> {
                imageArrowLeft!!.visibility = View.INVISIBLE
                imageArrowRight!!.visibility = View.INVISIBLE
                imageCross!!.visibility = View.INVISIBLE
                imageRecycleBin!!.visibility = View.INVISIBLE

            } 1 -> {
                imageArrowLeft!!.visibility = View.INVISIBLE
                imageArrowRight!!.visibility = View.INVISIBLE
                imageCross!!.visibility = View.VISIBLE
                imageRecycleBin!!.visibility = View.VISIBLE

            } else -> {
                when (indexActualImage) {

                    fileList.size - 1 -> {
                        imageArrowLeft!!.visibility = View.VISIBLE
                        imageArrowRight!!.visibility = View.INVISIBLE

                    } 0 -> {
                        imageArrowLeft!!.visibility = View.INVISIBLE
                        imageArrowRight!!.visibility = View.VISIBLE

                    } else -> {
                        imageArrowLeft!!.visibility = View.VISIBLE
                        imageArrowRight!!.visibility = View.VISIBLE
                    }
                }
                imageCross!!.visibility = View.VISIBLE
                imageRecycleBin!!.visibility = View.VISIBLE
            }
        }
    }

    // Function to run asynchronous
    private suspend fun sendButtonAsyncFun() {

        val text: String = requestSendingAsync()

        deliverPopUp(text)
    }

    // Request launch
    private suspend fun requestSendingAsync(): String {

        return requestFactory.launchRequest(toSendFile)
    }

    // Information Pop Up about Request response
    private fun deliverPopUp(text: String) {

        val intent = Intent(this, MessagePopUp::class.java)
        intent.putExtra("pop up text", text)
        startActivity(intent)
    }

    // File Explorer Closing
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == Activity.RESULT_OK && data != null) {
            fileList.add(data.data!!)

            val params: ViewGroup.LayoutParams = imageViewer!!.layoutParams as ViewGroup.LayoutParams
            params.width = 600
            params.height = 1200
            imageViewer!!.layoutParams = params

            uiUpdate()
        }
    }
}