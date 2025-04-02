package com.example.blogapp.ui.components

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.blogapp.network.Blog
import com.example.blogapp.utils.TokenManager
import com.example.blogapp.viewmodel.AddCommentViewModel
import com.example.blogapp.viewmodel.LikeDislikeBlogViewModel

@Composable
fun BlogItem(
    blog: Blog,
    navController: NavController,
    tokenManager: TokenManager
) {
    val likeDislikeBlogViewModel: LikeDislikeBlogViewModel = viewModel { LikeDislikeBlogViewModel(tokenManager) }
    val addCommentViewModel: AddCommentViewModel = viewModel { AddCommentViewModel(tokenManager) }

    val currentUser = tokenManager.getUser()
    val currentUserId = currentUser?._id ?: ""

    var isLiked by rememberSaveable { mutableStateOf(blog.likes.contains(currentUserId)) }
    var likeCount by rememberSaveable { mutableStateOf(blog.likes.size) }

    // Comments list
    var comments by remember { mutableStateOf(blog.comments) }

    // Only observe comments for this specific blog
    val newComments by addCommentViewModel.newComments.collectAsState()
    val newCommentForThisBlog = newComments[blog._id]

    // Update UI when a new comment is added
    LaunchedEffect(newCommentForThisBlog) {
        newCommentForThisBlog?.let {
            comments = listOf(it) + comments // Add new comment to the top
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .padding(16.dp)
            .clickable { navController.navigate("blogDetail/${blog._id}") }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val profilePicture = blog.author.profilePicture
                if (!profilePicture.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(profilePicture),
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = blog.author.username ?: "Unknown", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = blog.author.about ?: "No information available", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = blog.title ?: "Default title", style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp))

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = blog.content ?: "Default content", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp), maxLines = 5)

            Spacer(modifier = Modifier.height(16.dp))

            if (!blog.images.isNullOrEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(blog.images) { imageUrl ->
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = "Blog Image",
                            modifier = Modifier
                                .height(180.dp)
                                .width(240.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    likeDislikeBlogViewModel.toggleLike(blog._id) { liked, count ->
                        isLiked = liked
                        likeCount = count
                    }
                }) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (isLiked) "Liked" else "Not Liked",
                        tint = if (isLiked) Color.Red else Color.Gray
                    )
                }
                Text(text = "Likes: $likeCount", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                comments.take(3).forEach { comment ->
                    CommentItem(comment = comment)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                AddCommentSection(blog._id, addCommentViewModel)
            }
        }
    }
}
