package me.silaskolo.elibrary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    TextView textViewEmail;
    EditText editTextFname, editTextLname, editTextPassword, editTextMatricNumber;
    LinearLayout profileLayout;
    Spinner spinnerDepartment, spinnerLevel;
    ArrayAdapter<CharSequence> departmentAdapter;
    ArrayAdapter<CharSequence> LevelAdapter;
    int user_id;
    String email;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //if the user is already logged in we will directly start the profile activity

        editTextFname = (EditText) findViewById(R.id.editTextFName);
        editTextLname = (EditText) findViewById(R.id.editTextLName);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextMatricNumber = (EditText) findViewById(R.id.editTextMatricNumber);

        spinnerDepartment = (Spinner) findViewById(R.id.spinnerDepartment);
        spinnerLevel = (Spinner) findViewById(R.id.spinnerLevel);

        profileLayout = (LinearLayout) findViewById(R.id.profileLayout);

        currentUser = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        user_id = currentUser.getId();
        email = currentUser.getEmail();
        getUser();

        departmentAdapter = ArrayAdapter.createFromResource(this, R.array.department_array, android.R.layout.simple_spinner_item); // Create an ArrayAdapter using the string array and a default spinner layout
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Specify the layout to use when the list of choices appears
        spinnerDepartment.setAdapter(departmentAdapter); // Apply the adapter to the spinner

        LevelAdapter = ArrayAdapter.createFromResource(this, R.array.level_array, android.R.layout.simple_spinner_item); // Create an ArrayAdapter using the string array and a default spinner layout
        LevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Specify the layout to use when the list of choices appears
        spinnerLevel.setAdapter(LevelAdapter); // Apply the adapter to the spinner

        findViewById(R.id.buttonUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if user pressed on button register
                //here we will register the user to server
                updateUser();
            }
        });

        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browseActivityIntent = new Intent(ProfileActivity.this, LibraryActivity.class);
                startActivity(browseActivityIntent);
            }
        });


    }

    private void getUser() {
        class GetUser extends AsyncTask<Void, Void, String> {

            ProgressBar progressBar;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
                profileLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);
                profileLayout.setVisibility(View.VISIBLE);

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        //getting the user from the response
                        JSONObject userJson = obj.getJSONObject("user");

                        editTextFname.setText(userJson.getString("userFname"));
                        editTextLname.setText(userJson.getString("userLname"));
                        editTextMatricNumber.setText(userJson.getString("userMatricNo"));
                        textViewEmail.setText(userJson.getString("userEmail"));
                        spinnerDepartment.setSelection(departmentAdapter.getPosition(userJson.getString("departmentID")));
                        spinnerLevel.setSelection(departmentAdapter.getPosition(userJson.getString("userLevel")));


                    } else {
                        Toast.makeText(getApplicationContext(), "Unable to Retrieve User", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", Integer.toString(user_id));

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_PROFILE, params);
            }
        }

        GetUser gu = new GetUser();
        gu.execute();
    }

    private void updateUser() {
        final String password = editTextPassword.getText().toString().trim();
        final String first_name = editTextFname.getText().toString().trim();
        final String last_name = editTextLname.getText().toString().trim();
        final String matric_no = editTextMatricNumber.getText().toString().trim();

        final String department = spinnerDepartment.getSelectedItem().toString();
        final String level = spinnerLevel.getSelectedItem().toString();

        //first we will do the validations

        if (TextUtils.isEmpty(first_name)) {
            editTextFname.setError("Enter a First Name");
            editTextFname.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(last_name)) {
            editTextLname.setError("Please enter your Last Name");
            editTextLname.requestFocus();
            return;
        }


        if (TextUtils.isEmpty(matric_no)) {
            editTextMatricNumber.setError("Please enter your Matric Number");
            editTextMatricNumber.requestFocus();
            return;
        }



        //if it passes all the validations

        class UpdateUser extends AsyncTask<Void, Void, String> {

            private ProgressBar progressBar;

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", Integer.toString(user_id));
                params.put("email", email);
                params.put("first_name", first_name);
                params.put("last_name", last_name);
                params.put("matric_no", matric_no);
                params.put("department", department);
                params.put("level", level);
                params.put("password", password);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_UPDATE, params);
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
                super.onPostExecute(s);
                Log.d("myTag", s);
                //hiding the progressbar after completion
                progressBar.setVisibility(View.GONE);

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    Log.d("Here",obj.toString());
                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        //executing the async task
        UpdateUser uUser = new UpdateUser();
        uUser.execute();
    }

}