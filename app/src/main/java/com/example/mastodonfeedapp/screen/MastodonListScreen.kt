package com.example.mastodonfeedapp.screen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import org.orbitmvi.orbit.compose.collectAsState
import com.example.mastodonfeedapp.viewModel.MastodonViewModel

@Composable
fun MastodonScreen(viewModel: MastodonViewModel = hiltViewModel()) {
    val state = viewModel.collectAsState()
}