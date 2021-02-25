package android.kotlin.projectmanagement.activities

import android.kotlin.projectmanagement.R
import android.kotlin.projectmanagement.databinding.ActivitySplashBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}