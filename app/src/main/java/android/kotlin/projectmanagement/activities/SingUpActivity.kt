package android.kotlin.projectmanagement.activities

import android.kotlin.projectmanagement.R
import android.kotlin.projectmanagement.databinding.ActivitySingUpBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast

class SingUpActivity : BaseActivity() {

    private lateinit var binding: ActivitySingUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        binding.btnSignUp.setOnClickListener {
            registerUser()
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarSignUpActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_black_24dp)
        }

        binding.toolbarSignUpActivity.setNavigationOnClickListener{
            onBackPressed()
        }
    }

    private fun registerUser() {
        val name : String = binding.etName.text.toString().trim { it <= ' ' }
        val email : String = binding.etEmail.text.toString().trim { it <= ' ' }
        val password : String = binding.etPassword.text.toString().trim { it <= ' ' }

        if(validateForm(name, email, password)) {
            Toast.makeText(this,
                "Now can register a new user",
                Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter a name")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter an email")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter a password")
                false
            }
            else -> {
                true
            }
        }
    }

}