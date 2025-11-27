package com.aduilio.mytasks.entity

import java.time.LocalDate
import java.time.LocalTime

data class Task(
    val id: Int?,
    val title: String,
    val description: String,
    val date: LocalDate?,
    val time: LocalTime?,
    val completed: Boolean
)
