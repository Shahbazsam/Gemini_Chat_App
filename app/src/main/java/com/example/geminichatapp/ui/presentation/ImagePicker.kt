package com.example.geminichatapp.ui.presentation

import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.geminichatapp.data.ChatUiEvent
import kotlinx.coroutines.launch


@Composable
fun ImagePicker() : ActivityResultLauncher<PickVisualMediaRequest> {

    val coroutineScope = rememberCoroutineScope()
    val viewModel = viewModel<ChatViewModel>()
    val context = LocalContext.current

    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                coroutineScope.launch {
                    try {
                        val bitmap = BitmapFactory.decodeStream(
                            context.contentResolver.openInputStream(uri)
                        )
                        viewModel.onEvent(
                            ChatUiEvent.UpdateBitmap(bitmap)
                        )
                    }catch (e:Exception) {
                        Toast.makeText(context , "$e" , Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
    )
}