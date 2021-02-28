package android.kotlin.projectmanagement.activities

import android.kotlin.projectmanagement.R
import android.kotlin.projectmanagement.databinding.ActivitySingUpBinding
import android.kotlin.projectmanagement.utils.SingValidations
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

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
        val user = SingValidations()


        if(user.signUpValidate(name, email, password)) {

            showProgressDialog(R.string.please_wait.toString())

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        task ->
                        hideProgressDialog()

                        if (task.isSuccessful) {
                            try {

                                val firebaseUser: FirebaseUser = task.result!!.user!!
                                val registeredEmail = firebaseUser.email!!

                                Toast.makeText(this,
                                        "$name you have successfully registered the email $registeredEmail ",
                                        Toast.LENGTH_SHORT)
                                        .show()

                                FirebaseAuth.getInstance().signOut()
                                finish()

                            } catch (e: Exception) {
                            }
                        } else {
                            Toast.makeText(this,
                                    "Sorry we could´nt register your user. Please contact your administrator. Err: ${task.exception!!.message}",
                                    Toast.LENGTH_SHORT)
                                    .show()
                        }
                    }
        }

    }


}