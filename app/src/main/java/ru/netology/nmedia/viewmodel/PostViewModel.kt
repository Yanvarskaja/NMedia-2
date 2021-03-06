package ru.netology.nmedia.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.repository.*
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    content = "",
    author = "",
    authorAvatar = "",
    likedByMe = false,
    likes = 0,
    published = "",
    attachment = null
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    // упрощённый вариант
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        loadPosts()
    }

//    fun loadPosts() {
//        thread {
//            // Начинаем загрузку
//            _data.postValue(FeedModel(loading = true))
//            try {
//                // Данные успешно получены
//                val posts = repository.getAll()
//                FeedModel(posts = posts, empty = posts.isEmpty())
//            } catch (e: IOException) {
//                // Получена ошибка
//                FeedModel(error = true)
//            }.also(_data::postValue)
//        }
//    }

    fun loadPosts() {
        _data.value = FeedModel(loading = true)
        repository.getAllAsync(object : PostRepository.GetAllCallback {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

//    fun save() {
//        edited.value?.let {
//            thread {
//                repository.save(it)
//                _postCreated.postValue(Unit)
//            }
//        }
//        edited.value = empty
//    }

    fun save() {
        edited.value?.let {
                repository.saveAsync(it, object : PostRepository.SaveCallback{
                    override fun onSuccess(post: Post) {
                        _postCreated.postValue(Unit)
                        super.onSuccess(post)
                    }
                    override fun onError(e: Exception) {
                      Toast.makeText(getApplication(), "Error", Toast.LENGTH_SHORT).show()
                    }
                })

        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long) {
        val liked = data.value?.posts.orEmpty().filter { it.id == id }.none { it.likedByMe }
        val posts = _data.value?.posts.orEmpty()
            if (liked) {
                repository.likeByIdAsync(id, object : PostRepository.LikeByIdCallback{
                    override fun onSuccess(post : Post) {
                        _data.postValue(_data.value?.copy(posts = posts))
                    }
                    override fun onError(e: Exception) {
                        Toast.makeText(getApplication(), "Error", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                repository.unLikeByIdAsync(id, object : PostRepository.UnLikeByIdCallback{
                    override fun onSuccess(post : Post) {
                        _data.postValue(_data.value?.copy(posts = posts))
                    }
                    override fun onError(e: Exception) {
                        Toast.makeText(getApplication(), "Error", Toast.LENGTH_SHORT).show()
                    }
                })
            }
    }
//    fun removeById(id: Long) {
//        thread {
//            // Оптимистичная модель
//            val old = _data.value?.posts.orEmpty()
//            _data.postValue(
//                _data.value?.copy(posts = _data.value?.posts.orEmpty()
//                    .filter { it.id != id }
//                )
//            )
//            try {
//                repository.removeById(id)
//            } catch (e: IOException) {
//                _data.postValue(_data.value?.copy(posts = old))
//            }
//        }
//    }

    fun removeById(id: Long) {
            // Оптимистичная модель
            val old = _data.value?.posts.orEmpty()
            _data.postValue(
                _data.value?.copy(posts = _data.value?.posts.orEmpty()
                    .filter { it.id != id }
                )
            )
                repository.removeByIdAsync(id, object : PostRepository.RemoveByIdCallback {
                    override fun onSuccess(posts: Unit) {
                        _data.postValue(_data.value?.copy(posts = old))
                    }
                    override fun onError(e: Exception) {
                        Toast.makeText(getApplication(), "Error", Toast.LENGTH_SHORT).show()
                    }
                })
            }

    }

