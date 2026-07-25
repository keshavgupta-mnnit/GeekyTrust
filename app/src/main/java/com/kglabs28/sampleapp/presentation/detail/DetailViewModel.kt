package com.kglabs28.sampleapp.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kglabs28.sampleapp.data.local.db.entity.Article
import com.kglabs28.sampleapp.data.usecase.GetNewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getNewsUseCase: GetNewsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _article = MutableStateFlow<Article?>(null)
    val article = _article.asStateFlow()

    init {
        savedStateHandle.get<String>("url")?.let { url ->
            Timber.d("Loading article detail for url: $url")
            viewModelScope.launch {
                _article.value = getNewsUseCase.getById(url)
            }
        }
    }
}
