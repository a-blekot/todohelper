package com.anadi.todohelper.data

import com.anadi.todohelper.data.api.TodoService
import javax.inject.Inject

class TodoRepository @Inject constructor(private val todoService: TodoService) {
    fun getUser(id: Int) = todoService.getUser(id)
    fun getTodo(id1: Int, id2: Int) = todoService.getTodo(id1, id2)
}