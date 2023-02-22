package ru.pisarev.photogallery.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.pisarev.photogallery.data.api.FlickrApi
import ru.pisarev.photogallery.data.api.FlickrResponse
import ru.pisarev.photogallery.data.api.PhotoInterceptor
import ru.pisarev.photogallery.data.api.PhotoResponse
import ru.pisarev.photogallery.domain.GalleryItem

private const val TAG = "FlickrFetchr"

class FlickrFetchr {
    private val flickrApi: FlickrApi

    init {
        val client = OkHttpClient.Builder()
            .addInterceptor(PhotoInterceptor())
            .build()


        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create()) //ScalarsConverterFactory.create()
            .client(client)
            .build()
        flickrApi = retrofit.create(FlickrApi::class.java)
    }
    fun fetchPhotos(): LiveData<List<GalleryItem>> {
        return fetchPhotoMetadata(flickrApi.fetchPhotos())
    }

    fun searchPhotos(query: String): LiveData<List<GalleryItem>> {
        return fetchPhotoMetadata(flickrApi.searchPhotos(query))
    }
    private fun fetchPhotoMetadata(flickrRequest: Call<FlickrResponse>)
    : LiveData<List<GalleryItem>> {

        val responseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()
        flickrRequest.enqueue(object : Callback<FlickrResponse> {

            override fun onResponse(call: Call<FlickrResponse>, response: Response<FlickrResponse>) {
                Log.d(TAG,"Response received: ${response.body()}")
                val flickrResponse: FlickrResponse? = response.body()
                val photoResponse: PhotoResponse? = flickrResponse?.photos
                var gallaryItems: List<GalleryItem> = photoResponse?.galleryItems
                    ?: mutableListOf()
                gallaryItems = gallaryItems.filterNot {
                    it.url.isNullOrBlank()
                }
                responseLiveData.value=gallaryItems
            }

            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                Log.d(TAG,"Failed to feath photos", t)
            }
        })
        return responseLiveData
    }
    @WorkerThread
    fun fetchPhoto(url: String): Bitmap? {
        val response: Response<ResponseBody> = flickrApi.featchUrlBytes(url).execute()
        val bitmap = response.body()?.byteStream()?.use(BitmapFactory::decodeStream)
        Log.i(TAG,"Decode bitmap = $bitmap from Response=$response")
        return bitmap
    }
}