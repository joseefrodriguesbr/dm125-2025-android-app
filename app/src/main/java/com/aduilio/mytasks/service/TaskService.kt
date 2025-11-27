package com.aduilio.mytasks.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aduilio.mytasks.entity.Task
import com.aduilio.mytasks.repository.ResponseDto

class TaskService : ViewModel() {

    private val taskRepository = RetrofitService().getTaskRepository()

    fun create(task: Task): LiveData<ResponseDto<Task>> {
        val taskLiveData = MutableLiveData<ResponseDto<Task>>()

        taskRepository.create(task).enqueue(ServiceCallBack(taskLiveData))

        return taskLiveData
    }

    fun update(task: Task): LiveData<ResponseDto<Task>> {
        val taskLiveData = MutableLiveData<ResponseDto<Task>>()

        taskRepository.update(task.id!!.toLong(), task).enqueue(ServiceCallBack(taskLiveData))

        return taskLiveData
    }


    fun list(): LiveData<ResponseDto<List<Task>>> {
        val tasksLiveData = MutableLiveData<ResponseDto<List<Task>>>()

        taskRepository.list().enqueue(ServiceCallBack(tasksLiveData))

        return tasksLiveData
    }

    fun getById(id: Long): LiveData<ResponseDto<Task>> {
        val taskLiveData = MutableLiveData<ResponseDto<Task>>()

        taskRepository.getById(id).enqueue(ServiceCallBack(taskLiveData))

        return taskLiveData
    }

    fun delete(id: Long): LiveData<ResponseDto<Void>> {
        val liveData = MutableLiveData<ResponseDto<Void>>()

        taskRepository.delete(id).enqueue(ServiceCallBack(liveData))

        return liveData;
    }

    fun complete(id: Long): LiveData<ResponseDto<Task>> {
        val taskLiveData = MutableLiveData<ResponseDto<Task>>()

        taskRepository.complete(id).enqueue(ServiceCallBack(taskLiveData))

        return taskLiveData
    }
}