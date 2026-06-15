package com.whisper.sample

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.whisper.api.Whisper
import kotlinx.coroutines.launch

@Composable
fun App() {
    val scope = rememberCoroutineScope()
    var logs by remember { mutableStateOf(listOf<String>()) }
    
    MaterialTheme {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Whisper Sample App", style = MaterialTheme.typography.h4)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    scope.launch {
                        Whisper.startListening()
                        logs = logs + "Started listening"
                    }
                }) {
                    Text("Start Listening")
                }
                
                Button(onClick = {
                    scope.launch {
                        Whisper.stopListening()
                        logs = logs + "Stopped listening"
                    }
                }) {
                    Text("Stop Listening")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Button(onClick = {
                scope.launch {
                    Whisper.transmit("Hello Whisper!".encodeToByteArray())
                    logs = logs + "Transmitting: Hello Whisper!"
                }
            }) {
                Text("Transmit")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Log Panel", style = MaterialTheme.typography.h6)
            
            Divider()
            
            LazyColumn(modifier = Modifier.fillMaxSize().weight(1f)) {
                items(logs) { log ->
                    Text(log, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
            
            Button(onClick = { logs = emptyList() }) {
                Text("Clear Logs")
            }
        }
    }
}
