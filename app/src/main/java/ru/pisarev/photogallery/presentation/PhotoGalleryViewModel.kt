package ru.pisarev.photogallery.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ru.pisarev.photogallery.data.FlickrFetchr
import ru.pisarev.photogallery.domain.GalleryItem

class PhotoGalleryViewModel: ViewModel() {
    val galleryItemLiveData: LiveData<List<GalleryItem>>

    init {
        galleryItemLiveData = FlickrFetchr().searchPhotos("planets")
    }
}