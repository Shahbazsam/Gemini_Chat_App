package com.example.geminichatapp.ui.presentation

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AddAPhoto
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
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
import com.example.geminichatapp.ui.presentation.events.ChatUiEvent
import com.example.geminichatapp.ui.presentation.screens.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotAppBar() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) {
        ChatScreen(paddingValues = it)
    }
}

@Composable
fun ChatScreen(paddingValues: PaddingValues) {

    val chatViewModel:ChatViewModel = viewModel(factory = ChatViewModel.Factory)
    val chatState = chatViewModel.chatState.collectAsState().value
    val imagePicker = ImagePicker()

    val bitmap = getBitmap(chatViewModel)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                vertical = dimensionResource(R.dimen.padding_main_screen),
                horizontal = dimensionResource(R.dimen.padding_main_screen)
            ),
        verticalArrangement = Arrangement.Bottom
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            reverseLayout = true
        ) {
            itemsIndexed(chatState.chatList) { _, chat ->
                if (chat.isFromUser)
                    ChatFromUser(prompt = chat.prompt , bitmap = chat.bitmap)
                else
                    ChatFromModel(prompt = chat.prompt)
            }
        }
        Row (
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ){
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
                        .clickable {
                            imagePicker.launch(
                                PickVisualMediaRequest
                                    .Builder()
                                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    .build()
                            )
                        },
                    imageVector = Icons.Rounded.AddAPhoto ,
                    contentDescription = stringResource(R.string.add_bitmap),
                    tint = MaterialTheme.colorScheme.primaryContainer
                )
            }
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_spacer)))
            TextField(
                modifier = Modifier
                    .weight(1f),
                value = chatState.prompt,
                onValueChange = {
                    chatViewModel.onEvent(ChatUiEvent.UpdatePrompt(it))
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.ask_genie),
                        color = MaterialTheme.colorScheme.outline
                        )
                },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurface, // Text color when focused
                    unfocusedTextColor = MaterialTheme.colorScheme.surface, // Text color when unfocused
                    cursorColor = MaterialTheme.colorScheme.tertiaryContainer, // Cursor color
                    focusedIndicatorColor = MaterialTheme.colorScheme.tertiaryContainer, // No underline when focused
                    unfocusedIndicatorColor = Color.Transparent, // No underline when unfocused
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer, // Background color when focused
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer // Background color when unfocused
                )
            )
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_spacer)))
            Icon(
                modifier = Modifier
                    .size(dimensionResource(R.dimen.image_size))
                    .clickable {
                        chatViewModel.onEvent(
                            ChatUiEvent.SendPrompt(
                                chatState.prompt,
                                chatState.bitmap
                            )
                        )
                    },
                imageVector = Icons.AutoMirrored.Rounded.Send,
                contentDescription = stringResource(R.string.send_prompt),
                tint = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}

@Composable
fun ChatFromUser(prompt : String , bitmap : Bitmap?) {
    Box(
        modifier = Modifier
            .padding(start = dimensionResource(R.dimen.padding_large),
                bottom = dimensionResource(R.dimen.padding_chat_model)
            )
    ) {
        bitmap?.let {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(MaterialTheme.shapes.medium),
//                    .padding(bottom = 2.dp),
                contentScale = ContentScale.Crop,
                bitmap = it.asImageBitmap(),
                contentDescription = stringResource(R.string.image)
            )
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_extra_small))
                .clip(MaterialTheme.shapes.medium)
                .background(colorResource(R.color.User_chat_background)),
            text = prompt,
            color = colorResource(R.color.User_Text),
            fontSize = 18.sp
        )
    }
}

@Composable
fun ChatFromModel(prompt: String) {
    val styledText = parseRichText(prompt)
    Box(
        modifier = Modifier
            .padding(end = dimensionResource(R.dimen.padding_large),
                bottom = dimensionResource(R.dimen.padding_chat_model)
            )
    ) {
        ClickableText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_extra_small))
                .clip(MaterialTheme.shapes.medium)
                .background(colorResource(R.color.User_Text)),
            text = styledText,
            /*color = colorResource(R.color.Model_Text),
            fontSize = 18.sp,*/
            onClick = { offset ->
                styledText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                    .firstOrNull()?.let { annotation ->
                        val url = annotation.item
                        openLinkInBrowser(url)
                    }
            },
        )
    }
}

@Composable
private fun parseRichText(rawText: String): AnnotatedString {
    val builder = AnnotatedString.Builder()

    val lines = rawText.lines()
    for (line in lines) {
        when {
            line.startsWith("**") && line.endsWith(":**") -> {
                // Bold headings
                builder.withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(line.removeSurrounding("**").trim())
                }
                builder.append("\n")
            }
            line.startsWith("* ") -> {
                // Bullet points
                builder.append("• ")
                builder.append(line.removePrefix("* ").trim())
                builder.append("\n")
            }
            Regex("\\[([^\\]]+)]\\(([^)]+)\\)").containsMatchIn(line) -> {
                // Links [text](url)
                val match = Regex("\\[([^\\]]+)]\\(([^)]+)\\)").find(line)
                if (match != null) {
                    val (text, url) = match.destructured
                    builder.pushStringAnnotation(tag = "URL", annotation = url)
                    builder.withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline)) {
                        append(text)
                    }
                    builder.pop()
                    builder.append("\n")
                }
            }
            line.isNotBlank() -> {
                // Plain text
                builder.append(line.trim())
                builder.append("\n")
            }
        }
    }
    return builder.toAnnotatedString()
}
fun openLinkInBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    //context.startActivity(intent) // Un-comment when inside an Activity/Context
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
    ChatBotAppBar()
}