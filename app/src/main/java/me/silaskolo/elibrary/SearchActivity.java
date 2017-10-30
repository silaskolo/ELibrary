package me.silaskolo.elibrary;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "elibrary";
    EditText editTextSearch;
    Spinner spinnerGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        editTextSearch = (EditText) findViewById(R.id.editTextSearch);
        spinnerGroup = (Spinner) findViewById(R.id.spinnerGroup);

        ArrayAdapter<CharSequence> groupAdapter = ArrayAdapter.createFromResource(this, R.array.group_array, android.R.layout.simple_spinner_item); // Create an ArrayAdapter using the string array and a default spinner layout
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Specify the layout to use when the list of choices appears
        spinnerGroup.setAdapter(groupAdapter); // Apply the adapter to the spinner

        findViewById(R.id.buttonSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if user pressed on button register
                //here we will register the user to server
                runSearch();
            }
        });
    }

    private void runSearch() {
        final String searchText = editTextSearch.getText().toString().trim();
        final String searchGroup = spinnerGroup.getSelectedItem().toString();

        //first we will do the validations

        if (TextUtils.isEmpty(searchText)) {
            editTextSearch.setError("Enter a Search Parameter");
            editTextSearch.requestFocus();
            return;
        }


        //if it passes all the validations

        class RunSearch extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();

                String mUrl;

                switch (searchGroup.toLowerCase()) {
                    case "author":
                        mUrl = URLs.URL_AUTHOR_SEARCH;
                        break;
                    case "book":
                        mUrl = URLs.URL_BOOK_SEARCH;
                        break;
                    case "category":
                        mUrl = URLs.URL_CATEGORY_SEARCH;
                        break;
                    default:
                        mUrl = "";
                        break;
                }

                try {
                    mUrl += URLEncoder.encode(searchText, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                //returing the response
                return requestHandler.sendPostRequest(mUrl, params);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //displaying the progress bar while user registers on the server
                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {

                String[][] parsedBookData = null;
                super.onPostExecute(s);

                //hiding the progressbar after completion
                progressBar.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONArray booksJson = obj.getJSONArray("books");
                        parsedBookData = new String[booksJson.length()][2];

                        for (int i = 0; i < booksJson.length(); i++) {

                            JSONObject currentBook = booksJson.getJSONObject(i);
                            parsedBookData[i][0] = currentBook.getString("bookID");
                            parsedBookData[i][1] = currentBook.getString("bookCover");

                        }


                        Context context = SearchActivity.this;
                        Class destinationClass = SearchResultsActivity.class;
                        Intent intentToStartDetailActivity = new Intent(context, destinationClass);

                        Bundle booksBundle = new Bundle();
                        booksBundle.putSerializable("list", parsedBookData);
                        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, booksBundle);
                        intentToStartDetailActivity.putExtra("searchText", searchText);
                        startActivity(intentToStartDetailActivity);
                        //starting the profile activity
//                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //executing the async task
        RunSearch rs = new RunSearch();
        rs.execute();
    }

    public void onClickOpenDashboardActivity(View v) {
        Intent dashboardActivityIntent = new Intent(SearchActivity.this, DashboardActivity.class);
        startActivity(dashboardActivityIntent);
    }

    public void onClickOpenBrowseActivity(View v) {
        Intent browseActivityIntent = new Intent(SearchActivity.this, BrowseActivity.class);
        startActivity(browseActivityIntent);
    }

    public void onClickOpenSearchActivity(View v) {
        Intent searchActivityIntent = new Intent(SearchActivity.this, SearchActivity.class);
        startActivity(searchActivityIntent);
    }

    public void onClickOpenLibraryActivity(View v) {
        Intent libraryActivityIntent = new Intent(SearchActivity.this, LibraryActivity.class);
        startActivity(libraryActivityIntent);
    }
}
