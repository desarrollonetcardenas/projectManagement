package com.projemanag.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.projemanag.activities.*
import com.projemanag.model.User
import com.projemanag.models.Board
import com.projemanag.models.Task
import com.projemanag.utils.Constants

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
                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error writing document",
                            e
                    )
                }
    }

    fun createBoard(activity: CreateBoardActivity, boardInfo: Board) {
        mFireStore.collection(Constants.BOARDS)
                .document()
                .set(boardInfo, SetOptions.merge())
                .addOnSuccessListener {
                    activity.boardCreatedSuccessfully()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while creating a board",
                            e
                    )
                }
    }
    
    /**
     * A function to SignIn using firebase and get the user details from Firestore Database.
     */
    fun loadUserData(activity: Activity, readBoardsList: Boolean = false) {

        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.USERS)
                // The document id to get the Fields of user.
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->
                    Log.e(activity.javaClass.simpleName, document.toString())

                    // Here we have received the document snapshot which is converted into the User Data model object.
                    val loggedInUser = document.toObject(User::class.java)!!

                    // Here call a function of base activity for transferring the result to it.
                    when (activity) {
                        is SignInActivity -> {
                            activity.signInSuccess(loggedInUser)
                        }
                        is MainActivity -> {
                            activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                        }
                        is MyProfileActivity -> {
                            activity.setUserDataInUI(loggedInUser)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Here call a function of base activity for transferring the result to it.
                    when (activity) {
                        is SignInActivity -> {
                            activity.hideProgressDialog()
                        }
                        is MainActivity -> {
                            activity.hideProgressDialog()
                        }
                        is MyProfileActivity -> {
                            activity.hideProgressDialog()
                        }
                    }
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while getting loggedIn user details",
                            e
                    )
                }
    }

    /**
     * A function to update the user profile data into the database.
     */
    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS) // Collection Name
                .document(getCurrentUserID()) // Document ID
                .update(userHashMap) // A hashmap of fields which are to be updated.
                .addOnSuccessListener {
                    // Profile data is updated successfully.
                    Log.e(activity.javaClass.simpleName, "Profile Data updated successfully!")

                    Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

                    // Notify the success result.
                    activity.profileUpdateSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while creating a board.",
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


    fun getBoardsList(activity: MainActivity) {
        mFireStore
                .collection(Constants.BOARDS)
                .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserID())
                .get()
                .addOnSuccessListener {
                    document ->
                    Log.e(activity.javaClass.simpleName, document.documents.toString())

                    try {
                        val boardsList: ArrayList<Board> = ArrayList()

                        // A for loop as per the list of documents to convert them into Boards ArrayList.
                        for(item in document.documents) {
                            val board = item.toObject(Board::class.java)!!
                            board.documentId = item.id
                            boardsList.add(board)
                        }

                        activity.populateBoardsListToUI(boardsList)
                    }catch(e: Exception) {
                        Toast.makeText(activity, "Error occurred trying to load boards", Toast.LENGTH_LONG)
                                .show()
                        Log.e(javaClass.simpleName, e.message.toString())
                    }
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
                }


    }

    fun getBoardDetails(taskListActivity: TaskListActivity, boardDocumentId: String) {
        mFireStore
            .collection(Constants.BOARDS)
            .document(boardDocumentId)
            .get()
            .addOnSuccessListener {
                    document ->
                try {
                    Log.i(taskListActivity.javaClass.simpleName, document.toString())

                    val board = document.toObject(Board::class.java)!!
                    board.documentId = document.id

                    taskListActivity.boardDetails(board)
                }catch(e: Exception) {
                    Toast.makeText(taskListActivity, "Error occurred while getting board details", Toast.LENGTH_LONG)
                        .show()
                    Log.e(javaClass.simpleName, e.message.toString())
                }
            }
            .addOnFailureListener { e ->
                taskListActivity.hideProgressDialog()
                Log.e(taskListActivity.javaClass.simpleName, "Error accessing board details.", e)
            }
    }

    fun addUpdateTaskList(activity: TaskListActivity, board: Board) {

        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList

        mFireStore.collection(Constants.BOARDS)
                .document(board.documentId)
                .update(taskListHashMap)
                .addOnSuccessListener {
                    snapshot ->
                    Log.i(this.javaClass.simpleName, "Task List Updated Successfully")

                    activity.addUpdateTaskListSuccess()

                    Log.i(this.javaClass.name, "snapshot: $snapshot")
                }
                .addOnFailureListener { exception ->
                    Log.e(this.javaClass.simpleName, "Error updating the task list: ${exception.message}", exception)
                    activity.hideProgressDialog()
                }
    }

}