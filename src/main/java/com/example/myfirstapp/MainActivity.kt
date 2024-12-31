package com.example.myfirstapp

import android.os.Bundle
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.ui.theme.MyFirstAppTheme
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyFirstAppTheme {
                ToDoApp()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyFirstAppTheme {
        Greeting("Android")
    }
}

@Composable
fun ToDoApp(){
    val toDoList = remember { mutableStateListOf<Entry>() }
    val titleInput = remember { mutableStateOf("")}
    val inputPopup = remember { mutableStateOf(false) }
    val statusFilter = remember { mutableStateOf(Status.All) }

    toDoList.add(Entry("This is a done task", Status.Done))
    toDoList.add(Entry("This is a done task 2", Status.Done))
    toDoList.add(Entry("This is a pending task", Status.Pending))
    toDoList.add(Entry("This is a pending task 2", Status.Pending))


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ){
                    // button to open modal/dialog
                    Button(onClick = {inputPopup.value = true}
                    ) {
                        Text(text = "Add Task")
                    }


                }
            }
        }
        ){ innerPadding ->
            Column (modifier = Modifier.padding(innerPadding)) {
                //Button that will let us filter stuff
                Button(onClick = {
                    statusFilter.value = when (statusFilter.value) {
                        Status.All -> Status.Done
                        Status.Done -> Status.Pending
                        Status.Pending -> Status.All
                    }
                }) {
                    Text(text = "filter by ${statusFilter.value}")
                }
                //Element on how we display task cards
                LazyColumn(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    items(toDoList.filter { entry ->
                        statusFilter.value == Status.All || entry.status == statusFilter.value
                    }) { entry ->
                        CardView(
                            entry,
                            updateStatus = { oldEntry ->
                                val index = toDoList.indexOf(oldEntry)
                                if (index != -1 ) {
                                    toDoList[index] = oldEntry.copy(
                                        status = if (oldEntry.status == Status.Pending) Status.Done else Status.Pending
                                    )
                                }
                            },
                            remove = { oldEntry ->
                                    toDoList.remove(oldEntry)
                            }
                        )
                    }
                }
            }

        }
    // Popup Dialog for Input
    if (inputPopup.value) {
        AlertDialog(
            onDismissRequest = { inputPopup.value = false }, // Close the dialog when dismissed
            title = {
                Text(text = "Add New Task")
            },
            text = {
                Column {
                    TextField(
                        value = titleInput.value,
                        onValueChange = { titleInput.value = it },
                        placeholder = { Text("Enter a task title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titleInput.value.isNotBlank()) {
                            toDoList.add(
                                Entry(
                                    title = titleInput.value,
                                    status = Status.Pending
                                )
                            )
                            titleInput.value = "" // Clear input
                            inputPopup.value = false // Close dialog
                        }
                    }
                ) {
                    Text("Submit")
                }
            },
            dismissButton = {
                Button(onClick = { inputPopup.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CardView(entry: Entry, updateStatus: (Entry) -> Unit, remove: (Entry) -> Unit) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Add elevation for a shadow effect
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp) // Add padding inside the card
        ) {
            // Title
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleMedium, // Use a predefined text style
                modifier = Modifier.padding(bottom = 8.dp) // Add spacing below the title
            )

            // Row for Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween // Distribute buttons evenly
            ) {
                // Status Button
                Button(
                    onClick = { updateStatus(entry) },
                    modifier = Modifier.weight(1f) // Make buttons share space
                ) {
                    Text(text = if (entry.status == Status.Pending) "Mark as Done" else "Mark as Pending")
                }

                Spacer(modifier = Modifier.width(8.dp)) // Add spacing between buttons

                // Remove Button
                Button(
                    onClick = { remove(entry) },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error) // Use a red color for "Remove"
                ) {
                    Text(text = "Remove", color = MaterialTheme.colorScheme.onError) // Ensure text is readable
                }
            }
        }
    }
}


