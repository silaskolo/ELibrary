package me.silaskolo.elibrary;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class BrowseActivity extends AppCompatActivity implements CategoryAdapter.CategoryAdapterOnClickHandler{

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private CategoryAdapter mCategoryAdapter;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

                   /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_category);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        /*
         * LinearLayoutManager can support HORIZONTAL or VERTICAL orientations. The reverse layout
         * parameter is useful mostly for HORIZONTAL layouts that should reverse for right to left
         * languages.
         */
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        mCategoryAdapter = new CategoryAdapter(this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mCategoryAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        loadCategoryData();
    }

    /**
     * This method will get the user's preferred location for weather, and then tell some
     * background method to get the weather data in the background.
     */
    private void loadCategoryData() {
        showBookDataView();

        new FetchCategoryTask().execute();
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

    public class FetchCategoryTask extends AsyncTask<Void, Void, String> {

        final String CATEGORY_URL = URLs.URL_CATEGORY_ALL;

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
                return requestHandler.sendPostRequest(CATEGORY_URL, params);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String categoryData) {

            String[][] parsedCategoryData = null;

            mLoadingIndicator.setVisibility(View.INVISIBLE);

            try {
                //converting response to json object
                JSONObject obj = new JSONObject(categoryData);
                //if no error in response
                if (!obj.getBoolean("error")) {

                    Log.d(TAG,obj.toString());

                    //getting the user from the response
                    JSONArray booksJson = obj.getJSONArray("category");


                    parsedCategoryData = new String[booksJson.length()][2];

                    for (int i = 0; i < booksJson.length(); i++){

                        JSONObject currentBook = booksJson.getJSONObject(i);
                        parsedCategoryData[i][0] = currentBook.getString("categoryID");
                        parsedCategoryData[i][1] = currentBook.getString("categoryName");

                    }

                    Log.d(TAG,parsedCategoryData.toString());
                    mCategoryAdapter.setCategoryData(parsedCategoryData);

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
        Intent dashboardActivityIntent = new Intent(BrowseActivity.this,DashboardActivity.class);
        startActivity(dashboardActivityIntent);
    }

    public void onClickOpenBrowseActivity(View v) {
        Intent browseActivityIntent = new Intent(BrowseActivity.this,BrowseActivity.class);
        startActivity(browseActivityIntent);
    }

    public void onClickOpenSearchActivity(View v) {
        Intent searchActivityIntent = new Intent(BrowseActivity.this,SearchActivity.class);
        startActivity(searchActivityIntent);
    }

    public void onClickOpenLibraryActivity(View v) {
        Intent libraryActivityIntent = new Intent(BrowseActivity.this,LibraryActivity.class);
        startActivity(libraryActivityIntent);
    }

    @Override
    public void onClick(String[] category) {
        Context context = this;
        Class destinationClass = BrowseBooksActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, category);
        startActivity(intentToStartDetailActivity);
    }
}
