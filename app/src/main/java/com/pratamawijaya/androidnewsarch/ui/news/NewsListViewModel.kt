package com.pratamawijaya.androidnewsarch.ui.news

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.pratamawijaya.androidnewsarch.data.repository.NewsRepository
import com.pratamawijaya.androidnewsarch.domain.model.Article
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

private val TAG = NewsListViewModel::class.java.name

class NewsListViewModel @Inject constructor(private val repo: NewsRepository) : ViewModel() {

    // declare state for news list
    val stateLiveData = MutableLiveData<NewsListState>()

    // initiate state for news list
    init {
        stateLiveData.value = LoadingState(emptyList(), false)
    }

    fun updateNewsList() {
        Log.d(TAG, "update news list")
        getNewsList()
    }

    fun restoreNewsList() {
        Log.d(TAG, "restore news list")
        stateLiveData.value = DefaultState(obtainCurrentData(), true)
    }

    private fun getNewsList() {
        repo.getTopNews(country = "us", category = "technology")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onNewsReceived, this::onError)
    }

    private fun onError(error: Throwable) {
        Log.e(TAG, "error ${error.localizedMessage}")
        stateLiveData.value = ErrorState(error.localizedMessage, obtainCurrentData(), false)
    }

    private fun onNewsReceived(news: List<Article>) {
        Log.d(TAG, "data news received ${news.size}")
        val currentNews = obtainCurrentData().toMutableList()
        currentNews.addAll(news)
        stateLiveData.value = DefaultState(currentNews, true)
    }

    private fun obtainCurrentData() = stateLiveData.value?.data ?: emptyList()

    private fun obtainCurrentLoadedAllItems() = stateLiveData.value?.loadedAllItems ?: false
}
