package com.anadi.todohelper.ui.selector

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.anadi.todohelper.databinding.ActivityTodoSelectorBinding
import com.anadi.todohelper.ui.TodoHelperActivity
import com.anadi.todohelper.util.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoSelectorActivity : AppCompatActivity() {
    companion object {
        private const val RESULT_NOTHING = Activity.RESULT_FIRST_USER
    }

    private lateinit var binding: ActivityTodoSelectorBinding
    private val viewModel: TodoSelectorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoSelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * Only one task could be selected at time
         * And SELECT button enables only when at least one task is selected
         */
        binding.todo1.setOnClickListener {
            binding.todo1.toggle()
            binding.todo2.isChecked = false
            binding.buttonSelect.isEnabled = todoSelected()
        }

        binding.todo2.setOnClickListener {
            binding.todo2.toggle()
            binding.todo1.isChecked = false
            binding.buttonSelect.isEnabled = todoSelected()
        }

        /**
         * Here we launch API request in our viewModel
         */
        binding.buttonSearch.setOnClickListener{ viewModel.search() }

        /**
         * Write result to bundle and go back to first Activity
         */
        binding.buttonSelect.setOnClickListener{
            val resultIntent = Intent()
            resultIntent.putExtra(TodoHelperActivity.USER_NAME, binding.userName.text)
            resultIntent.putExtra(TodoHelperActivity.TODO_TITLE, resultText)
            setResult(resultCode, resultIntent)
            finish()
        }

        viewModel.status.observe(this, Observer {
            when (it) {
                Status.SUCCESS -> binding.progress.visibility = View.GONE
                Status.ERROR -> binding.progress.visibility = View.GONE
                Status.LOADING -> binding.progress.visibility = View.VISIBLE
            }
        })

        viewModel.user.observe(this, Observer { binding.userName.text = it.name })

        viewModel.todoTasks.observe(this, Observer {
            if (it.size == 2) {
                binding.title1.text = it[0].title
                binding.title2.text = it[1].title

                binding.todo1.isChecked = false
                binding.todo2.isChecked = false
                binding.buttonSelect.isEnabled = false
                setCardsVisibility(View.VISIBLE)
            } else {
                setCardsVisibility(View.GONE)
            }
        })
    }

    /**
     * Save state during configuration changes
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("todo1", binding.todo1.isChecked)
        outState.putBoolean("todo2", binding.todo2.isChecked)
    }

    override fun onRestoreInstanceState(inState: Bundle) {
        super.onRestoreInstanceState(inState)
        binding.todo1.isChecked = inState.getBoolean("todo1", false)
        binding.todo2.isChecked = inState.getBoolean("todo2", false)
        binding.buttonSelect.isEnabled = todoSelected()
    }

    private val resultText: CharSequence
        get() = if (binding.todo1.isChecked) {
            binding.title1.text
        } else if (binding.todo2.isChecked) {
            binding.title2.text
        } else {
            ""
        }

    private val resultCode: Int
        get() = if (todoSelected()) {
            Activity.RESULT_OK
        } else {
            RESULT_NOTHING
        }

    private fun todoSelected() : Boolean {
        return binding.todo1.isChecked || binding.todo2.isChecked
    }

    private fun setCardsVisibility(visibility: Int) {
        binding.name.visibility = visibility
        binding.todo1.visibility = visibility
        binding.todo2.visibility = visibility
    }
}