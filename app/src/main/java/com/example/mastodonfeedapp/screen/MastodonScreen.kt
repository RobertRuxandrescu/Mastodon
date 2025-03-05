package com.example.mastodonfeedapp.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mastodonfeedapp.model.MastodonPost
import com.example.mastodonfeedapp.viewModel.MastodonState

@Composable
fun MastodonScreen(
    state: MastodonState,
    onKeywordChange: (String) -> Unit,
    onStartStreaming: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        onStartStreaming()
    }

    Column(modifier = modifier.padding(16.dp)) {
        OutlinedTextField(
            value = state.filterKeyword,
            onValueChange = { onKeywordChange(it) },
            label = { Text("Filter posts") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        if (state.error != null) {
            Text(text = "Error: ${state.error}", color = Color.Red)
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(state.posts) { post ->
                MastodonPostItem(post)
            }
        }
    }
}

@Composable
fun MastodonPostItem(post: MastodonPost) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = post.content,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMastodonScreen() {
    val fakeState = MastodonState(
        posts = listOf(
            MastodonPost(id = "1", content = "Message 1"),
            MastodonPost(id = "2", content = "Message 2"),
            MastodonPost(id = "3", content = "Message 3")
        ),
        isLoading = false
    )

    MaterialTheme {
        Surface {
            MastodonScreen(
                state = fakeState,
                onKeywordChange = {},
                onStartStreaming = {}
            )
        }
    }
}

