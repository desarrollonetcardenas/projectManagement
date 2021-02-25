package android.kotlin.projectmanagement.activities

import android.kotlin.projectmanagement.R
import android.kotlin.projectmanagement.databinding.ActivitySingInBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager

class SingInActivity : BaseActivity() {
    private lateinit var binding: ActivitySingInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingInBinding.inflate(layoutInflater)
        val view = binding.root

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(view)

        setupActionBar()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarSignInActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_black_24dp)
        }

        binding.toolbarSignInActivity.setNavigationOnClickListener{
            onBackPressed()
        }

    }

}