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
    val author: Author,
    val images: List<String>?,
    val createdAt: String
)

data class Author(
    val _id: String,
    val username: String,
    val email: String,
    val about: String,  // New field
    val profilePicture: String  // New field
)

data class BlogUpdateRequest(
    val title: String,
    val content: String,
    val images: List<String>?,
    val imagesToDelete: List<String>
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

    @PATCH("blogs/{id}")
    suspend fun updateBlog(
        @Path("id") blogId: String,
        @Body request: BlogUpdateRequest,  // Pass the custom request object
        @Header("Authorization") token: String // Keep token as a header
    ): Response<Blog>



}

