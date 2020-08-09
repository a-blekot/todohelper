package com.anadi.todohelper.data.api

import com.anadi.todohelper.data.model.TodoTask
import com.anadi.todohelper.data.model.User
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TodoService {
    @GET("users/{id}")
    fun getUser(@Path("id") id: Int): Single<User>

    @GET("todos/")
    fun getTodo(@Query("id") id1: Int,
                @Query("id") id2: Int): Single<List<TodoTask>>
}