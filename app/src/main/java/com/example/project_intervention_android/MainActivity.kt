package com.example.project_intervention_android

import objects.AsyncFactory
import objects.RequestFactory
import objects.ToSendFile
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.*
import popUps.MessagePopUp

class MainActivity : AppCompatActivity() {

    // Global variables
    private var pictureSearchActivityResult : ActivityResultLauncher<Intent>? = null
    private var pictureTakeActivityResult : ActivityResultLauncher<Intent>? = null
    private var toSendFile : ToSendFile? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Link Layout
        setContentView(R.layout.activity_main)

        // Link Layout's variables
        val fileSearchButton : Button = findViewById(R.id.FileSearchButton)
        val sendButton : Button = findViewById(R.id.SendButton)
        val sendWithoutTest : Button = findViewById(R.id.SendWithoutTest)
        val cameraButton : ImageButton = findViewById(R.id.cameraButton)
        interNameText = findViewById(R.id.InterventionName)
        imageViewer = findViewById(R.id.ImageViewer)
        imageNumber = findViewById(R.id.ImageNumber)
        imageCross = findViewById(R.id.ImageCross)
        imageArrowRight = findViewById(R.id.ImageArrowRight)
        imageArrowLeft = findViewById(R.id.ImageArrowLeft)
        imageRecycleBin = findViewById(R.id.ImageRecycleBin)

        // Other Variable
        toSendFile = ToSendFile(
            intent.getStringExtra("userId").toString(),
            intent.getStringExtra("userPassword").toString()
        )

        // Change Layout
        fileSearchButton.text = getString(R.string.fileSearchButtonText)
        sendButton.text = getString(R.string.sendButton)
        sendWithoutTest.text = getString(R.string.sendWithoutTest)

        val params: ViewGroup.LayoutParams = imageViewer!!.layoutParams as ViewGroup.LayoutParams
        params.width = 600
        params.height = 1200
        imageViewer!!.layoutParams = params

        // Set Up File Selection Activity
        pictureSearchActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {

                val data = it.data

                if (data!!.data != null) {

                    val uri: Uri = data.data!!
                    if (!redundancyCheck(uri)) {

                        toSendFile!!.filePaths.add(uri)

                        uiUpdate()
                    } else deliverPopUp(getString(R.string.multipleExemplarImageError))
                }
                else if (data.clipData != null) {

                    var i = 0
                    var uri: Uri?
                    var bool = false

                    while (i < data.clipData!!.itemCount) {

                        uri = data.clipData!!.getItemAt(i).uri
                        if (!redundancyCheck(uri!!)) {

                            toSendFile!!.filePaths.add(uri)
                        }
                        else bool = true
                        i++
                    }

                    uiUpdate()

                    if (bool) deliverPopUp(getString(R.string.multipleExemplarImageError))
                }
                else {

                    errorNumber(901)
                }
            }
        }

        pictureTakeActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {

                val image = Bitmap.createBitmap(it.data!!.extras!!.get("data") as Bitmap)
                toSendFile!!.bitmapPictures.add(image)

                uiUpdate()
            }
        }

        // On Click Listeners
        fileSearchButton.setOnClickListener {

            // File Explorer Opening
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            pictureSearchActivityResult!!.launch(intent)
        }

        cameraButton.setOnClickListener {

            // Take picture directly with phone camera
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            pictureTakeActivityResult!!.launch(intent)
        }

        sendButton.setOnClickListener {

            val error : String? = errorCheck()
            if (error == null) {
                // Files Sending
                toSendFile!!.interName = interNameText!!.text.toString()

                asyncFactory.execSynchronous(
                    ioScope,
                    ::sendButtonAsyncFun,
                    ::errorCatch
                )
            }
            else {
                val intent = Intent(this@MainActivity, MessagePopUp::class.java)
                intent.putExtra("pop up text", errorMessage(error))
                startActivity(intent)
            }
        }

        sendWithoutTest.setOnClickListener {

            // Files Sending
            toSendFile!!.interName = interNameText!!.text.toString()

            asyncFactory.execSynchronous(
                ioScope,
                ::sendButtonAsyncFun,
                ::errorCatch
            )
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

            if (toSendFile!!.isIndexBitmap()) {

                toSendFile!!.removeBitmapPicture(toSendFile!!.bitmapPictures[toSendFile!!.indexBitmap()])

            } else {

                toSendFile!!.removeFilePath(toSendFile!!.filePaths[toSendFile!!.index])
            }

            if (toSendFile!!.index > 0 && toSendFile!!.totalSize() > 0) { toSendFile!!.index-- }
            if (toSendFile!!.filePaths.size == 0) {  toSendFile!!.resetFilePaths() }
            if (toSendFile!!.bitmapPictures.size == 0) { toSendFile!!.resetBitmapPictures() }

            uiUpdate()
        }

        // Look at the image on the left
        imageArrowLeft!!.setOnClickListener {

            toSendFile!!.index--
            uiUpdate()
        }

        // Look at the image on the right
        imageArrowRight!!.setOnClickListener {

            toSendFile!!.index++
            uiUpdate()
        }
    }

    // Function out for dialog pop up
    private fun recycleBinFun() {

        toSendFile!!.resetFilePaths()
        toSendFile!!.resetBitmapPictures()
        uiUpdate()
    }

    // Update the different UI buttons before one has been pressed
    private fun uiUpdate() {

        imageNumber!!.text = (toSendFile!!.totalSize()).toString()

        if((toSendFile!!.areArraysEmpty()) && toSendFile!!.index < toSendFile!!.totalSize()) {

            if (toSendFile!!.isIndexBitmap()) {

                imageViewer!!.setImageBitmap(toSendFile!!.bitmapPictures[toSendFile!!.indexBitmap()])
            }
            else {

                imageViewer!!.setImageURI(toSendFile!!.filePaths[toSendFile!!.index])
            }
        }
        else {
            toSendFile!!.resetIndex()
            imageViewer!!.setImageURI(Uri.EMPTY)
        }

        when (toSendFile!!.filePaths.size + toSendFile!!.bitmapPictures.size) {
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
                when (toSendFile!!.index) {

                    toSendFile!!.filePaths.size + toSendFile!!.bitmapPictures.size - 1 -> {
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

        val text: java.lang.Exception? = requestSendingAsync()

        if(text != null) errorCatch(text)
    }

    // Request launch
    private suspend fun requestSendingAsync(): java.lang.Exception? {

        return requestFactory.launchRequest(this@MainActivity, toSendFile!!)
    }

    // Information Pop Up about Request response
    private fun deliverPopUp(text: String) {

        val intent = Intent(this@MainActivity, MessagePopUp::class.java)
        intent.putExtra("pop up text", errorMessage(text))
        startActivity(intent)
    }

    // Information Pop Up about Request response
    private fun errorCatch(e: Exception) {
        // Show Error details
        val intent = Intent(this@MainActivity, MessagePopUp::class.java)
        intent.putExtra("pop up text", errorMessage(e.message!!))
        startActivity(intent)
    }

    private fun errorCheck() : String? {

        if (interNameText!!.text.toString() == String()) {

            return errorMessage(getString(R.string.noInterNameError))
        }
        if (toSendFile!!.filePaths.size + toSendFile!!.bitmapPictures.size == 0) {

            return errorMessage(getString(R.string.noImageError))
        }
        if (toSendFile!!.filePaths.size < 0 || toSendFile!!.bitmapPictures.size < 0) {

            return errorNumber(901)
        }
        return null
    }

    private fun redundancyCheck(data: Uri): Boolean {

        if (toSendFile!!.filePaths.size > 0) for (file in toSendFile!!.filePaths) if (file == data) return true

        return false
    }

    private fun errorMessage(eMessage : String) : String {

        return when (eMessage) {
            "Connection is not open" -> {
                getString(R.string.noServerFind) + errorNumber(400)
            }
            getString(R.string.noInterNameError) -> {
                getString(R.string.noInterNameError) + errorNumber(900)
            }
            getString(R.string.noImageError) -> {
                getString(R.string.noImageError) + errorNumber(900)
            }
            getString(R.string.multipleExemplarImageError) -> {
                getString(R.string.multipleExemplarImageError) + errorNumber(900)
            }
            else -> {

                eMessage
            }
        }
    }

    private fun errorNumber(eNumber : Int) : String {

        when (eNumber) {

            200 -> return getString(R.string.n200)

            400 -> return getString(R.string.n400)
            401 -> return getString(R.string.n401)
            402 -> return getString(R.string.n402)
            403 -> return getString(R.string.n403)
            404 -> return getString(R.string.n404)
            405 -> return getString(R.string.n405)
            406 -> return getString(R.string.n406)
            407 -> return getString(R.string.n407)
            408 -> return getString(R.string.n408)
            409 -> return getString(R.string.n409)
            410 -> return getString(R.string.n410)
            411 -> return getString(R.string.n411)
            412 -> return getString(R.string.n412)
            413 -> return getString(R.string.n413)
            414 -> return getString(R.string.n414)
            415 -> return getString(R.string.n415)
            416 -> return getString(R.string.n416)
            417 -> return getString(R.string.n417)
            418 -> return getString(R.string.n418)
            421 -> return getString(R.string.n421)
            422 -> return getString(R.string.n422)
            423 -> return getString(R.string.n423)
            424 -> return getString(R.string.n424)
            425 -> return getString(R.string.n425)
            426 -> return getString(R.string.n426)
            428 -> return getString(R.string.n428)
            429 -> return getString(R.string.n429)
            431 -> return getString(R.string.n431)
            444 -> return getString(R.string.n444)
            449 -> return getString(R.string.n449)
            450 -> return getString(R.string.n450)
            451 -> return getString(R.string.n451)
            456 -> return getString(R.string.n456)
            495 -> return getString(R.string.n495)
            496 -> return getString(R.string.n496)
            497 -> return getString(R.string.n497)
            498 -> return getString(R.string.n498)
            499 -> return getString(R.string.n499)

            500 -> return getString(R.string.n500)
            501 -> return getString(R.string.n501)
            502 -> return getString(R.string.n502)
            503 -> return getString(R.string.n503)
            504 -> return getString(R.string.n504)
            505 -> return getString(R.string.n505)
            506 -> return getString(R.string.n506)
            507 -> return getString(R.string.n507)
            508 -> return getString(R.string.n508)
            509 -> return getString(R.string.n509)
            510 -> return getString(R.string.n510)
            511 -> return getString(R.string.n511)
            520 -> return getString(R.string.n520)
            521 -> return getString(R.string.n521)
            522 -> return getString(R.string.n522)
            523 -> return getString(R.string.n523)
            524 -> return getString(R.string.n524)
            525 -> return getString(R.string.n525)
            526 -> return getString(R.string.n526)
            527 -> return getString(R.string.n527)

            900 -> return getString(R.string.n900)
            901 -> return getString(R.string.n901)

            else -> return getString(R.string.n901)
        }
    }
}