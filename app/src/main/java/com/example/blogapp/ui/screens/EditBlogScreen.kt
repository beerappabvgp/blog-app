package com.example.blogapp.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.blogapp.network.Blog
import com.example.blogapp.viewmodel.EditBlogViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.blogapp.utils.TokenManager
import com.example.blogapp.viewmodel.BlogDetailViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun EditBlogScreen(navController: NavController, blogId: String) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    val blogDetailViewModel: BlogDetailViewModel = viewModel { BlogDetailViewModel(tokenManager) }
    val editBlogViewModel: EditBlogViewModel = viewModel { EditBlogViewModel(tokenManager) }

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var images by remember { mutableStateOf<List<String>>(emptyList()) }
    var imagesToDelete by remember { mutableStateOf<List<String>>(emptyList()) }

    val isLoading = blogDetailViewModel.isLoading
    val errorMessage = blogDetailViewModel.errorMessage
    val blogDetails = blogDetailViewModel.blog

    // Fetch blog details when blogId changes
    LaunchedEffect(blogId) {
        blogDetailViewModel.fetchBlogDetails(blogId)
    }

    // Set initial values once blog details are fetched
    LaunchedEffect(blogDetails) {
        blogDetails?.let {
            title = it.title
            content = it.content
            images = it.images ?: emptyList()
        }
    }

    // Navigate on successful blog update
    LaunchedEffect(editBlogViewModel.updatedBlog) {
        if (editBlogViewModel.updatedBlog != null) {
            navController.navigate("dashboard")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Edit Blog", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Content") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display existing images and allow removal
        if (images.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(images) { imageUrl ->
                    Box(modifier = Modifier.size(100.dp)) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Blog Image",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.primary)
                        )
                        IconButton(
                            onClick = {
                                // Add image to delete list and remove it from current images
                                imagesToDelete = imagesToDelete + imageUrl
                                images = images.filter { it != imageUrl }
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(24.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        if (isLoading) {
            CircularProgressIndicator()
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Update Blog button
        Button(
            onClick = {
                val updatedBlogObj = Blog(
                    _id = blogId,
                    title = title,
                    content = content,
                    author = blogDetails?.author ?: return@Button,
                    images = images,
                    createdAt = blogDetails?.createdAt ?: ""
                )

                // Pass imagesToDelete when updating the blog
                editBlogViewModel.updateBlog(blogId, title, content, images, imagesToDelete)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Blog")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Success message on successful blog update
        editBlogViewModel.updatedBlog?.let {
            Text(text = "Blog updated successfully!", color = MaterialTheme.colorScheme.primary)
        }
    }
}
