package baseAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.example.DocteurFTP.ConfigActivity
import com.example.DocteurFTP.R


class ShortcutAdapter(_context: Context, _shortcuts: ArrayList<String>) : BaseAdapter() {

    private var context : Context = _context
    private var shortcuts : ArrayList<String> = _shortcuts

    override fun getCount(): Int {
        return shortcuts.size //returns total of items in the list
    }

    override fun getItem(position: Int): Any {
        return shortcuts[position] //returns list item at the specified position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, _convertView: View?, parent: ViewGroup?): View {
        // inflate the layout for each list row
        var convertView : View? = _convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                .inflate(R.layout.row_shortcut, parent, false)
        }

        // get current item to be displayed
        val currentShortcut = getItem(position) as String

        // get the TextView for item name and item description
        val textViewShortcutName = convertView!!.findViewById(R.id.contentText) as TextView

        val deleteButton = convertView.findViewById(R.id.deleteButton) as ImageButton
        val saveButton = convertView.findViewById(R.id.saveButton) as ImageButton

        deleteButton.setOnClickListener {

            (context as ConfigActivity).deleteShortcut(position)
        }

        saveButton.setOnClickListener {

            (context as ConfigActivity).saveShortcut(textViewShortcutName.text.toString(), position)
        }

        //sets the text for item name and item description from the current item object
        textViewShortcutName.text = currentShortcut

        // returns the view for the current row
        return convertView
    }
}