package com.aduilio.mytasks.listener

import com.aduilio.mytasks.entity.Task

interface ClickListener {

    fun onClick(task: Task)

    fun onComplete(id: Long)

}