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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mastodonfeedapp.screen.MastodonScreen
import com.example.mastodonfeedapp.viewModel.MastodonState
import com.example.mastodonfeedapp.viewModel.MastodonViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MastodonViewModel = hiltViewModel()
            val state by viewModel.container.stateFlow.collectAsState()

            MastodonApp(state, viewModel::setFilterKeyword, viewModel::startStreaming)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MastodonApp(
    state: MastodonState,
    onKeywordChange: (String) -> Unit,
    onStartStreaming: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mastodon Stream") })
        }
    ) { innerPadding ->
        MastodonScreen(
            state = state,
            onKeywordChange = onKeywordChange,
            onStartStreaming = onStartStreaming,
            modifier = Modifier.padding(innerPadding)
        )
    }
}