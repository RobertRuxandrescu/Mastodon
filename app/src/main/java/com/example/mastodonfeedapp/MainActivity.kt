package com.example.mastodonfeedapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mastodonfeedapp.screen.MastodonScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MastodonApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MastodonApp() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mastodon Stream") })
        }
    ) { innerPadding ->
        MastodonScreen(modifier = Modifier.padding(innerPadding))
    }
}