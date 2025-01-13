package com.example.geminichatapp.ui.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.geminichatapp.R
import com.example.geminichatapp.data.model.ChatHistory
import com.example.geminichatapp.data.repository.ChatsRepository
import com.example.geminichatapp.ui.presentation.events.UserData
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun HistoryScreen(
    historyViewModel: HistoryViewModel,
    onSignOut : ()->Unit,
    userData: UserData?,
    onNavigate : () -> Unit,
    onBackPress : () -> Unit
) {
    val historyState = historyViewModel.listOfConversationState.collectAsStateWithLifecycle().value
    Column(
      modifier = Modifier
          .fillMaxSize()
          .background(Color(0xFF1E1E1E)),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        userData?.profilePicture?.let {
            AsyncImage(
                model = it,
                modifier = Modifier
                    .padding(top = 45.dp)
                    .size(130.dp)
                    .clip(CircleShape),
                contentDescription = " Profile Picture"
            )
        } ?: run {
            Image(
                painter = painterResource(R.drawable.assistant),
                colorFilter = ColorFilter.tint(Color.White),
                modifier = Modifier
                    .padding(top = 30.dp)
                    .size(130.dp)
                    .clip(CircleShape),
                contentDescription = "Default Profile Picture"
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "Md shahbaz",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold

        )
        Spacer(modifier = Modifier.height(14.dp))
        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier
                .weight(1f)
        ) {
            items(historyState.chatDatabaseList) {
                ConversationItem(
                    historyViewModel,
                    onNavigate,
                    chat = it
                )
            }

        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            modifier = Modifier
                .clickable { onSignOut() },
            text = " Log Out ",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
    BackHandler {
        onBackPress()
    }
}


@Composable
fun ConversationItem(
    historyViewModel: HistoryViewModel,
    onNavigate: () -> Unit,
    chat : ChatHistory
) {

    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .clickable {
                CoroutineScope(Dispatchers.Main).launch {
                    historyViewModel.getHistoryByConversationId(chat.conversationId)
                    onNavigate()
                }
            }
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF3A3A3A)
        )
    ) {
        Column(
            modifier = Modifier
                .animateContentSize (
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp)
            ) {
                Text(
                    text = (" Conversation : ${chat.conversationId}"),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFE0E0E0),
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.padding_small))
                )
                Spacer(modifier = Modifier.weight(1f))
                ConversationButton(
                    expanded = expanded,
                    onClick = { expanded = !expanded}
                )
            }
            if (expanded) {
                MarkdownText(
                    markdown = chat.prompt,
                    color = Color(0xFFE0E0E0),
                    modifier = Modifier
                        .padding(
                            top = 8.dp,
                            start = 16.dp,
                            bottom = 16.dp,
                            end = 16.dp
                        ),
                    maxLines = 2,
                )
            }

        }
    }
}

@Composable
private fun ConversationButton(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {

        Icon(
            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
            contentDescription = "Button",
            tint = MaterialTheme.colorScheme.secondary
        )
    }

}


