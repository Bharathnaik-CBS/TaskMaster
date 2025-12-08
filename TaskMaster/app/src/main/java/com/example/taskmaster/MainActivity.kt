package com.example.taskmaster
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taskmaster.data.AppDatabase
import com.example.taskmaster.data.TaskRepository
import com.example.taskmaster.data.TaskStatus
import com.example.taskmaster.ui.*
import com.example.taskmaster.ui.theme.TaskMasterTheme
import com.example.taskmaster.worker.NotificationScheduler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getDatabase(this)
        val repository = TaskRepository(database.taskDao())
        val viewModel = ViewModelProvider(this, TaskViewModelFactory(repository))[TaskViewModel::class.java]
        NotificationScheduler.scheduleDailyReminder(this)

        setContent {
            TaskMasterTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        val tasks by viewModel.tasks.collectAsState()
                        val filter by viewModel.filter.collectAsState()
                        HomeScreen(tasks, filter, { viewModel.setFilter(it) }, { task, action ->
                            when (action) {
                                TaskAction.COMPLETE -> viewModel.updateStatus(task, TaskStatus.COMPLETED)
                                TaskAction.CANCEL -> viewModel.updateStatus(task, TaskStatus.CANCELLED)
                                TaskAction.DELETE -> viewModel.deleteTask(task)
                            }
                        }, { navController.navigate("add") })
                    }
                    composable("add") {
                        AddTaskScreen({ name, deadline -> viewModel.addTask(name, deadline); navController.popBackStack() }, { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
