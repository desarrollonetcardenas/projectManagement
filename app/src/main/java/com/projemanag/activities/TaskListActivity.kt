package com.projemanag.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.projemanag.R
import com.projemanag.databinding.ActivityTaskListBinding
import com.projemanag.firebase.FirestoreClass
import com.projemanag.models.Board
import com.projemanag.utils.Constants

class TaskListActivity : BaseActivity() {

    private lateinit var binding: ActivityTaskListBinding
    private lateinit var boardDocumentId: String

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

    private fun setupActionBar(title: String) {

        setSupportActionBar(binding.toolbarTaskListActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = title
        }

        binding.toolbarTaskListActivity.setNavigationOnClickListener { onBackPressed() }
    }

    fun boardDetails(board: Board) {
        hideProgressDialog()
        setupActionBar(board.name)
    }
}