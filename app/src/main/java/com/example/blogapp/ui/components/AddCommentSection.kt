package com.example.blogapp.ui.components

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.blogapp.viewmodel.AddCommentViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

@Composable
fun AddCommentSection(
    blogId: String,
    addCommentViewModel: AddCommentViewModel
) {
    val context = LocalContext.current

    // State isolated for each comment section
    var commentText by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        selectedImages = selectedImages + uris // Append new images to the list
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = commentText,
            onValueChange = { commentText = it },
            label = { Text("Write a comment...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Image picker button
        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Add Images")
        }

        // Show selected images with remove option
        if (selectedImages.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                selectedImages.forEachIndexed { index, uri ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Gray)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = { selectedImages = selectedImages.toMutableList().apply { removeAt(index) } }
                        ) {
                            Text("Remove", color = Color.Red)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Submit button
        Button(
            onClick = {
                val imagesParts = convertUriListToMultipart(selectedImages, context)
                addCommentViewModel.addComment(blogId, commentText, imagesParts)
                commentText = ""
                selectedImages = emptyList()
            },
            modifier = Modifier.align(Alignment.End),
            enabled = commentText.isNotBlank() || selectedImages.isNotEmpty() // Disable if both are empty
        ) {
            Text("Post Comment")
        }
    }
}

/**
 * Converts a list of URIs to a list of MultipartBody.Part for file upload.
 */
fun convertUriListToMultipart(uris: List<Uri>, context: Context): List<MultipartBody.Part> {
    val parts = mutableListOf<MultipartBody.Part>()

    for (uri in uris) {
        try {
            val file = uriToFile(uri, context) ?: continue
            val mediaType = context.contentResolver.getType(uri)?.toMediaTypeOrNull() ?: "image/*".toMediaTypeOrNull()
            val requestFile = RequestBody.create(mediaType, file)
            val part = MultipartBody.Part.createFormData("images", file.name, requestFile)
            parts.add(part)
        } catch (e: Exception) {
            Log.e("FileConversion", "Error converting URI to file: ${e.localizedMessage}")
        }
    }

    return parts
}

/**
 * Converts a Uri to a File safely.
 */
fun uriToFile(uri: Uri, context: Context): File? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        if (inputStream == null) {
            Log.e("uriToFile", "Failed to open InputStream for URI: $uri")
            return null
        }

        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        inputStream.close()
        file
    } catch (e: Exception) {
        Log.e("uriToFile", "Error processing URI: ${e.localizedMessage}")
        null
    }
}
