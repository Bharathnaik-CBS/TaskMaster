package com.example.taskmaster.ui
import androidx.lifecycle.*
import com.example.taskmaster.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class TaskFilter { ALL, PENDING, MISSED, ACCOMPLISHED, CANCELLED }

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    private val _filter = MutableStateFlow(TaskFilter.ALL)
    val filter: StateFlow<TaskFilter> = _filter

    val tasks: StateFlow<List<Task>> = combine(repository.allTasks, _filter) { tasks, currentFilter ->
        val today = LocalDate.now()
        val filtered = when (currentFilter) {
            TaskFilter.ALL -> tasks
            TaskFilter.PENDING -> tasks.filter { it.status == TaskStatus.PENDING }
            TaskFilter.ACCOMPLISHED -> tasks.filter { it.status == TaskStatus.COMPLETED }
            TaskFilter.CANCELLED -> tasks.filter { it.status == TaskStatus.CANCELLED }
            TaskFilter.MISSED -> tasks.filter { it.deadline.isBefore(today) && it.status == TaskStatus.PENDING }
        }
        filtered.sortedWith(Comparator { t1, t2 ->
            val score1 = getTaskPriorityScore(t1, today)
            val score2 = getTaskPriorityScore(t2, today)
            if (score1 != score2) score1.compareTo(score2) else t1.deadline.compareTo(t2.deadline)
        })
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun getTaskPriorityScore(task: Task, today: LocalDate): Int {
        return when {
            task.status == TaskStatus.PENDING && task.deadline.isBefore(today) -> 1
            task.status == TaskStatus.PENDING && !task.deadline.isAfter(today.plusDays(2)) -> 2
            task.status == TaskStatus.PENDING -> 3
            task.status == TaskStatus.COMPLETED -> 4
            else -> 5
        }
    }
    fun setFilter(newFilter: TaskFilter) { _filter.value = newFilter }
    fun addTask(name: String, deadline: LocalDate) { viewModelScope.launch { repository.insertTask(Task(name = name, deadline = deadline)) } }
    fun updateStatus(task: Task, status: TaskStatus) { viewModelScope.launch { repository.updateTask(task.copy(status = status)) } }
    fun deleteTask(task: Task) { viewModelScope.launch { repository.deleteTask(task) } }
}
class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = TaskViewModel(repository) as T
}
