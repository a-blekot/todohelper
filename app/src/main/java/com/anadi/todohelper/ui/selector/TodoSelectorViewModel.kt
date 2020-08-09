package com.anadi.todohelper.ui.selector

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anadi.todohelper.data.TodoRepository
import com.anadi.todohelper.data.model.TodoTask
import com.anadi.todohelper.data.model.User
import com.anadi.todohelper.util.Status
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

class TodoSelectorViewModel @ViewModelInject constructor(
        private val repository: TodoRepository
) : ViewModel() {

    companion object {
        private const val TAG = "RESULTS"
        private const val MAX_USERS = 10
        private const val MAX_TODOS = 20
    }

    private val compositeDisposable = CompositeDisposable()

    private val random: Random = Random(System.currentTimeMillis())

    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status>
        get() = _status

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _todoTasks = MutableLiveData<List<TodoTask>>()
    val todoTasks: LiveData<List<TodoTask>>
        get() = _todoTasks

    /**
     * Don't forget to clear all disposables
     */
    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    /**
     * Generate random User_ID and Todo_ID #1 and #2
     * Then call two requests:
     *  1) repository.getUser(userId)
     *  2) repository.getTodo(id1, id2)
     */
    fun search() {
        val userId = random.nextInt(MAX_USERS) + 1
        val id1 = random.nextInt(MAX_TODOS) + (userId - 1) * MAX_TODOS
        var id2 = random.nextInt(MAX_TODOS) + (userId - 1) * MAX_TODOS
        while (id1 == id2) {
            id2 = random.nextInt(MAX_TODOS) + (userId - 1) * MAX_TODOS
        }

        _status.value = Status.LOADING
        compositeDisposable.add(repository.getUser(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<User>() {
                    override fun onSuccess(value: User?) {
                        _user.value = value
                    }

                    override fun onError(e: Throwable?) {
                        e!!.printStackTrace()
                    }
                }))

        compositeDisposable.add(repository.getTodo(id1, id2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<TodoTask>>() {
                    override fun onSuccess(value: List<TodoTask>?) {
                        if (value?.size != 2) {
                            _status.value = Status.ERROR
                            _todoTasks.value = ArrayList()
                            return
                        }
                        _status.value = Status.SUCCESS
                        _todoTasks.value = value
                    }

                    override fun onError(e: Throwable?) {
                        _todoTasks.value = ArrayList()
                        _status.value = Status.ERROR
                        e?.printStackTrace()
                    }
                }))
    }
}