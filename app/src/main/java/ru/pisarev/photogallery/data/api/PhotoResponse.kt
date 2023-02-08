package ru.pisarev.photogallery.data.api

import com.google.gson.annotations.SerializedName
import ru.pisarev.photogallery.domain.GalleryItem

class PhotoResponse {
    @SerializedName("photo")
    lateinit var galleryItems: List<GalleryItem>
}