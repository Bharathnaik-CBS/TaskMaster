package com.example.taskmaster.data
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks") fun getAllTasks(): Flow<List<Task>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertTask(task: Task)
    @Delete suspend fun deleteTask(task: Task)
    @Update suspend fun updateTask(task: Task)
    @Query("SELECT * FROM tasks WHERE deadline = :date AND status = 'PENDING'") suspend fun getTasksDueOn(date: String): List<Task>
}
