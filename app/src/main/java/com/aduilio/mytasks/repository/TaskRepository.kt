package com.aduilio.mytasks.repository

import com.aduilio.mytasks.entity.Task
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface TaskRepository {

    @POST("/tasks")
    fun create(@Body task: Task): Call<Task>

    @GET("/tasks")
    fun list(): Call<List<Task>>

    @GET("/tasks/{id}")
    fun getById(@Path("id") id: Long): Call<Task>

    @DELETE("/tasks/{id}")
    fun delete(@Path("id") id: Long): Call<Void>

    @POST("/tasks/{id}/completed")
    fun complete(@Path("id") id: Long): Call<Task>

    @PATCH("/tasks/{id}")
    fun update(@Path("id") id: Long, @Body task: Task): Call<Task>
}