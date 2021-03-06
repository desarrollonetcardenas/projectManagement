package android.kotlin.projectmanagement.activities

import android.content.Intent
import android.kotlin.projectmanagement.R
import android.kotlin.projectmanagement.databinding.ActivitySingInBinding
import android.kotlin.projectmanagement.firebase.FirestoreClass
import android.kotlin.projectmanagement.models.User
import android.kotlin.projectmanagement.utils.SingValidations
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {
    private lateinit var binding: ActivitySingInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySingInBinding.inflate(layoutInflater)
        val view = binding.root

        bootstrapValues()
        setContentView(view)

        binding.btnSignIn.setOnClickListener {
            signInRegisteredUser()
        }
    }

    private fun bootstrapValues() {

        /*Initialize auth variable*/
        auth = FirebaseAuth.getInstance()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setupActionBar()
    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarSignInActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_black_24dp)
        }

        binding.toolbarSignInActivity.setNavigationOnClickListener {
            onBackPressed()
        }

    }
    private fun signInRegisteredUser() {

        val email = binding.etEmail.text.toString().trim { it <= ' ' }
        val password = binding.etPassword.text.toString().trim { it <= ' ' }
        val user = SingValidations()

        if (user.signInValidate(email, password)) {

            showProgressDialog(resources.getString(R.string.please_wait))

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        FirestoreClass().signInUser(this)
                    } else {

                        // If sign in fails, display a message to the user.
                        Log.w("SingIn", "User sing in failed", task.exception)
                        Toast.makeText(
                            this, "Sing In authentication failed.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

    }

    fun singInSuccess(loggedInUser: User) {

        hideProgressDialog()

        startActivity(Intent(this, MainActivity::class.java))

        finish()
    }

}
