package com.example.taskmaster.ui
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(onSave: (String, LocalDate) -> Unit, onCancel: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf(LocalDate.now()) }
    val context = LocalContext.current
    val datePickerDialog = DatePickerDialog(context, { _: DatePicker, y: Int, m: Int, d: Int -> deadline = LocalDate.of(y, m + 1, d) }, deadline.year, deadline.monthValue - 1, deadline.dayOfMonth)

    Scaffold(topBar = { TopAppBar(title = { Text("Add New Task") }, navigationIcon = { IconButton(onClick = onCancel) { Icon(Icons.Default.ArrowBack, "Back") } }) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Task Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = deadline.format(DateTimeFormatter.ISO_LOCAL_DATE), onValueChange = { }, label = { Text("Deadline") }, readOnly = true, trailingIcon = { IconButton(onClick = { datePickerDialog.show() }) { Icon(Icons.Default.DateRange, "Select Date") } }, modifier = Modifier.fillMaxWidth())
            Button(onClick = { if (title.isNotBlank()) onSave(title, deadline) }, modifier = Modifier.fillMaxWidth()) { Text("Save Task") }
        }
    }
}
