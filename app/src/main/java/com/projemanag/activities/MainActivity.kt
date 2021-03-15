package com.projemanag.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.projemanag.R
import com.projemanag.adapters.BoardItemsAdapter
import com.projemanag.databinding.ActivityMainBinding
import com.projemanag.firebase.FirestoreClass
import com.projemanag.model.User
import com.projemanag.models.Board
import com.projemanag.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding : ActivityMainBinding
    private lateinit var mUserName: String

    companion object{
        const val MY_PROFILE_REQUEST_CODE: Int = 11
        const val CREATE_BOARD_REQUEST_CODE: Int = 12
    }

    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        // This is used to align the xml view to this class
        setContentView(view)

        setupActionBar()

        // Assign the NavigationView.OnNavigationItemSelectedListener to navigation view.
        binding.navView.setNavigationItemSelectedListener(this)

        FirestoreClass().loadUserData(this, true)

        val fabCreateBoard = findViewById<FloatingActionButton>(R.id.fab_create_board)

        fabCreateBoard.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)

            intent.putExtra(Constants.NAME, mUserName)
            startActivityForResult(intent, Constants.CREATE_BOARD_REQUEST_CODE)
        }

    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // A double back press function is added in Base Activity.
            doubleBackToExit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
                && requestCode == MY_PROFILE_REQUEST_CODE) {

            FirestoreClass().loadUserData(this)
        }
        else if(requestCode == Constants.CREATE_BOARD_REQUEST_CODE
                && resultCode == Activity.RESULT_OK) {
            FirestoreClass().getBoardsList(this)
        }
        else {
            Log.e("Cancelled", "The user has cancelled the update profile action")
        }

    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_my_profile -> {
                startActivityForResult(
                        Intent(this
                                , MyProfileActivity::class.java)
                        , MY_PROFILE_REQUEST_CODE)
            }

            R.id.nav_sign_out -> {
                // Here sign outs the user from firebase in this device.
                FirebaseAuth.getInstance().signOut()

                // Send the user to the intro screen of the application.
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {

        val toolbar = findViewById<Toolbar>(R.id.toolbar_main_activity)

        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbar.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    /**
     * A function for opening and closing the Navigation Drawer.
     */
    private fun toggleDrawer() {

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    fun updateNavigationUserDetails(user: User, readBoardsList: Boolean) {

        mUserName = user.name

        val navUserImageView = findViewById<ImageView>(R.id.nav_user_image)

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImageView);

        val tvUserName = findViewById<TextView>(R.id.tv_username)
        tvUserName.text = user.name

        if(readBoardsList){
            showProgressDialog(R.string.please_wait.toString())
            FirestoreClass().getBoardsList(this)
        }

    }

    fun populateBoardsListToUI(boardsList: ArrayList<Board>) {

        hideProgressDialog()

        val rvBoardsList = findViewById<RecyclerView>(R.id.rv_boards_list)
        val tvNoBoardsAvailable = findViewById<TextView>(R.id.tv_no_boards_available)

        if(boardsList.size > 0) {

            rvBoardsList.visibility = View.VISIBLE
            tvNoBoardsAvailable.visibility = View.GONE

            rvBoardsList.layoutManager = LinearLayoutManager(this)
            rvBoardsList.setHasFixedSize(true)

            var adapter = BoardItemsAdapter(this, boardsList)
            rvBoardsList.adapter = adapter


            /*onClick event linked to every single item in Boards List*/
            adapter.setOnClickListener(object:
                BoardItemsAdapter.OnClickListener {
                override fun onClick(position: Int, model: Board) {
                    val intent  = Intent(this@MainActivity, TaskListActivity::class.java)

                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })

        } else {
            rvBoardsList.visibility = View.GONE
            tvNoBoardsAvailable.visibility = View.VISIBLE
        }

    }

}
