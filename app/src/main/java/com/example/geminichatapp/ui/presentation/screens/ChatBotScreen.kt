package com.example.geminichatapp.ui.presentation.screens


import android.graphics.Bitmap
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.geminichatapp.R
import com.example.geminichatapp.ui.presentation.ImagePicker
import com.example.geminichatapp.ui.presentation.events.ChatState
import com.example.geminichatapp.ui.presentation.events.ChatUiEvent
import com.example.geminichatapp.ui.presentation.events.UserData
import com.example.geminichatapp.ui.theme.GeminiChatAppTheme
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.delay

@Composable
fun ChatScreen(
    userData: UserData?,
    onSignOut : ()->Unit,
    paddingValues: PaddingValues
) {
    val chatViewModel: ChatViewModel = viewModel(factory = ChatViewModel.Factory)
    val chatState = chatViewModel.chatState.collectAsState().value
    val imagePicker = ImagePicker()
    val bitmap = getBitmap(chatViewModel)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(
                vertical = dimensionResource(R.dimen.padding_main_screen),
                horizontal = dimensionResource(R.dimen.padding_main_screen),
            ),
        verticalArrangement = Arrangement.Bottom
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            itemsIndexed(chatState.chatList, key = {  _ , message -> message.prompt }) { _, chat ->
                
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
                                    topStart = 48f,
                                    topEnd = 48f,
                                    bottomStart = if (chat.isFromUser) 48f else 0f,
                                    bottomEnd = if (chat.isFromUser) 0f else 48f
                                )
                            )
                            .background(if (chat.isFromUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer)
                            .padding(dimensionResource(R.dimen.padding_medium)),
                    ) {
                        if (chat.isFromUser) ChatFromUser(prompt = chat.prompt, bitmap = chat.bitmap)
                            else ChatFromModel(prompt = chat.prompt)
                    }
                }
            }
        }
        PromptField(
            chatState = chatState,
            bitmap = bitmap,
            onPromptChange = { chatViewModel.onEvent(ChatUiEvent.UpdatePrompt(it)) },
            onSend = {
                chatViewModel.onEvent(
                    ChatUiEvent.SendPrompt(chatState.prompt, chatState.bitmap)
                )
            },
            onImagePickerLaunch = {
                imagePicker.launch(
                    PickVisualMediaRequest
                        .Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        .build()
                )
            }
        )
    }
}

@Composable
fun PromptField(
    chatState: ChatState,
    bitmap: Bitmap?,
    onPromptChange: (String) -> Unit,
    onSend: () -> Unit,
    onImagePickerLaunch: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .padding(top = dimensionResource(R.dimen.padding_extra_small)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Column {
            bitmap?.let {
                Image(
                    modifier = Modifier
                        .padding(bottom = dimensionResource(R.dimen.padding_extra_small))
                        .clip(MaterialTheme.shapes.small)
                        .size(dimensionResource(R.dimen.image_size)),
                    bitmap = it.asImageBitmap(),
                    contentScale = ContentScale.Crop,
                    contentDescription = stringResource(R.string.added_photo)
                )
            }
            Icon(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.image_size))
                    .clickable { onImagePickerLaunch() },
                imageVector = Icons.Rounded.AddAPhoto,
                contentDescription = stringResource(R.string.add_bitmap),
                tint = MaterialTheme.colorScheme.primaryContainer
            )
        }
        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_spacer)))
        OutlinedTextField(
            modifier = Modifier
                .clip(RoundedCornerShape(32.dp))
                .weight(1f),
            value = chatState.prompt,
            onValueChange = onPromptChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.ask_genie),
                    color = MaterialTheme.colorScheme.outline
                )
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.surface,
                cursorColor = MaterialTheme.colorScheme.error,
                focusedIndicatorColor = MaterialTheme.colorScheme.tertiaryContainer,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiaryContainer,
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_spacer)))
        Icon(
            modifier = Modifier
                .size(dimensionResource(R.dimen.image_size))
                .clickable { onSend() },
            imageVector = Icons.AutoMirrored.Rounded.Send,
            contentDescription = stringResource(R.string.send_prompt),
            tint = MaterialTheme.colorScheme.primaryContainer
        )
    }
}

@Composable
fun ChatFromUser(prompt : String , bitmap : Bitmap?) {
    Column {
        bitmap?.let {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .padding(bottom = dimensionResource(R.dimen.padding_prompt_image)),
                contentScale = ContentScale.Crop,
                bitmap = it.asImageBitmap(),
                contentDescription = stringResource(R.string.image)
            )
        }
        Text(
            text = prompt,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun ChatFromModel(prompt: String) {
    var displayedText by remember { mutableStateOf("") }


    LaunchedEffect(prompt) {
        displayedText = ""
        val characters =  prompt.toCharArray()
        for (char in characters) {
            displayedText += char.toString()
            delay(1)
        }
    }
    MarkdownText(
        markdown = prompt,
        isTextSelectable = true,
        fontResource = R.font.actor_regular,
        style = MaterialTheme.typography.bodyMedium
    )

}


@Composable
fun getBitmap(chatViewModel: ChatViewModel) : Bitmap? {
    val uri = chatViewModel.chatState.collectAsState().value

    val imageState : AsyncImagePainter.State = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(uri.bitmap)
            .size(Size.ORIGINAL)
            .build(),
    ).state

    if (imageState is AsyncImagePainter.State.Success){
        return imageState.result.drawable.toBitmap()
    }

    return null
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    GeminiChatAppTheme {
        ChatScreen(
            userData = null,
            onSignOut = {},
            paddingValues = PaddingValues(4.dp)
        )
    }
}


