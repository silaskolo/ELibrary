package me.silaskolo.elibrary;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class FileActivity extends AppCompatActivity {

    public static final String FRAGMENT_PDF_RENDERER_BASIC = "pdf_renderer_basic";

    private String bookFileName;

    private String bookUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                bookFileName = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
                bookUrl = URLs.BOOK_PATH + bookFileName;

                if (savedInstanceState == null) {
                    Log.d("Here",bookUrl);

                    new DownloadFile().execute(bookUrl, bookFileName);


                    try{
                        //startActivity(pdfIntent);
                    }catch(ActivityNotFoundException e){
                        Toast.makeText(FileActivity.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... strings) {

            String fileUrl = strings[0];

            String fileName = strings[1];

            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "pdf");
            if (!folder.exists()){
                folder.mkdir();

            }

            Log.d("here",folder.toString());
            Log.d("here",fileName);

            File pdfFile = new File(folder, fileName);

            try{
                if (!pdfFile.exists()){
                    pdfFile.createNewFile();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            BitmapUtils.downloadFile(fileUrl, pdfFile);

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
         //   Toast.makeText(getApplicationContext(), "Download PDf successfully", Toast.LENGTH_SHORT).show();

            PdfRendererBasicFragment pdfRendererBasicFragment = new PdfRendererBasicFragment();

            Bundle args = new Bundle();
            args.putString("name", bookFileName);
            pdfRendererBasicFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, pdfRendererBasicFragment,
                            FRAGMENT_PDF_RENDERER_BASIC)
                    .commit();
            Log.d("Download complete", "----------");
        }
    }

}