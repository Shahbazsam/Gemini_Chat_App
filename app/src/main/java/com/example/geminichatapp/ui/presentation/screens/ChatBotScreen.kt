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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
        if(chatState.chatList.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f), // Ensure it takes the full screen
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ask me anything!",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color(0xFFE0E0E0),
                        fontWeight = FontWeight.Bold
                    ),
                )
            }
            //GeniePlaceholder(isTextFieldFocused = textFieldFocused)
            } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = lazyListState,
                reverseLayout = true
            ) {
                itemsIndexed(chatState.chatList, key = {  _ , message -> message.prompt }) { _, chat ->
                    val shouldAnimate = chat == chatState.chatList.first() && !chat.isFromUser

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
                                .background(if (chat.isFromUser) Color(0xFF3A3A3A) else  Color(0xFF2A2A2A))
                                .padding(dimensionResource(R.dimen.padding_medium)),
                        ) {
                            if (chat.isFromUser) ChatFromUser(prompt = chat.prompt, bitmap = chat.bitmap)
                            else ChatFromModel(prompt = chat.prompt , shouldAnimate = shouldAnimate)
                        }
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
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptField(
    chatState: ChatState,
    bitmap: Bitmap?,
    onPromptChange: (String) -> Unit,
    onSend: () -> Unit,
    onImagePickerLaunch: () -> Unit,
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
                tint = Color(0xFF00C6D1)
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        OutlinedTextField(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(24.dp)) // Smooth rounded edges
                .background(Color(0xFF2A2A2A)) // Matches dark theme
                .padding(dimensionResource(R.dimen.padding_small)), // Adds breathing space
            value = chatState.prompt,
            onValueChange = onPromptChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.ask_genie),
                    color = Color(0xFF7A7A7A), // Subtle gray for the placeholder
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFFE0E0E0)), // Off-white for text
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = Color(0xFFA3C9FF), // Light blue for the cursor
                focusedBorderColor = Color(0xFF3A86FF), // Bright blue when focused
                unfocusedBorderColor = Color(0xFF444444), // Subtle gray border when not focused
                focusedLabelColor = Color(0xFFA3C9FF),
                unfocusedLabelColor = Color(0xFF7A7A7A),
                containerColor = Color(0xFF1E1E1E) // Slightly darker background for the container
            ),

            shape = RoundedCornerShape(24.dp) // Matches the overall theme
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
            modifier = Modifier
                .size(dimensionResource(R.dimen.image_size))
                .clickable { onSend() },
            imageVector = Icons.AutoMirrored.Rounded.Send,
            contentDescription = stringResource(R.string.send_prompt),
            tint = Color(0xFF3A86FF)
        )
    }
}

@Composable
fun ChatFromUser(prompt : String , bitmap : Bitmap?) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_medium)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Column {
            bitmap?.let {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .padding(bottom = dimensionResource(R.dimen.padding_prompt_image)),
                    contentScale = ContentScale.Crop,
                    bitmap = it.asImageBitmap(),
                    contentDescription = stringResource(R.string.image)
                )
            }
            Text(
                text = prompt,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFE0E0E0), // Off-white text for readability
                modifier = Modifier
                    .background(Color(0xFF3A3A3A), RoundedCornerShape(8.dp))
                    .padding(dimensionResource(R.dimen.padding_small))
            )
        }
        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_extra_small)))
        Text(
            text = "You",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF8D8D8D), // Subtle gray text for "You"
            modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small))
        )
    }
}

@Composable
fun ChatFromModel(prompt: String , shouldAnimate : Boolean) {
    var displayedText by remember { mutableStateOf("") }


    LaunchedEffect(shouldAnimate) {
        if (shouldAnimate) {
            displayedText = ""
            val characters = prompt.toCharArray()
            for (char in characters) {
                displayedText += char.toString()
                delay(1)
            }
        } else {
            displayedText = prompt
        }
    }
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
                .background(Color(0xFF2B2B2B)), // Matches the dark theme
            painter = painterResource(R.drawable.assistant), // Replace with your logo resource
            contentDescription = "logo",
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_small)))
        MarkdownText(
            markdown = displayedText,
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
            userData = UserData(
                userId = "1",
                userName = null,
                profilePicture = ""
            ),
            onSignOut = {},
            paddingValues = PaddingValues(4.dp)
        )
    }
}


