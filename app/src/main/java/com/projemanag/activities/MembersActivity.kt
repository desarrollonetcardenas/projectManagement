package com.projemanag.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.projemanag.R
import com.projemanag.adapters.MemberListItemsAdapter
import com.projemanag.databinding.ActivityMembersBinding
import com.projemanag.firebase.FirestoreClass
import com.projemanag.model.User
import com.projemanag.models.Board
import com.projemanag.utils.Constants

class MembersActivity : BaseActivity() {

    private lateinit var binding: ActivityMembersBinding
    private lateinit var mBoardDetails: Board
    private lateinit var mAssignedMembersList: ArrayList<User>
    private var anyChangesMade : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        if(intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }

        setupActionBar()

        showProgressDialog(R.string.please_wait.toString())
        FirestoreClass().getAssignedMembersListDetails(this, mBoardDetails.assignedTo)
    }

    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarMembersActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.nav_my_profile)
        }

        binding.toolbarMembersActivity.setNavigationOnClickListener { onBackPressed() }
    }

    fun memberDetails(user: User) {

        mBoardDetails.assignedTo.add(user.id)
        FirestoreClass().assignMemberToBoard(this, mBoardDetails, user)

    }

    fun memberAssignedSuccess(user: User) {

        hideProgressDialog()
        mAssignedMembersList.add(user)

        anyChangesMade = true

        setupMembersList(mAssignedMembersList)

    }

    /*
    * Display all of the members List in the RecyclerView
    * */
    fun setupMembersList(members: ArrayList<User>) {

        mAssignedMembersList = members

        hideProgressDialog()
        binding.rvMembersList.layoutManager = LinearLayoutManager(this)
        binding.rvMembersList.setHasFixedSize(true)

        val adapter = MemberListItemsAdapter(this, members)
        binding.rvMembersList.adapter = adapter
    }


    override fun onBackPressed() {

        if(anyChangesMade) {
            setResult(Activity.RESULT_OK)
        }

        super.onBackPressed()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member -> {

                dialogAddMembers()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogAddMembers() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_search_member)
        dialog.findViewById<TextView>(R.id.tv_add)
            .setOnClickListener {

                val email = dialog.findViewById<TextView>(R.id.et_email_search_member)
                    .text.toString()

                if(email.isNotEmpty()) {

                    dialog.dismiss()
                    showProgressDialog(R.string.please_wait.toString())
                    FirestoreClass().getMemberDetails(this, email)

                } else {
                    Toast.makeText(this,
                            "Please enter members email address",
                            Toast.LENGTH_LONG)
                        .show()
                }
            }
        dialog.findViewById<TextView>(R.id.tv_cancel)
            .setOnClickListener {
                dialog.dismiss()
            }

        dialog.show()
    }

}