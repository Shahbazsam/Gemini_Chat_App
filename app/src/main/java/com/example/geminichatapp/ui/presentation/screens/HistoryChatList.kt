package com.example.geminichatapp.ui.presentation.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.geminichatapp.R
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun HistoryChatList(
    historyViewModel: HistoryViewModel,
    paddingValues: PaddingValues,
    onNavigate : () -> Unit,
    onBackPress : () -> Unit
) {
    val chatState = historyViewModel.historyOfConversationState.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
            .padding(paddingValues)
            .padding(
                vertical = dimensionResource(R.dimen.padding_main_screen),
                horizontal = dimensionResource(R.dimen.padding_main_screen),
            )
            .imePadding(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.side_panel),
                contentDescription = "side panel",
                tint = Color.White,
                modifier = Modifier
                    .clickable {
                        onNavigate()
                    }
                    .size(40.dp)
            )
            Spacer(modifier = Modifier.weight(0.75f))
            Image(
                modifier = Modifier.size(38.dp),
                painter = painterResource(R.drawable.assistant),
                contentDescription = "Logo",
                colorFilter = ColorFilter.tint(Color.White)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(14.dp))
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = lazyListState,
            ) {
                items(
                    chatState.value.chatHistoryForConversation
                ) { chat ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(R.dimen.padding_extra_small))
                    ) {
                        Box(
                            modifier = Modifier
                                .align(if (chat.isFromUser) Alignment.End else Alignment.Start)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (chat.isFromUser) 16.dp else 0.dp,
                                        bottomEnd = if (chat.isFromUser) 0.dp else 16.dp
                                    )
                                )
                                .background(
                                    if (chat.isFromUser) Color(0xFF3A3A3A) else Color(
                                        0xFF2A2A2A
                                    )
                                )
                                .padding(dimensionResource(R.dimen.padding_medium)),
                        ) {
                            if (chat.isFromUser) HistoryChatFromUser(
                                prompt = chat.prompt,
                                byteArray = chat.byteArray
                            )
                            else HistoryChatFromModel(prompt = chat.prompt)
                        }
                    }
                }
            }
    }
    BackHandler {
        onBackPress()
    }
}
@Composable
fun HistoryChatFromUser(prompt : String , byteArray : ByteArray?) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_medium)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Column {
            byteArray?.let {
                val bitmap = byteArrayToBitmap(it).asImageBitmap()
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .padding(bottom = dimensionResource(R.dimen.padding_prompt_image)),
                    contentScale = ContentScale.Crop,
                    bitmap = bitmap,
                    contentDescription = stringResource(R.string.image)
                )
            }
            Text(
                text = prompt,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFE0E0E0),
                modifier = Modifier
                    .background(Color(0xFF3A3A3A), RoundedCornerShape(8.dp))
                    .padding(dimensionResource(R.dimen.padding_small))
            )
        }
        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_extra_small)))
        Text(
            text = "You",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF8D8D8D),
            modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small))
        )
    }
}
private fun byteArrayToBitmap (byteArray: ByteArray) : Bitmap {
    return BitmapFactory.decodeByteArray(byteArray , 0 , byteArray.size)
}

@Composable
fun HistoryChatFromModel(prompt: String ) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_medium)),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF2B2B2B)),
            painter = painterResource(R.drawable.assistant),
            contentDescription = "logo",
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(color = Color.White),
        )
        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_small)))
        MarkdownText(
            markdown = prompt,
            isTextSelectable = true,
            fontResource = R.font.actor_regular,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFA3C9FF), // Light blue for model responses
            modifier = Modifier
                .background(Color(0xFF2A2A2A), RoundedCornerShape(8.dp))
                .padding(dimensionResource(R.dimen.padding_small))
        )
    }
}


