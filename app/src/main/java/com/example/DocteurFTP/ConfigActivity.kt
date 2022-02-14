package com.example.DocteurFTP

import android.app.ActionBar
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import baseAdapter.ShortcutAdapter
import objects.POJOFactory
import objects.User

class ConfigActivity : AppCompatActivity() {

    private var user : User? = null
    var pojoFactory : POJOFactory? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Link Layout
        setContentView(R.layout.activity_config)

        // Link Layout's variables
        val saveFilePathButton : ImageButton = findViewById(R.id.saveFilePathButton)
        val saveShortcutButton : ImageButton = findViewById(R.id.saveShortcutButton)
        val filePathText : EditText = findViewById(R.id.filePathText)
        val shortcutText : EditText = findViewById(R.id.shortcutText)
        val listViewShortcut : ListView = findViewById(R.id.listViewShortcut)
        pojoFactory = POJOFactory(cacheDir.absolutePath)
        user = pojoFactory!!.readJSONFromFile()

        // Other variables
        val adapter = ShortcutAdapter(this, user!!.shortcutList!!)
        if (adapter.count > 5) {

            val item: View = adapter.getView(0, null, listViewShortcut)
            item.measure(0, 0)
            val params = ViewGroup.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                5 * item.measuredHeight
            )
            listViewShortcut.layoutParams = params
        }
        listViewShortcut.adapter = adapter

        // Change Layout
        filePathText.setText(user!!.filePath)

        // On Click Listeners
        saveFilePathButton.setOnClickListener {

            user!!.filePath = filePathText.text.toString()
            pojoFactory!!.writeJSONToFile(user!!)
        }
        saveShortcutButton.setOnClickListener {

            user!!.shortcutList!!.add(shortcutText.text.toString())
            pojoFactory!!.writeJSONToFile(user!!)
            reloadActivity()
        }
    }

    fun deleteShortcut(position: Int) {

        user!!.shortcutList!!.remove(user!!.shortcutList!![position])
        pojoFactory!!.writeJSONToFile(user!!)
        reloadActivity()
    }

    fun saveShortcut(currentText: String, position : Int) {

        user!!.shortcutList!![position] = currentText
        pojoFactory!!.writeJSONToFile(user!!)
        reloadActivity()
    }

    fun reloadActivity() {

        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}