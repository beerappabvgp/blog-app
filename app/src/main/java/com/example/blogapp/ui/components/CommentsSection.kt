package com.example.blogapp.ui.components

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.blogapp.network.Blog
import com.example.blogapp.network.Comment
import com.example.blogapp.utils.TokenManager
import com.example.blogapp.viewmodel.AddCommentViewModel
import com.example.blogapp.viewmodel.LikeCommentViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.MultipartBody


@Composable
fun CommentItem(comment: Comment, blogId: String) {
    val context = LocalContext.current
    val tokenManager = TokenManager(context)
    val token = tokenManager.getToken()
    val userId = tokenManager.getUser()?._id

    val initialCount = comment.likes.size
    val liked = comment.likes.any { it._id == userId }
//    var isLiked by rememberSaveable { mutableStateOf(blog.likes.any { it._id == currentUserId }) }
    // Create a ViewModel for each comment
    val likeViewModel: LikeCommentViewModel = remember {
        LikeCommentViewModel(initialCount, liked)
    }

    // Observe like count and like state from ViewModel
    val likeCount by likeViewModel.likeCount.collectAsState()
    val isLiked by likeViewModel.isLiked.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        if (!comment.userId.profilePicture.isNullOrEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(comment.userId.profilePicture),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = comment.userId.username ?: "Unknown",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = comment.content,
                fontSize = 16.sp
            )

            if (!comment.images.isNullOrEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    comment.images.forEach { imageUrl ->
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Comment Image",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Step 2: Add Like Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    coroutineScope.launch {
                        likeViewModel.toggleLike(blogId, comment._id!! , token!!)
                    }
                }
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like Button",
                    tint = if (isLiked) Color.Red else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(text = likeCount.toString(), fontSize = 14.sp)
            }
        }
    }
}


@Composable
fun CommentInputSection(
    commentText: String,
    onCommentTextChange: (String) -> Unit,
    selectedImages: List<Uri>,
    onImagesSelected: (List<Uri>) -> Unit,
    isLoading: Boolean,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = commentText,
            onValueChange = onCommentTextChange,
            label = { Text("Write a comment...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSubmit,
            enabled = commentText.isNotEmpty() && !isLoading,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = if (isLoading) "Posting..." else "Post Comment")
        }
    }
}