    package com.example.blogapp.ui.components

    import androidx.compose.foundation.*
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.LazyRow
    import androidx.compose.foundation.lazy.items
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material3.*
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import coil.compose.rememberAsyncImagePainter
    import com.example.blogapp.network.Blog

    @Composable
    fun BlogItem(blog: Blog) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Profile Info Section
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Profile Picture (Handle null or empty URLs gracefully)
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

                    // Author Info (Username and About)
                    Column {
                        Text(
                            text = blog.author.username ?: "Unknown",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = blog.author.about ?: "No information available",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Blog Title
                Text(
                    text = blog.title ?: "Default title",  // Fallback for null title
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
                )

                Spacer(modifier = Modifier.height(6.dp))

                Spacer(modifier = Modifier.height(12.dp))

                // Blog Content
                Text(
                    text = blog.content ?: "Default content",  // Fallback for null content
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Image Section (If Available)
                if (!blog.images.isNullOrEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(blog.images) { imageUrl ->
                            Image(
                                painter = rememberAsyncImagePainter(imageUrl),
                                contentDescription = "Blog Image",
                                modifier = Modifier
                                    .height(180.dp)
                                    .width(240.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp)) // Adds a subtle border to images
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
