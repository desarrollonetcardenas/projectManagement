package android.kotlin.projectmanagement.activities

import android.kotlin.projectmanagement.R
import android.kotlin.projectmanagement.databinding.ActivitySingUpBinding
import android.kotlin.projectmanagement.firebase.FirestoreClass
import android.kotlin.projectmanagement.models.User
import android.kotlin.projectmanagement.utils.SingValidations
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {

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

        binding.toolbarSignUpActivity.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    private fun registerUser() {

        val name: String = binding.etName.text.toString().trim { it <= ' ' }
        val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim { it <= ' ' }
        val user = SingValidations()


        if (user.signUpValidate(name, email, password)) {

            showProgressDialog(R.string.please_wait.toString())

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener (
                    OnCompleteListener<AuthResult> { task ->

                        if (task.isSuccessful) {

                            // Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            // Registered Email
                            val registeredEmail = firebaseUser.email!!

                            val user = User(
                                firebaseUser.uid, name, registeredEmail
                            )

                            // call the registerUser function of FirestoreClass to make an entry in the database.
                            FirestoreClass().registerUser(this, user)
                        } else {
                            Toast.makeText(
                                this,
                                "Sorry we could´nt register your user. Please contact your administrator. Err: ${task.exception!!.message}",
                                Toast.LENGTH_SHORT
                            )
                                .show()

                        }
                    })
                }

        }


    fun userRegisteredSuccess() {

        Toast
            .makeText(this, "You have successfully registered", Toast.LENGTH_SHORT)
            .show()

        hideProgressDialog()

        FirebaseAuth.getInstance().signOut()

        finish()
    }

}
