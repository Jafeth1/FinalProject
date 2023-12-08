package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String title, explanation, dateText, url, hdurl;
    private Bitmap pic;
    private static String datePicked;
    public static final String JSON_URL = "https://api.nasa.gov/planetary/apod?api_key=CHqd1uSvGVMw6ftr0I6Mr9p7Vh8d1og75ZUwBFQe";
    private ProgressBar progressBar;
    private SQLiteDatabase db;
    private static TextView dateSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        progressBar = findViewById(R.id.mainProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String today = formatter.format(new Date());

        new NASAImageQuery().execute(JSON_URL);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        Button dateButton = findViewById(R.id.mainDatePickerButton);
        dateButton.setOnClickListener(v -> showDatePickerDialog());


        Button favouriteButton = findViewById(R.id.mainFavouriteThisImage);
        favouriteButton.setOnClickListener(v -> saveToFavorites());
    }

    private void saveToFavorites() {
        String toastResponse;
        String filename = title + ".png";
        if (!fileExistance(filename)) {
            saveNASAimage(title, url);
            toastResponse = getString(R.string.imageSaved);
        } else {
            toastResponse = getString(R.string.imageExists);
        }
        Cursor results = db.rawQuery("SELECT * FROM " + DBHelper.TABLE_NAME + " WHERE DATE = ?", new String[] {dateText});
        if(results.getCount() == 0){
            ContentValues newrow = new ContentValues();
            newrow.put(DBHelper.COL_TITLE, title);
            newrow.put(DBHelper.COL_DATE, dateText);
            newrow.put(DBHelper.COL_FILENAME, filename);
            newrow.put(DBHelper.COL_HDURL, hdurl);
            newrow.put(DBHelper.COL_EXPLANATION, explanation);
            db.insert(DBHelper.TABLE_NAME, null, newrow);
        }
        Toast.makeText(MainActivity.this, toastResponse, Toast.LENGTH_SHORT).show();
    }

    private void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.navMenuHome) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.navMenuFavourites) {
            startActivity(new Intent(this, FavouriteImage.class));
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.help_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.helpbtn) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setNegativeButton(R.string.close, null)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean fileExistance(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();
    }

    private void saveNASAimage(String title, String url) {
        try {
            URL imageURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                Bitmap image = BitmapFactory.decodeStream(connection.getInputStream());
                FileOutputStream outputStream = openFileOutput(title + ".png", Context.MODE_PRIVATE);
                image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void onDateSet(int year, int month, int day) {
        month++; // Month is 0-based
        datePicked = year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
        dateSelected.setText(datePicked);
        String jsonTemp = JSON_URL + "&date=" + datePicked;
        new NASAImageQuery().execute(jsonTemp);
    }
    public static class DatePickerFragment extends DialogFragment {

        public interface OnDateSetListener {
            void onDateSet(int year, int month, int day);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), (view, year1, monthOfYear, dayOfMonth) -> {
                OnDateSetListener listener = (OnDateSetListener) getActivity();
                if (listener != null) {
                    listener.onDateSet(year1, monthOfYear, dayOfMonth);
                }
            }, year, month, day);
        }
    }


    public class NASAImageQuery extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... args) {
            try {
                URL url = new URL(args[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject jsonObject = new JSONObject(result.toString());
                title = jsonObject.getString("title");
                dateText = jsonObject.getString("date");
                explanation = jsonObject.getString("explanation");
                MainActivity.this.url = jsonObject.getString("url");  // Updated to use MainActivity.this.url
                hdurl = jsonObject.optString("hdurl");  // Use optString for optional fields
                pic = downloadNASAimage(MainActivity.this.url);  // Pass the URL as a string
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return "Executed";
        }

        private Bitmap downloadNASAimage(String link) {
            try {
                URL imageURL = new URL(link);
                HttpURLConnection connection = (HttpURLConnection) imageURL.openConnection();
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    return BitmapFactory.decodeStream(connection.getInputStream());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            ImageView imageView = findViewById(R.id.mainImageView);
            if (pic != null) {
                imageView.setImageBitmap(pic);
            }
            progressBar.setVisibility(View.GONE);  // Hide the progress bar after loading the image

            // Update other UI components as needed
            TextView titleView = findViewById(R.id.mainTitleContent);
            TextView explanationView = findViewById(R.id.mainExplanationContent);
            TextView dateView = findViewById(R.id.mainDateContent);
            TextView hdurlView = findViewById(R.id.mainHDURLContent);

            titleView.setText(title);
            explanationView.setText(explanation);
            dateView.setText(dateText);
            hdurlView.setText(hdurl);
        }
    }

    }

