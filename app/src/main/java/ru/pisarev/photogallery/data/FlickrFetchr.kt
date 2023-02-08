package ru.pisarev.photogallery.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.pisarev.photogallery.data.api.FlickrApi
import ru.pisarev.photogallery.data.api.FlickrResponse
import ru.pisarev.photogallery.data.api.PhotoResponse
import ru.pisarev.photogallery.domain.GalleryItem

private const val TAG = "FlickrFetchr"

class FlickrFetchr {
    private val flickrApi: FlickrApi

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create()) //ScalarsConverterFactory.create()
            .build()
        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    fun fetchPhotos(): LiveData<List<GalleryItem>> {
        val responseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()
        val flickrRequest: Call<FlickrResponse> = flickrApi.fetchPhotos()

        flickrRequest.enqueue(object : Callback<FlickrResponse> {

            override fun onResponse(call: Call<FlickrResponse>, response: Response<FlickrResponse>) {
                Log.d(TAG,"Response received: ${response.body()}")
                val flickrResponse: FlickrResponse? = response.body()
                val photoResponse: PhotoResponse? = flickrResponse?.photos
                var gallaryItems: List<GalleryItem> = photoResponse?.galleryItems
                    ?: mutableListOf()
                gallaryItems = gallaryItems.filterNot {
                    it.url.isBlank()
                }
                responseLiveData.value=gallaryItems
            }

            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                Log.d(TAG,"Failed to feath photos", t)
            }
        })
        return responseLiveData
    }
}