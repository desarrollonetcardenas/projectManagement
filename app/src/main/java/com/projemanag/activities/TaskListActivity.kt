package com.projemanag.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.projemanag.R
import com.projemanag.adapters.TaskListItemsAdapter
import com.projemanag.databinding.ActivityTaskListBinding
import com.projemanag.firebase.FirestoreClass
import com.projemanag.models.Board
import com.projemanag.models.Task
import com.projemanag.utils.Constants

class TaskListActivity : BaseActivity() {

    private lateinit var binding: ActivityTaskListBinding
    private lateinit var boardDocumentId: String
    private lateinit var mBoardDetails: Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTaskListBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        if(intent.hasExtra(Constants.DOCUMENT_ID)) {

            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
            showProgressDialog(R.string.please_wait.toString())
            FirestoreClass().getBoardDetails(this, boardDocumentId)
        }

    }

    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarTaskListActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mBoardDetails.name
        }

        binding.toolbarTaskListActivity.setNavigationOnClickListener { onBackPressed() }
    }

    fun boardDetails(board: Board) {

        mBoardDetails = board

        hideProgressDialog()
        setupActionBar()

        val itemTask = Task(R.string.add_list.toString())
        board.taskList.add(itemTask)

        /*Set layoutManager settings to our taskLists recyclerView*/
        binding.rvTaskList.layoutManager = LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        )
        binding.rvTaskList.setHasFixedSize(true)

        /*Prepare and set the adapter to our taskList recyclerView*/
        var adapter = TaskListItemsAdapter(this, board.taskList)
        binding.rvTaskList.adapter = adapter
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()
        showProgressDialog(R.string.please_wait.toString())
        FirestoreClass().getBoardDetails(this, mBoardDetails.documentId)
    }

    fun createTaskList(taskLisTittle: String) {

        val task = Task(taskLisTittle, FirestoreClass().getCurrentUserID())

        /*Ignore the last item, cause is the "add new task" option*/
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        mBoardDetails.taskList.add(0, task)

        showProgressDialog(R.string.please_wait.toString())
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)

    }

    fun updateTaskList(position: Int, listName: String, model: Task) {

        val task = Task(listName, model.createdBy)

        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(R.string.please_wait.toString())
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    fun deleteTaskList(position: Int) {

        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)

        showProgressDialog(R.string.please_wait.toString())
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

}