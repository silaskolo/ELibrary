package me.silaskolo.elibrary;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;

import me.silaskolo.elibrary.BookAdapter.BookAdapterOnClickHandler;

public class DashboardActivity extends AppCompatActivity implements BookAdapterOnClickHandler{

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecommendedRecyclerView;
    private RecyclerView mNewRecyclerView;
    private BookAdapter mRecommendedBookAdapter;
    private BookAdapter mNewBookAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

                /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecommendedRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_recommended);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        /*
         * LinearLayoutManager can support HORIZONTAL or VERTICAL orientations. The reverse layout
         * parameter is useful mostly for HORIZONTAL layouts that should reverse for right to left
         * languages.
         */
        LinearLayoutManager recommendedlayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mRecommendedRecyclerView.setLayoutManager(recommendedlayoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecommendedRecyclerView.setHasFixedSize(true);

        mRecommendedBookAdapter = new BookAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecommendedRecyclerView.setAdapter(mRecommendedBookAdapter);

                  /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mNewRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_new);


        /*
         * LinearLayoutManager can support HORIZONTAL or VERTICAL orientations. The reverse layout
         * parameter is useful mostly for HORIZONTAL layouts that should reverse for right to left
         * languages.
         */
        LinearLayoutManager newLayoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mNewRecyclerView.setLayoutManager(newLayoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mNewRecyclerView.setHasFixedSize(true);

        mNewBookAdapter = new BookAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mNewRecyclerView.setAdapter(mNewBookAdapter);
        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
         * circle. We didn't make the rules (or the names of Views), we just follow them.
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        /* Once all of our views are setup, we can load the weather data. */
        loadBookData();
    }
    /**
     * This method will get the user's preferred location for weather, and then tell some
     * background method to get the weather data in the background.
     */
    private void loadBookData() {
        showBookDataView();

        new FetchBookTask(URLs.URL_BOOK_RECOMMENDED,mRecommendedBookAdapter).execute();
        new FetchBookTask(URLs.URL_BOOK_NEW,mNewBookAdapter).execute();
    }

    private void showBookDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.GONE);
        /* Then, make sure the weather data is visible */
        mRecommendedRecyclerView.setVisibility(View.VISIBLE);
        mNewRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecommendedRecyclerView.setVisibility(View.INVISIBLE);
        mNewRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchBookTask extends AsyncTask<Void, Void, String> {

        String bookUrl;
        BookAdapter bookAdapter;

        public FetchBookTask(String bookUrl,BookAdapter bookAdapter) {
            this.bookUrl = bookUrl;
            this.bookAdapter = bookAdapter;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {


            try {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();

                //returing the response
                return requestHandler.sendPostRequest(bookUrl, params);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String bookData) {

            String[][] parsedBookData = null;

            mLoadingIndicator.setVisibility(View.INVISIBLE);

            try {
                //converting response to json object
                JSONObject obj = new JSONObject(bookData);
                //if no error in response
                if (!obj.getBoolean("error")) {

                    //getting the user from the response
                    JSONArray booksJson = obj.getJSONArray("books");

                    parsedBookData = new String[booksJson.length()][2];

                    for (int i = 0; i < booksJson.length(); i++){

                        JSONObject currentBook = booksJson.getJSONObject(i);
                        parsedBookData[i][0] = currentBook.getString("bookID");
                        parsedBookData[i][1] = currentBook.getString("bookCover");

                    }

                    bookAdapter.setBookData(parsedBookData);

                    //starting the profile activity
                    // finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Unable to Load Books ATM", Toast.LENGTH_SHORT).show();
                    showErrorMessage();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }



    public void onClickOpenDashboardActivity(View v) {
        Intent dashboardActivityIntent = new Intent(DashboardActivity.this,DashboardActivity.class);
        startActivity(dashboardActivityIntent);
    }

    public void onClickOpenBrowseActivity(View v) {
        Intent browseActivityIntent = new Intent(DashboardActivity.this,BrowseActivity.class);
        startActivity(browseActivityIntent);
    }

    public void onClickOpenSearchActivity(View v) {
        Intent searchActivityIntent = new Intent(DashboardActivity.this,SearchActivity.class);
        startActivity(searchActivityIntent);
    }

    public void onClickOpenLibraryActivity(View v) {
        Intent libraryActivityIntent = new Intent(DashboardActivity.this,LibraryActivity.class);
        startActivity(libraryActivityIntent);
    }

    @Override
    public void onClick(String bookID) {
        Context context = this;
        Class destinationClass = PreviewActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, bookID);
        startActivity(intentToStartDetailActivity);
    }
}
