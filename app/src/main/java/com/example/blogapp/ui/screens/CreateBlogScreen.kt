package com.example.blogapp.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.blogapp.R
import com.example.blogapp.network.BlogRetrofitClient
import com.example.blogapp.utils.TokenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import okhttp3.RequestBody.Companion.asRequestBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBlogScreen(navController: NavHostController) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var images by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val token = "Bearer ${TokenManager(context).getToken()}"
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { selectedImages -> images = selectedImages }
    )

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Create Blog", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Create a New Blog", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 5
            )

            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Photo, contentDescription = "Pick Images")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pick Images")
            }

            // **Improved Image Preview Layout**
            if (images.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(images) { uri ->
                        Image(
                            painter = rememberAsyncImagePainter(uri),
                            contentDescription = "Selected Image",
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.LightGray)
                                .padding(4.dp)
                        )
                    }
                }
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }

            Button(
                onClick = {
                    if (title.isEmpty() || content.isEmpty()) {
                        scope.launch { snackbarHostState.showSnackbar("Title and Content cannot be empty") }
                    } else {
                        isLoading = true
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
                                val contentPart = content.toRequestBody("text/plain".toMediaTypeOrNull())

                                val imageParts = images.map { uri ->
                                    val file = File(getRealPathFromURI(context, uri) ?: return@map null)
                                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                                    MultipartBody.Part.createFormData("images", file.name, requestFile)
                                }.filterNotNull()

                                val response = BlogRetrofitClient.instance.createBlog(token, titlePart, contentPart, imageParts)

                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    if (response.isSuccessful) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "Blog Created Successfully!",
                                                actionLabel = "OK",
                                                withDismissAction = true
                                            )
                                        }
                                        navController.popBackStack()
                                    } else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "Failed: ${response.errorBody()?.string()}",
                                                actionLabel = "Retry",
                                                withDismissAction = true
                                            )
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            message = "Error: ${e.message}",
                                            actionLabel = "OK",
                                            withDismissAction = true
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = title.isNotEmpty() && content.isNotEmpty()
            ) {
                Text("Create Blog")
            }
        }
    }
}

// **Function to get real image path**
fun getRealPathFromURI(context: Context, uri: Uri): String? {
    var cursor: Cursor? = null
    try {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        cursor = context.contentResolver.query(uri, projection, null, null, null)
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        return cursor?.getString(columnIndex ?: -1)
    } catch (e: Exception) {
        Log.e("GetRealPath", "Failed to get real path from URI: ${e.message}")
    } finally {
        cursor?.close()
    }
    return null
}
