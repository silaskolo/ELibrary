package me.silaskolo.elibrary;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;


public class PreviewActivity extends AppCompatActivity{

    private static final String TAG = MainActivity.class.getSimpleName();

    private String mBookID;

    private String mBookUrl;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;


    private TextView mBookName;
    private TextView mBookAuthor;
    private TextView mBookCategory;
    private ImageView mBookCover;
    private String bookFileName;

    private Switch mbookPref;

    User currentUser;

    int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        ActivityCompat.requestPermissions(PreviewActivity.this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                100);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                mBookID = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
                mBookUrl = URLs.URL_BOOK_BY_ID + mBookID;
            }
        }

        currentUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        user_id = currentUser.getId();

        /* Once all of our views are setup, we can load the weather data. */
        loadBookData();

        mbookPref = (Switch) findViewById(R.id.book_pref);
        mbookPref.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String mAction;
                if (isChecked) {
                    mAction = "add";
                }else{
                    mAction = "remove";
                }
                (new manageBookPref(mAction)).execute();
            }
        });
    }
    /**
     * This method will get the user's preferred location for weather, and then tell some
     * background method to get the weather data in the background.
     */
    private void loadBookData() {
        showBookDataView();

        (new FetchBookTask(mBookUrl)).execute();
    }

    private void showBookDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.GONE);
        /* Then, make sure the weather data is visible */
        //        mRecommendedRecyclerView.setVisibility(View.VISIBLE);


    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
//        mRecommendedRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void populateBookData(String bookName,String bookAuthor,String bookCategory,String bookCover,String bookFile){
        mBookName = (TextView) findViewById(R.id.tv_book_name);
        mBookAuthor = (TextView) findViewById(R.id.tv_book_author);
        mBookCategory = (TextView) findViewById(R.id.tv_book_category);
        mBookCover = (ImageView) findViewById(R.id.iv_book_image);

        String bookCoverUrl = URLs.BOOK_PATH + bookCover;

        mBookName.setText(bookName);
        mBookAuthor.setText(bookAuthor);
        mBookCategory.setText(bookCategory);
        new DownloadImageTask(mBookCover).execute(bookCoverUrl);
        bookFileName = bookFile;

    }

    public class FetchBookTask extends AsyncTask<Void, Void, String> {

        String bookUrl;

        public FetchBookTask(String bookUrl) {
            this.bookUrl = bookUrl;
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

            String[] parsedBookData = null;

            mLoadingIndicator.setVisibility(View.INVISIBLE);

            try {
                //converting response to json object
                JSONObject obj = new JSONObject(bookData);
                //if no error in response
                if (!obj.getBoolean("error")) {

                    //getting the user from the response
                    JSONObject booksJson = obj.getJSONObject("book");


                    String bookName = booksJson.getString("bookName");
                    String bookAuthor = booksJson.getString("authorName");
                    String bookCategory = booksJson.getString("categoryName");
                    String bookCover = booksJson.getString("bookCover");
                    String bookFile = booksJson.getString("bookFileLocation");




                    populateBookData(bookName,bookAuthor,bookCategory,bookCover,bookFile);

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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String bookCoverUrl = urls[0];
            Bitmap bookBitmap = null;
            try {
                bookBitmap = BitmapUtils.loadBitmap(bookCoverUrl);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bookBitmap;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public class manageBookPref extends AsyncTask<Void, Void, String> {

        String action;

        public manageBookPref(String action) {
            this.action = action;
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
                params.put("user_id", Integer.toString(user_id));
                params.put("book_id", mBookID);
                //returing the response
                String mUrl = URLs.URL_PREFERENCE_BOOK + action;


                return requestHandler.sendPostRequest(mUrl, params);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String prefData) {


            mLoadingIndicator.setVisibility(View.INVISIBLE);

            try {
                //converting response to json object
                JSONObject obj = new JSONObject(prefData);

                //if no error in response
                if (!obj.getBoolean("error")) {
                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void onClickOpenDashboardActivity(View v) {
        Intent dashboardActivityIntent = new Intent(PreviewActivity.this,DashboardActivity.class);
        startActivity(dashboardActivityIntent);
    }

    public void onClickOpenBrowseActivity(View v) {
        Intent browseActivityIntent = new Intent(PreviewActivity.this,BrowseActivity.class);
        startActivity(browseActivityIntent);
    }

    public void onClickOpenSearchActivity(View v) {
        Intent searchActivityIntent = new Intent(PreviewActivity.this,SearchActivity.class);
        startActivity(searchActivityIntent);
    }

    public void onClickOpenLibraryActivity(View v) {
        Intent libraryActivityIntent = new Intent(PreviewActivity.this,LibraryActivity.class);
        startActivity(libraryActivityIntent);
    }

    public void onClickViewBookActivity(View v) {
        Context context = this;
        Class destinationClass = FileActivity.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, bookFileName);
        startActivity(intentToStartDetailActivity);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0
                        || grantResults[0] == PackageManager.PERMISSION_GRANTED
                        || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    /* User checks permission. */

                } else {
                    Toast.makeText(PreviewActivity.this, "Permission is denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                //return; // delete.
        }
    }
}
