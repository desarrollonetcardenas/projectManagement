package android.kotlin.projectmanagement.activities

import android.content.Intent
import android.graphics.Typeface
import android.kotlin.projectmanagement.databinding.ActivitySplashBinding
import android.kotlin.projectmanagement.firebase.FirestoreClass
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager

class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val typeFace: Typeface =
            Typeface.createFromAsset(assets, "carbon bl.ttf")

        binding.tvAppName.typeface = typeFace

        Handler().postDelayed({

            var currentUserID = FirestoreClass().getCurrentUserID()

            if (currentUserID.isNotEmpty())
                startActivity(Intent(this, MainActivity::class.java))
            else
                startActivity(Intent(this, IntroActivity::class.java))

            finish()

        }, 2500)

    }
}