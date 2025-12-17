package com.example.taskmaster.ui
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.taskmaster.data.Task
import com.example.taskmaster.data.TaskStatus
import java.time.LocalDate

enum class TaskAction { COMPLETE, CANCEL, DELETE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(tasks: List<Task>, currentFilter: TaskFilter, onFilterSelected: (TaskFilter) -> Unit, onTaskAction: (Task, TaskAction) -> Unit, onAddTaskClick: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("TaskMaster") }) }, floatingActionButton = { FloatingActionButton(onClick = onAddTaskClick) { Icon(Icons.Default.Add, "Add") } }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            LazyRow(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(TaskFilter.values()) { filter ->
                    FilterChip(selected = currentFilter == filter, onClick = { onFilterSelected(filter) }, label = { Text(filter.name.lowercase().replaceFirstChar { it.titlecase() }) })
                }
            }
            LazyColumn(contentPadding = PaddingValues(bottom = 80.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tasks, key = { it.id }) { task -> TaskItem(task, onAction = { onTaskAction(task, it) }) }
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onAction: (TaskAction) -> Unit) {
    val today = LocalDate.now()
    val cardColor = when {
        task.status == TaskStatus.COMPLETED || task.status == TaskStatus.CANCELLED -> Color.Gray.copy(alpha = 0.5f)
        task.deadline.isBefore(today) -> Color(0xFFCF6679)
        task.deadline.isEqual(today) || task.deadline.isBefore(today.plusDays(3)) -> Color(0xFFFDD835)
        else -> Color(0xFF81C784)
    }
    val textColor = if (cardColor == Color(0xFFFDD835)) Color.Black else Color.White

    Card(colors = CardDefaults.cardColors(containerColor = cardColor), modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(task.name, style = MaterialTheme.typography.titleMedium, color = textColor)
                    Text("Due: ${task.deadline}", style = MaterialTheme.typography.bodySmall, color = textColor.copy(alpha = 0.8f))
                }
                Row {
                    if (task.status == TaskStatus.PENDING) {
                        IconButton(onClick = { onAction(TaskAction.COMPLETE) }) { Icon(Icons.Default.Check, "Complete", tint = textColor) }
                        IconButton(onClick = { onAction(TaskAction.CANCEL) }) { Icon(Icons.Default.Close, "Cancel", tint = textColor) }
                    }
                    IconButton(onClick = { onAction(TaskAction.DELETE) }) { Icon(Icons.Default.Delete, "Delete", tint = textColor) }
                }
            }
        }
    }
}
