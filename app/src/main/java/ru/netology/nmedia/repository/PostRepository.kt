package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAll(): List<Post>
    fun likeById(id: Long)
    fun unlikeById(id: Long)
    fun save(post: Post)
    fun removeById(id: Long)

    fun getAllAsync(callback: GetAllCallback)

    interface GetAllCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }

    fun removeByIdAsync(id: Long, callback: RemoveByIdCallback)

    interface RemoveByIdCallback {
        fun onSuccess(posts: List<Post>) {}
        fun onError(e: Exception) {}
    }

    fun saveAsync(post: Post, callback: SaveCallback)

    interface SaveCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Exception) {}
    }

    fun likeByIdAsync(id: Long, callback: LikeByIdCallback)

    interface LikeByIdCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Exception) {}
    }

    fun unLikeByIdAsync(id: Long, callback: UnLikeByIdCallback)

    interface UnLikeByIdCallback {
        fun onSuccess(post: Post) {}
        fun onError(e: Exception) {}
    }
}
