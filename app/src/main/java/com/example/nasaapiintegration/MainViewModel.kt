package com.example.nasaapiintegration

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.example.nasaapiintegration.models.NASAData
import com.example.nasaapiintegration.repo.MyRepository
import org.json.JSONException
import org.json.JSONObject

/** Creates a new ViewModel instance. */
class MainViewModel(application: Application, repository: MyRepository) : AndroidViewModel(application),
    Response.Listener<String>, Response.ErrorListener {

    private val _requestStatusLiveData = MutableLiveData<RequestStatus>()
    val requestStatusLiveData: LiveData<RequestStatus>
        get() = _requestStatusLiveData

    private val _nasaLiveData = MutableLiveData<NASAData>()
    val nasaLiveData: LiveData<NASAData>
        get() = _nasaLiveData
    private val repo: MyRepository = repository

    init {
        _requestStatusLiveData.postValue(RequestStatus.IN_PROGRESS)
        val isCachedDataPresent = repo.getBooleanData(CACHED_DATE_KEY)
        if (isCachedDataPresent) {
            _requestStatusLiveData.postValue(RequestStatus.SUCCEEDED)
            val data = getDataFromRepo()
            _nasaLiveData.value = data
        } else {
            fetchNasaData()
        }
    }

    /** This method will access the data from the SharedPreferencesHelper via [repo]. */
    private fun getDataFromRepo(): NASAData {
        val title = repo.getData(RESPONSE_TITLE)
        val date = repo.getData(RESPONSE_DATE)
        val description = repo.getData(RESPONSE_DESCRIPTION)
        val imageUrl = repo.getData(RESPONSE_URL)
        val mediaType = repo.getData(RESPONSE_MEDIA_TYPE)
        if (mediaType == "video") {
            val thumbnailUrl = repo.getData(RESPONSE_VID_THUMB_URL)
            return NASAData(title!!, date!!, description!!, imageUrl!!, mediaType, thumbnailUrl)
        }
        return NASAData(title!!, date!!, description!!, imageUrl!!, mediaType!!, null)
    }

    /** This method will check the cache data in Volley instance. */
    private fun fetchNasaData() {
        val cachedData =
            VolleySingleton.getInstance(getApplication()).requestQueue.cache.get(API_URL)

        if (cachedData != null) {
            // Data found in cache, deserialize and update UI.
            val cachedString = String(cachedData.data)
            onResponse(cachedString)
        } else {
            // Data not in cache, fetch it from the api.
            fetchDataFromApi()
        }
    }

    /** This method will fetch the [NASAData] from Api. */
    private fun fetchDataFromApi() {
        val stringRequest = StringRequest(Request.Method.GET, API_URL, this, this)
        // Add the request to the RequestQueue
        VolleySingleton.getInstance(getApplication()).addToRequestQueue(stringRequest)
    }

    /** This method is used for re-fetching [NASAData] on pull-down to refresh gesture via SwipeRefreshLayout. */
    fun refetchNasaData() {
        _requestStatusLiveData.postValue(RequestStatus.IN_PROGRESS)
        // Fetch latest data from Api when user perform pull-to-refresh gesture.
        repo.clearCache()
        fetchDataFromApi()
    }


    /** This method will be triggered when we get Success/Failure Response from Api. */
    override fun onResponse(response: String) {
        try {
            _requestStatusLiveData.postValue(RequestStatus.SUCCEEDED)
            _nasaLiveData.value = deserializeData(response)
        } catch (e: JSONException) {
            e.printStackTrace()
            _requestStatusLiveData.postValue(RequestStatus.FAILED)
        }
    }

    /** This method will be triggered when we get error while fetching [NASAData] from Api. */
    override fun onErrorResponse(error: VolleyError?) {
        _requestStatusLiveData.postValue(RequestStatus.FAILED)
    }

    /** This method will be used to get [NASAData] from Api response. */
    private fun deserializeData(response: String): NASAData {
        try {
            val jsonObject = JSONObject(response)
            val title = jsonObject.getString(RESPONSE_TITLE)
            val date = jsonObject.getString(RESPONSE_DATE)
            val description = jsonObject.getString(RESPONSE_DESCRIPTION)
            val imageUrl = jsonObject.getString(RESPONSE_URL)
            val mediaType = jsonObject.getString(RESPONSE_MEDIA_TYPE)
            var thumbUrl = ""
            if (mediaType == "video") {
                thumbUrl = jsonObject.getString(RESPONSE_VID_THUMB_URL)
            }
            saveDataToRepo(title, date, description, imageUrl, mediaType, thumbUrl)
            return NASAData(title, date, description, imageUrl, mediaType, thumbUrl)
        } catch (e: JSONException) {
            // Handle JSON parsing error
            e.printStackTrace()
            return NASAData("", "", "", "", "", "")
        }
    }

    /** This method will save the Data i.e. [NASAData] to SharedPreferencesHelper via [repo]. */
    private fun saveDataToRepo(
        title: String,
        date: String,
        description: String,
        imageUrl: String,
        mediaType: String,
        thumbUrl: String
    ) {
        repo.saveBooleanData(CACHED_DATE_KEY, true)
        repo.saveData(RESPONSE_TITLE, title)
        repo.saveData(RESPONSE_DATE, date)
        repo.saveData(RESPONSE_DESCRIPTION, description)
        repo.saveData(RESPONSE_URL, imageUrl)
        repo.saveData(RESPONSE_MEDIA_TYPE, mediaType)
        repo.saveData(RESPONSE_VID_THUMB_URL, thumbUrl)
    }

    /**
     * Enum class to define status of NASA API request.
     */
    enum class RequestStatus {
        /* Show API is in progress. */
        IN_PROGRESS,

        /* Show API request is failed. */
        FAILED,

        /* Show API request is successfully completed. */
        SUCCEEDED
    }

    private companion object {
        /* For latest data (mostly image response). */
        const val API_URL =
            "https://api.nasa.gov/planetary/apod?api_key=R2Pji4odd8yy9iQKsH0piBGh5xOtfZcSYKTyeIk8"

        /* For image data. */
//        const val API_URL = "https://api.nasa.gov/planetary/apod?api_key=R2Pji4odd8yy9iQKsH0piBGh5xOtfZcSYKTyeIk8&date=2023-09-16&thumbs=true"

        /* For video data. */
//        const val API_URL = "https://api.nasa.gov/planetary/apod?api_key=R2Pji4odd8yy9iQKsH0piBGh5xOtfZcSYKTyeIk8&date=2023-09-06&thumbs=true"
//        const val API_URL = "https://api.nasa.gov/planetary/apod?api_key=R2Pji4odd8yy9iQKsH0piBGh5xOtfZcSYKTyeIk8&date=2023-06-17&thumbs=true"

        const val RESPONSE_TITLE = "title"
        const val RESPONSE_DATE = "date"
        const val RESPONSE_DESCRIPTION = "explanation"
        const val RESPONSE_URL = "url"
        const val RESPONSE_MEDIA_TYPE = "media_type"
        const val RESPONSE_VID_THUMB_URL = "thumbnail_url"
        const val CACHED_DATE_KEY="cached_data"
    }
}