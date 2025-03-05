package com.example.mastodonfeedapp.screen

import androidx.compose.runtime.Composable
import org.orbitmvi.orbit.compose.collectAsState
import com.example.mastodonfeedapp.viewModel.MastodonViewModel

@Composable
fun ListScreen(viewModel: MastodonViewModel) {
    val state = viewModel.collectAsState()
}