package objects

import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.reflect.KFunction1
import kotlin.reflect.KSuspendFunction0

class AsyncFactory : AppCompatActivity() {

    fun execSynchronous(scope: CoroutineScope, coroutine: KSuspendFunction0<Unit>, errorCatch: KFunction1<String, Unit>) {

        // Coroutine Start
        scope.launch {
            try {
                // Function Execution
                coroutine()
            }
            catch (e: Exception) {
                // Show Error details
                errorCatch(e.toString())
            }
        }
    }
}