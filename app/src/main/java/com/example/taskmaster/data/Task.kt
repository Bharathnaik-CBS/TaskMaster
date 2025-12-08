package com.example.taskmaster.data
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

enum class TaskStatus { PENDING, COMPLETED, CANCELLED }

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val createdDate: LocalDate = LocalDate.now(),
    val deadline: LocalDate,
    val status: TaskStatus = TaskStatus.PENDING
)
