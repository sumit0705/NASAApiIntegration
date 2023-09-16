# NASAApiIntegration
This is an API Integration task in which I'm fetching NASA API https://api.nasa.gov/ which is APOD(Astronomy picture of the day) result via GET request using Volley library and showing/manipulating the UI.

The code contains: 
1. Fetching the daily image data from NASA's API: https://api.nasa.gov after generating api_key, API request will look like: https://api.nasa.gov/planetary/apod?api_key=R2Pji4odd8yy9iQKsH0piBGh5xOtfZcSYKTyeIk8.
2. Displaying the image using an ImageView and showing image title, date, and description using TextView.
3. Loading latest image from Api on user refresh which will be performed via pull-down to refresh gesture via SwipeRefreshLayout or first time app launch.
4. If Api response contains video content then we will show the video in WebView through which we can play the video while keeping the rest of the TextViews.
5. Also handled errors while fetching data from Api using Volley.
6. Handled video content with placeholder image and play button.
7. Implemented Volley caching behaviour for fetched data and used SharedPreference for storing the latest data in memory through which we will update the UI on consecutive app launch.
8. If user wants to fetch the latest data then they need to perform pull-down to refresh gesture via SwipeRefreshLayout.
9. Used MVVM architecture along with repository and several other classes for better separation of concerns.


API URL:

1. For latest data (image/video response)
        const val API_URL =
            "https://api.nasa.gov/planetary/apod?api_key=R2Pji4odd8yy9iQKsH0piBGh5xOtfZcSYKTyeIk8"

2. For image data.
      const val API_URL = "https://api.nasa.gov/planetary/apod?api_key=R2Pji4odd8yy9iQKsH0piBGh5xOtfZcSYKTyeIk8&date=2023-09-16&thumbs=true"

3. For video data.
          const val API_URL = "https://api.nasa.gov/planetary/apod?api_key=R2Pji4odd8yy9iQKsH0piBGh5xOtfZcSYKTyeIk8&date=2023-09-06&thumbs=true"
                                                             OR
          const val API_URL = "https://api.nasa.gov/planetary/apod?api_key=R2Pji4odd8yy9iQKsH0piBGh5xOtfZcSYKTyeIk8&date=2023-06-17&thumbs=true"


I have also added these values in MainViewModel (keeping commented) where we can play with alternate API_URL and check the UI on response.

Steps:
1. Open the code in Android Studio.
2. Open the Device Manager and Launch the AVD(Android Virtual device) or create a new device if not present (recommended Android API 31 i.e. Android 12)
3. Select the url as per your preference from MainViewModel.
4. Build and run the app.

