package ru.pisarev.photogallery.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.pisarev.photogallery.data.FlickrFetchr
import ru.pisarev.photogallery.domain.GalleryItem

class PhotoGalleryViewModel(private val app: Application): AndroidViewModel(app) {
    val galleryItemLiveData: LiveData<List<GalleryItem>>

    private val flickrFetchr = FlickrFetchr()
    private val mutableSearchTerm = MutableLiveData<String>()

    val searchTerm: String
        get() = mutableSearchTerm.value ?: ""

    init {
        mutableSearchTerm.value = QueryPreferences.getStoredQuery(app)

        galleryItemLiveData = Transformations.switchMap(mutableSearchTerm) {
            if (it.isBlank()) {
                flickrFetchr.fetchPhotos()
            } else {
                flickrFetchr.searchPhotos(it)
            }
        }
    }

    fun fetchPhotos(query: String = ""){
        QueryPreferences.setStoredQuery(app,query)
        mutableSearchTerm.value=query
    }
}