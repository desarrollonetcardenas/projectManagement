package com.projemanag.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.projemanag.R
import com.projemanag.activities.TaskListActivity
import com.projemanag.models.Task

open class TaskListItemsAdapter(
        private val context: Context,
        private val list: ArrayList<Task>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(context)
                .inflate(R.layout.item_task, parent, false)

        /*Set width and height params values to LinearLayout */
        val layoutParams = LinearLayout.LayoutParams(
                (parent.width * 0.7).toInt(),
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        /*Set only left and right margin values to LinearLayout*/
        layoutParams.setMargins(
                (15.toDp()),
                0,
                (40.toDp()).toPx(),
                0
        )
        /*Set the layout configuration values to our view*/
        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            val tvAddTaskList = holder.itemView.findViewById<TextView>(R.id.tv_add_task_list)
            val llTaskItem = holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item)
            val cvAddTaskListName = holder.itemView.findViewById<CardView>(R.id.cv_add_task_list_name)

            /*If there's no entries in task list items, just hide the LinearLayout, otherwise show items*/
            if (position == list.size - 1) {

                tvAddTaskList.visibility = View.VISIBLE
                llTaskItem.visibility = View.GONE
            } else {

                tvAddTaskList.visibility = View.GONE
                llTaskItem.visibility = View.VISIBLE
            }

            /*Set the title card*/
            val tvTaskListTitle = holder.itemView.findViewById<TextView>(R.id.tv_task_list_title)
            tvTaskListTitle.text = model.title

            tvAddTaskList.setOnClickListener {
                tvAddTaskList.visibility = View.GONE
                cvAddTaskListName.visibility = View.VISIBLE
            }

            /*'close list' Image button. onClickListener*/
            val ibCloseListName = holder.itemView.findViewById<ImageButton>(R.id.ib_close_list_name)
            ibCloseListName.setOnClickListener {
                tvAddTaskList.visibility = View.VISIBLE
                cvAddTaskListName.visibility = View.GONE
            }

            /*List Done onClickListener*/
            val ibDoneListName = holder.itemView.findViewById<ImageButton>(R.id.ib_done_list_name)

            ibDoneListName.setOnClickListener {
                val listName = holder.itemView
                        .findViewById<EditText>(R.id.et_task_list_name)
                        .text
                        .toString()

                if (listName.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.createTaskList(listName)
                    } else {
                        Toast.makeText(context, "Please enter list name", Toast.LENGTH_LONG)
                                .show()
                    }
                }
            }

            /*
            * Set Edit Task List onClickListener
            * */
            val ibEditListName = holder.itemView
                    .findViewById<ImageButton>(R.id.ib_edit_list_name)
            ibEditListName.setOnClickListener {

                with(holder.itemView) {

                    findViewById<EditText>(R.id.et_edit_task_list_name)
                            .setText(model.title)
                    findViewById<LinearLayout>(R.id.ll_title_view)
                            .visibility = View.GONE
                    findViewById<CardView>(R.id.cv_edit_task_list_name)
                            .visibility = View.VISIBLE
                }
            }

            /*
            * Cancel Edit task list name
            * */
            holder.itemView.findViewById<ImageButton>(R.id.ib_close_editable_view)
                    .setOnClickListener {
                        with(holder.itemView) {

                            findViewById<LinearLayout>(R.id.ll_title_view)
                                    .visibility = View.VISIBLE
                            findViewById<CardView>(R.id.cv_edit_task_list_name)
                                    .visibility = View.GONE
                        }

                    }

            /*
            * Edit task list onClickListener
            * */
            holder.itemView.findViewById<ImageButton>(R.id.ib_done_edit_list_name)
                    .setOnClickListener {
                        val listName = holder.itemView
                                .findViewById<EditText>(R.id.et_edit_task_list_name)
                                .text
                                .toString()

                        if (listName.isNotEmpty()) {
                            if (context is TaskListActivity) {
                                context.updateTaskList(position, listName, model)
                            } else {
                                Toast.makeText(context, "Please enter list name", Toast.LENGTH_LONG)
                                        .show()
                            }
                        }
                    }

            /*
            * Delete task list onClickListener
            * */
            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_list)
                    .setOnClickListener {
                        if(context is TaskListActivity) {
                            alertDialogForDeleteList(position, model.title)
                        } else {
                            Toast.makeText(context, "Invalid task list", Toast.LENGTH_LONG)
                                    .show()
                        }
                    }

        }
    }

    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)

        builder.setTitle(title)
        builder.setMessage(R.string.delete_list_alert_dialog_message)
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") {
            dialogInterface, which ->
            dialogInterface.dismiss()

            if(context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }

        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    override fun getItemCount(): Int = list.size

    private fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}