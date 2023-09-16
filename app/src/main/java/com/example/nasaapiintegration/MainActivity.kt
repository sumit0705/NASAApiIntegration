package com.example.nasaapiintegration

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.nasaapiintegration.database.SharedPreferencesHelper
import com.example.nasaapiintegration.initializers.MainActivityViewInitializer
import com.example.nasaapiintegration.models.NASAData
import com.example.nasaapiintegration.repo.MyRepository
import com.example.nasaapiintegration.utils.NetworkUtils

/** Starting point of the App. */
class MainActivity : AppCompatActivity() {

    /** This variable will contain latest [NASAData] from API response. */
    private lateinit var nasaData: NASAData

    /** This will be visible to user when API fetching takes place. */
    private var loadingDialog: ProgressDialog? = null

    /**
     * This will be visible to user when error occurred while fetching API response and it will
     * show a AlertDialog to ask from user to again fetch the API data.
     */
    private var failureDialog: AlertDialog? = null

    /**
     * The [MainActivityViewInitializer] instance used to initialize and manage view components
     * within the [MainActivity].
     */
    private lateinit var viewInitializer: MainActivityViewInitializer


    /** ViewModel to fetch the API result. */
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!NetworkUtils.isInternetConnected(this)) {
            generateToast("Check your internet connection")
            finish()
        }
        viewInitializer = MainActivityViewInitializer(findViewById(android.R.id.content))

        setupViewModel()
        setClickListeners()
    }

    /**
     * This method will set up the [viewModel] and adds an observer for NASA Data and Api request
     * status.
     */
    private fun setupViewModel() {
        val sharedPreferences: SharedPreferences = getSharedPreferences(
            "MyNasaDataCache",
            Context.MODE_PRIVATE
        )
        val repo = MyRepository(SharedPreferencesHelper(sharedPreferences))
        val viewModelFactory = MainViewModelFactory(application, repo)
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        viewModel.nasaLiveData.observe(this) { data ->
            nasaData = data
            updateUI()
        }

        viewModel.requestStatusLiveData.observe(this) { requestStatus ->
            handleRequestStatus(requestStatus)
        }
    }

    /** This method will set click listeners. */
    private fun setClickListeners() {
        viewInitializer.swipeRefreshLayout.setOnRefreshListener {
            fetchDataAndUpdateUI()
        }

        viewInitializer.playButton.setOnClickListener {
            viewInitializer.videoView.visibility = View.VISIBLE
            viewInitializer.imageView.visibility = View.GONE
            viewInitializer.playButton.visibility = View.GONE

            // Enable JavaScript in the WebView.
            val webSettings: WebSettings = viewInitializer.videoView.settings
            webSettings.javaScriptEnabled = true

            // Enable video playback in WebView.
            webSettings.mediaPlaybackRequiresUserGesture = false

            // Play the video in WebView.
            viewInitializer.videoView.loadUrl(nasaData.url)
        }
    }

    /** This method will be used to update the UI via [viewInitializer]. */
    private fun updateUI() {
        var imageUrl = nasaData.url
        viewInitializer.videoView.visibility = View.GONE
        if (nasaData.mediaType == "video") {
            viewInitializer.playButton.visibility = View.VISIBLE
            nasaData.thumb_url?.let {
                imageUrl = it
            }
        } else {
            viewInitializer.playButton.visibility = View.GONE
        }
        viewInitializer.imageView.visibility = View.VISIBLE
        Glide.with(this).load(imageUrl).listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                viewInitializer.swipeRefreshLayout.isRefreshing = false
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                viewInitializer.swipeRefreshLayout.isRefreshing = false
                return false
            }
        }).into(viewInitializer.imageView)
        viewInitializer.topText.text = "NASA Data of the day!"
        viewInitializer.title.text = "Title: ${nasaData.title}"
        viewInitializer.date.text = "Date: ${nasaData.date}"
        viewInitializer.description.text = "Description: ${nasaData.description}"
    }

    /** This method will be used to handle the API request status of Volley library. */
    private fun handleRequestStatus(requestStatus: MainViewModel.RequestStatus) {
        when (requestStatus) {
            MainViewModel.RequestStatus.IN_PROGRESS -> showSpinner()
            MainViewModel.RequestStatus.SUCCEEDED -> hideSpinner()
            MainViewModel.RequestStatus.FAILED -> showError()
        }
    }

    /** This method will show spinner i.e. [loadingDialog] when API request is still in progress. */
    private fun showSpinner() {
        if (loadingDialog == null) {
            loadingDialog = ProgressDialog(this)
        }
        loadingDialog?.let { progressDialog ->
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progressDialog.setTitle("Fetching NASA Data")
            progressDialog.setMessage("Please wait...")
            progressDialog.setIndeterminate(true)
            progressDialog.setCancelable(false)
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
        }
    }

    /**
     * This method will dismiss the spinner i.e. [loadingDialog] when API request provides
     * successful response.
     */
    private fun hideSpinner() {
        loadingDialog?.dismiss()
    }

    /** This method will show the [failureDialog] when error occurs while fetching the API response. */
    private fun showError() {
        hideSpinner()
        if (failureDialog == null) {
            failureDialog = getFailureDialog()
        }
        failureDialog?.let { alertDialog ->
            alertDialog.show()
            alertDialog.setCanceledOnTouchOutside(false)
        }
    }

    /** This method will return a [AlertDialog] to show when error occurs while fetching the API response. */
    private fun getFailureDialog(): AlertDialog {
        return AlertDialog.Builder(this)
            .setTitle("NASA data request failed")
            .setMessage("NASA Data fetching is failed, do you want to retry?")
            .setPositiveButton("Retry") { dialog, _ ->
                dialog.dismiss()
                viewModel.refetchNasaData()
            }
            .setNegativeButton("Close app") { dialog, _ ->
                dialog.dismiss()
                this.finish()
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setCancelable(false)
            .create()
    }

    override fun onPause() {
        super.onPause()
        // Check if the WebView is currently playing a video and pause it.
        pauseYoutubeVideo()
    }

    /**
     * This method will pause the youtube video playing on WebView. We will pause in two scenarios:
     * 1. When activity goes in background.
     * 2. When user again fetches the [NASAData] using pull-down to refresh gesture via SwipeRefreshLayout.
     */
    private fun pauseYoutubeVideo() {
        viewInitializer.videoView.loadUrl("javascript:document.querySelector('video').pause();")
    }

    override fun onDestroy() {
        super.onDestroy()
        failureDialog?.dismiss()
        loadingDialog?.dismiss()
    }


    /**
     * This method is triggered by the pull-down to refresh gesture via SwipeRefreshLayout and it
     * will again fetch the [NASAData] and update the UI accordingly.
     */
    private fun fetchDataAndUpdateUI() {
        generateToast("fetching data on pull down to refresh gesture")
        pauseYoutubeVideo()
        viewModel.refetchNasaData()
    }

    /** This method will show the toast. */
    private fun generateToast(msg: String) {
        Toast.makeText(
            this,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }
}