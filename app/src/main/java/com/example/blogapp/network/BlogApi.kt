package com.example.blogapp.network


import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// Data class for Blog creation response
data class CreateBlogResponse(
    val message: String,
    val blog: Blog
)

// Data class for Blog object
data class Blog(
    val _id: String,
    val title: String,
    val content: String,
    val author: Author,  // Author details
    val images: List<String>? = null,
    val createdAt: String,
    val likes: List<Author>,
    val comments: List<Comment> // New field for comments
)


data class Author(
    val _id: String,
    val username: String,
    val email: String,
    val about: String,
    val profilePicture: String
)

// Data class for comment request
data class CreateCommentRequest(
    val content: String,  // Comment content
    val images: List<MultipartBody.Part>? = null // Images as multipart files
)

// Data class for comment response
data class CreateCommentResponse(
    val message: String,
    val blog: Blog
)

// Updated Comment class to include images
data class Comment(
    val _id: String?,
    val content: String, // Comment content
    val userId: Author,  // Author details
    val images: List<String>? = null, // List of image URLs
    val likes: List<Author>,
    val createdAt: String
)


// Modify BlogUpdateRequest to accept List<MultipartBody.Part>
data class BlogUpdateRequest(
    val title: String,
    val content: String,
    val images: List<MultipartBody.Part>?, // Multipart form-data for images
    val imagesToDelete: List<String> // List of image URLs or IDs to be deleted
)

data class DeleteBlogResponse(
    val message: String
)

data class LikeBlogResponse(
    val message: String,
    val liked: Boolean,
    val likeCount: Int
)

// Request data class for liking a comment
data class LikeCommentRequest(
    val userId: String // User ID of the person liking the comment
)

// Response data class for liking a comment
data class LikeCommentResponse(
    val success: Boolean,
    val message: String,
    val likes: Int,  // Updated like count
    val userLiked: Boolean // Indicates if the user liked the comment
)


interface BlogApi {

    @GET("blogs/user")
    suspend fun getUserPosts(@Header("Authorization") token: String): Response<List<Blog>>

    @Multipart
    @POST("blogs")
    suspend fun createBlog(
        @Header("Authorization") token: String,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part images: List<MultipartBody.Part>
    ): Response<CreateBlogResponse>

    @GET("blogs")
    suspend fun getBlogs(@Header("Authorization") token: String): Response<List<Blog>>

    @GET("blogs/{id}")
    suspend fun getBlogDetails(@Header("Authorization") token: String, @retrofit2.http.Path("id") id: String): Response<Blog>

    @Multipart
    @PATCH("blogs/{id}")
    suspend fun updateBlog(
        @Path("id") blogId: String,
        @Header("Authorization") token: String,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part images: List<MultipartBody.Part>?,  // Updated images
        @Part("imagesToDelete") imagesToDelete: RequestBody // Send imagesToDelete as JSON string
    ): Response<Blog>

    @DELETE("blogs/{id}")
    suspend fun deleteBlog(
        @Path("id") blogId: String,
        @Header("Authorization") token: String
    ): Response<DeleteBlogResponse>

    @POST("blogs/{id}/like")
    suspend fun likeBlog(
        @Header("Authorization") token: String,
        @Path("id") blogId: String
    ): Response<LikeBlogResponse>

    @Multipart
    @POST("blogs/{id}/comment")
    suspend fun addComment(
        @Header("Authorization") token: String,
        @Path("id") blogId: String,
        @Part("content") content: RequestBody,
        @Part images: List<MultipartBody.Part>? = null
    ): Response<CreateCommentResponse>


    @POST("blogs/{blogId}/comments/{commentId}/like")
    suspend fun likeComment(
        @Header("Authorization") token: String,
        @Path("blogId") blogId: String,
        @Path("commentId") commentId: String
    ): Response<LikeCommentResponse>
}

