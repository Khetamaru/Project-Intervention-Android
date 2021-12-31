package objects

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import popUps.MessagePopUp
import kotlin.reflect.KSuspendFunction0

class AsyncFactory : AppCompatActivity() {

    fun execSynchronous(context: Context, scope: CoroutineScope, coroutine: KSuspendFunction0<Unit>) {

        // Coroutine Start
        scope.launch {
            try {
                // Function Execution
                coroutine.call()
            }
            catch (e: Exception) {
                // Show Error details
                val intent = Intent(context, MessagePopUp::class.java)
                intent.putExtra("pop up text", "Error Hash Code : " + e.hashCode().toString() + " => " + "Error Message : " + e.message)
                startActivity(intent)
            }
        }
    }
}