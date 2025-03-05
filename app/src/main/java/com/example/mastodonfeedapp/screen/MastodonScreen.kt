package com.example.mastodonfeedapp.screen

import android.text.Html.fromHtml
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mastodonfeedapp.model.MastodonPost
import com.example.mastodonfeedapp.viewModel.MastodonState

@Composable
fun MastodonScreen(
    state: MastodonState,
    onKeywordEntered: (String) -> Unit,
    onLifetimeEntered: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        MastodonFilterField(onKeywordEntered)
        MastodonSecondsField(onLifetimeEntered)

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
            AndroidView(
                factory = { context ->
                    TextView(context).apply {
                        text = fromHtml(post.content) // This keeps formatting
                        movementMethod = LinkMovementMethod.getInstance() // Enables links
                    }
                }
            )
        }
    }
}

@Composable
fun MastodonFilterField(
    onKeywordEntered: (String) -> Unit,
) {
    var tempKeyword by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = tempKeyword,
        onValueChange = { tempKeyword = it },
        label = { Text("Filter posts") },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done // ✅ Changes "Return" key to "Done"
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onKeywordEntered(tempKeyword) // ✅ Update filter only when "Done" is pressed
                keyboardController?.hide() // ✅ Hide keyboard
            }
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun MastodonSecondsField(
    onLifetimeEntered: (Int) -> Unit
) {
    var tempSeconds by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = tempSeconds,
        onValueChange = { tempSeconds = it.filter { char -> char.isDigit() } }, // Only allow numbers
        label = { Text("Remove messages older than (seconds)") },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                tempSeconds.toIntOrNull()?.let { onLifetimeEntered(it) } // Input to number
                keyboardController?.hide()
            }
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewMastodonScreen() {
    val fakeState = MastodonState(
        posts = listOf(
            MastodonPost(id = "1", content = "Message 1", ""),
            MastodonPost(id = "2", content = "Message 2", ""),
            MastodonPost(id = "3", content = "Message 3", "")
        ),
        isLoading = false
    )

    MaterialTheme {
        Surface {
            MastodonScreen(
                state = fakeState,
                onKeywordEntered = {},
                onLifetimeEntered = {}
            )
        }
    }
}

