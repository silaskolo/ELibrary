package me.silaskolo.elibrary;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class BooksActivity extends AppCompatActivity implements BookAdapter.BookAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private BookAdapter mBookAdapter;



    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    User currentUser;

    int user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

                /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_personal_books);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        /*
         * LinearLayoutManager can support HORIZONTAL or VERTICAL orientations. The reverse layout
         * parameter is useful mostly for HORIZONTAL layouts that should reverse for right to left
         * languages.
         */
        GridLayoutManager layoutManager
                = new GridLayoutManager(this, 3,GridLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        mBookAdapter = new BookAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mBookAdapter);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
         * circle. We didn't make the rules (or the names of Views), we just follow them.
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        currentUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        user_id = currentUser.getId();

        /* Once all of our views are setup, we can load the weather data. */
        loadBookData();
    }
    /**
     * This method will get the user's preferred location for weather, and then tell some
     * background method to get the weather data in the background.
     */
    private void loadBookData() {
        showBookDataView();

        new FetchBookTask().execute();
    }

    private void showBookDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.GONE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    public class FetchBookTask extends AsyncTask<Void, Void, String> {


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
                params.put("user_id", Integer.toString(user_id));

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_BOOK_PERSONAL, params);

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
                Log.d(TAG,obj.toString());
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

                    mBookAdapter.setBookData(parsedBookData);

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
        Intent dashboardActivityIntent = new Intent(BooksActivity.this,DashboardActivity.class);
        startActivity(dashboardActivityIntent);
    }

    public void onClickOpenBrowseActivity(View v) {
        Intent browseActivityIntent = new Intent(BooksActivity.this,BrowseActivity.class);
        startActivity(browseActivityIntent);
    }

    public void onClickOpenSearchActivity(View v) {
        Intent searchActivityIntent = new Intent(BooksActivity.this,SearchActivity.class);
        startActivity(searchActivityIntent);
    }

    public void onClickOpenLibraryActivity(View v) {
        Intent libraryActivityIntent = new Intent(BooksActivity.this,LibraryActivity.class);
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
