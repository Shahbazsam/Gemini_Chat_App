package com.example.geminichatapp.ui.presentation

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.geminichatapp.R
import com.example.geminichatapp.data.ChatUiEvent


@Composable
fun ChatBotScreen() {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.Black
    ) {
        Scaffold(
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .clip(RoundedCornerShape(12.dp))
                        .height(45.dp)
                        .padding(horizontal = 16.dp)

                ) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.Center),
                        text = stringResource(id = R.string.app_name),
                        fontSize = 20.sp,
                        color = Color.White

                    )
                }
            }
        ) {
            ChatScreen(paddingValues = it)

        }
    }
}

@Composable
fun ChatScreen(paddingValues: PaddingValues) {


    val chatViewModel = viewModel<ChatViewModel>()
    val chatState = chatViewModel.chatState.collectAsState().value
    val imagePicker = ImagePicker()

    val bitmap = getBitmap(chatViewModel)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top = paddingValues.calculateTopPadding()),
        verticalArrangement = Arrangement.Bottom
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .background(Color.Black),
            reverseLayout = true
        ) {
            itemsIndexed(chatState.chatList) { _, chat ->
                if (chat.isFromUser){
                    ChatFromUser(prompt = chat.prompt , bitmap = chat.bitmap)
                }else {
                    ChatFromModel(prompt = chat.prompt)
                }
            }
        }
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(start = 4.dp, end = 4.dp, bottom = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Column {
                bitmap?.let {
                    Image(
                        modifier = Modifier
                            .padding(bottom = 3.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .size(40.dp),
                        bitmap = it.asImageBitmap(),
                        contentScale = ContentScale.Crop,
                        contentDescription = "Added Photo" )
                }
                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            imagePicker.launch(
                                PickVisualMediaRequest
                                    .Builder()
                                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    .build()
                            )
                        },
                    imageVector = Icons.Rounded.AddAPhoto ,
                    contentDescription ="Add Bitmap",
                    tint = Color(0xFF757575)
                )

            }
            Spacer(modifier = Modifier.width(10.dp))
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 1.dp),
                value = chatState.prompt,
                onValueChange = {
                    chatViewModel.onEvent(ChatUiEvent.UpdatePrompt(it))
                },
                placeholder = {
                    Text(
                        text = " Ask Genie....!",
                        color = Color(0xFF757575)
                        )
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color(0xFFE0E0E0), // Text color when focused
                    unfocusedTextColor = Color(0xFFE0E0E0), // Text color when unfocused
                    cursorColor = Color(0xFFA3C9FF), // Cursor color
                    focusedIndicatorColor = Color.Transparent, // No underline when focused
                    unfocusedIndicatorColor = Color.Transparent, // No underline when unfocused
                    focusedContainerColor = Color(0xFF1E1E1E), // Background color when focused
                    unfocusedContainerColor = Color(0xFF1E1E1E) // Background color when unfocused
                )
            )
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        chatViewModel.onEvent(
                            ChatUiEvent.SendPrompt(
                                chatState.prompt,
                                chatState.bitmap
                            )
                        )
                    },
                imageVector = Icons.AutoMirrored.Rounded.Send,
                contentDescription = "Send Prompt",
                tint = Color(0xFF757575)
            )

        }

    }
}

@Composable
fun ChatFromUser(prompt : String , bitmap : Bitmap?) {

    Column(
        modifier = Modifier
            .padding(start = 100.dp, bottom = 20.dp)
    ) {
        bitmap?.let {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .padding(bottom = 2.dp),
                contentScale = ContentScale.Crop,
                bitmap = it.asImageBitmap(),
                contentDescription = "image"
            )
        }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp , bottom = 4.dp , top = 4.dp , end = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorResource(id = R.color.User_chat_background)),
                text = prompt,
                color = colorResource(id = R.color.User_Text),
                fontSize = 18.sp
            )
    }

}

@Composable
fun ChatFromModel(prompt: String) {
    Column(
        modifier = Modifier
            .padding(end = 100.dp, bottom = 20.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp , bottom = 4.dp , top = 4.dp , end = 4.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colorResource(id = R.color.Model_chat_background)),
            text = prompt,
            color = colorResource(id = R.color.Model_Text),
            fontSize = 18.sp
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
fun ChatPreview() {
    ChatBotScreen()
}