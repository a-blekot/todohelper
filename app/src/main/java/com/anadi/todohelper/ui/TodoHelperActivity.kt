package com.anadi.todohelper.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.anadi.todohelper.databinding.ActivityTodoHelperBinding
import com.anadi.todohelper.ui.selector.TodoSelectorActivity

class TodoHelperActivity : AppCompatActivity() {
    companion object {
        private const val SELECT_TODO_REQUEST = 1
        const val NAME = "name"
        const val TODO = "todo"
        const val USER_NAME = "user_name"
        const val TODO_TITLE = "todo_title"
    }

    lateinit var binding: ActivityTodoHelperBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoHelperBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // restore saved state on launch
        getPreferences(Context.MODE_PRIVATE)?.run {
            binding.userName.text = getString(USER_NAME, "")
            binding.todoTitle.text = getString(TODO_TITLE, "")
            binding.name.visibility = getInt(NAME, View.GONE)
            binding.todo.visibility = getInt(TODO, View.GONE)
        }

        /**
         * Here we launch second activity, where we select one todo task from random user
         */
        binding.buttonSelectRandom.setOnClickListener {
            val intent = Intent(this, TodoSelectorActivity::class.java)
            startActivityForResult(intent, SELECT_TODO_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_TODO_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.run {
                binding.userName.text = getCharSequenceExtra(USER_NAME)
                binding.todoTitle.text = getCharSequenceExtra(TODO_TITLE)
                binding.name.visibility = View.VISIBLE
                binding.todo.visibility = View.VISIBLE
            }
        } else {
            binding.name.visibility = View.GONE
            binding.todo.visibility = View.GONE
        }
    }

    /**
     * Save state during configuration changes
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(USER_NAME, binding.userName.text)
        outState.putCharSequence(TODO_TITLE, binding.todoTitle.text)
        outState.putInt(NAME, binding.name.visibility)
        outState.putInt(TODO, binding.todo.visibility)
    }

    override fun onRestoreInstanceState(inState: Bundle) {
        super.onRestoreInstanceState(inState)
        binding.userName.text = inState.getCharSequence(USER_NAME)
        binding.todoTitle.text = inState.getCharSequence(TODO_TITLE)
        binding.name.visibility = inState.getInt(NAME, View.GONE)
        binding.todo.visibility = inState.getInt(TODO, View.GONE)
    }

    override fun onDestroy() {
        super.onDestroy()
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(USER_NAME, binding.userName.text.toString())
            putString(TODO_TITLE, binding.todoTitle.text.toString())
            putInt(NAME, binding.name.visibility)
            putInt(TODO, binding.todo.visibility)
            commit()
        }
    }
}