package android.kotlin.projectmanagement

import android.graphics.Typeface
import android.kotlin.projectmanagement.databinding.ActivitySplashBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager

class SplashActivity : AppCompatActivity() {
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


    }
}