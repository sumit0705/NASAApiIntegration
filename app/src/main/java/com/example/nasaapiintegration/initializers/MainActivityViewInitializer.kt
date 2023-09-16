package com.example.nasaapiintegration.initializers

import android.view.View
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.nasaapiintegration.R

/**
 * A helper class responsible for initializing and managing view components
 * within the MainActivity.
 */
class MainActivityViewInitializer(rootView: View) {
    /** The TextView representing Heading at the top (NASA Data of the day!).*/
    val topText: TextView = rootView.findViewById(R.id.topText)

    /** The TextView representing title of the API response. */
    val title: TextView = rootView.findViewById(R.id.title)

    /** The TextView representing date of the API response. */
    val date: TextView = rootView.findViewById(R.id.date)

    /** The TextView representing explanation of the API response. */
    val description: TextView = rootView.findViewById(R.id.description)

    /** The ImageView representing image of the API response when response is image data. */
    val imageView: ImageView = rootView.findViewById(R.id.imageView)

    /**
     * The ImageView representing playButton which will be shown in center of the placeholder image
     * when response is video data and video will start playing once user clicks on this view.
     */
    val playButton: ImageView = rootView.findViewById(R.id.playButton)

    /** The SwipeRefreshLayout which will be used to fetch the latest data from API. */
    val swipeRefreshLayout: SwipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout)

    /** The WebView which will play the video when response is video data. */
    val videoView: WebView = rootView.findViewById(R.id.videoView)
}