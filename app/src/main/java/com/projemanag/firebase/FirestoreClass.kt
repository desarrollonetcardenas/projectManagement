package com.projemanag.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.projemanag.activities.SignInActivity
import com.projemanag.activities.SignUpActivity
import com.projemanag.model.User
import com.projemanag.utils.Constants

/**
 * A custom class where we will add the operation performed for the firestore database.
 */
class FirestoreClass {

    // Create a instance of Firebase Firestore
    private val mFireStore = FirebaseFirestore.getInstance()

    /**
     * A function to make an entry of the registered user in the firestore database.
     */
    fun registerUser(activity: SignUpActivity, userInfo: User) {

        mFireStore.collection(Constants.USERS)
                // Document ID for users fields. Here the document it is the User ID.
                .document(getCurrentUserID())
                // Here the userInfo are Field and the SetOption is set to merge. It is for if we wants to merge
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener {

                    // Here call a function of base activity for transferring the result to it.
                    activity.userRegisteredSuccess()
                }
                .addOnFailureListener { e ->
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error writing document",
                            e
                    )
                }
    }

    /**
     * A function to SignIn using firebase and get the user details from Firestore Database.
     */
    fun signInUser(activity: SignInActivity) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
                // The document id to get the Fields of user.
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->
                    Log.e(
                            activity.javaClass.simpleName, document.toString()
                    )

                    // Here we have received the document snapshot which is converted into the User Data model object.
                    val loggedInUser = document.toObject(User::class.java)!!

                    // Here call a function of base activity for transferring the result to it.
                    activity.signInSuccess(loggedInUser)
                }
                .addOnFailureListener { e ->
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while getting loggedIn user details",
                            e
                    )
                }
    }

    /**
     * A function for getting the user id of current logged user.
     */
    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        // A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }
}