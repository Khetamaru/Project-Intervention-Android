package popUps

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.ColorUtils
import com.example.DocteurFTP.R

class MessagePopUp : AppCompatActivity() {

    private var popupTitle = ""
    private var popupText = ""
    private var popupButton = ""
    private var popupWindowBackground : ConstraintLayout? = null
    private var popupWindowViewWithBorder : CardView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0, 0)
        setContentView(R.layout.message_pop_up)

        val bundle = intent.extras
        popupTitle = getString(R.string.popUpTitle)
        popupText = bundle?.getString("pop up text", "Text") ?: ""
        popupButton = getString(R.string.popUpBtn)

        val popupWindowTitle : AppCompatTextView = findViewById(R.id.popup_window_title)
        val popupWindowText : AppCompatTextView = findViewById(R.id.popup_window_text)
        val popupWindowButton : Button = findViewById(R.id.popup_window_button)
        popupWindowBackground = findViewById(R.id.popup_window_background_container)
        popupWindowViewWithBorder = findViewById(R.id.popup_window_view)

        popupWindowTitle.text = popupTitle
        popupWindowText.text = popupText
        popupWindowButton.text = popupButton

        this.window.statusBarColor = Color.TRANSPARENT

        val alpha = 100 //between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), Color.TRANSPARENT, alphaColor)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            popupWindowBackground?.setBackgroundColor(animator.animatedValue as Int)
        }
        colorAnimation.start()

        popupWindowViewWithBorder?.alpha = 0f
        popupWindowViewWithBorder?.animate()?.alpha(1f)?.setDuration(500)?.setInterpolator(
            DecelerateInterpolator()
        )?.start()

        popupWindowButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        // Fade animation for the background of Popup Window when you press the back button
        val alpha = 100 // between 0-255
        val alphaColor = ColorUtils.setAlphaComponent(Color.parseColor("#000000"), alpha)
        val colorAnimation = ValueAnimator.ofObject(ArgbEvaluator(), alphaColor, Color.TRANSPARENT)
        colorAnimation.duration = 500 // milliseconds
        colorAnimation.addUpdateListener { animator ->
            popupWindowBackground?.setBackgroundColor(
                animator.animatedValue as Int
            )
        }

        // Fade animation for the Popup Window when you press the back button
        popupWindowViewWithBorder?.animate()?.alpha(0f)?.setDuration(500)?.setInterpolator(
            DecelerateInterpolator()
        )?.start()

        // After animation finish, close the Activity
        colorAnimation.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                finish()
                overridePendingTransition(0, 0)
            }
        })
        colorAnimation.start()
    }
}