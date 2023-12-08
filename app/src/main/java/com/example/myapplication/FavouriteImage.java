package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;

public class FavouriteImage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ListView listView;
    private ArrayList<NASAimage> imageList;
    private SQLiteDatabase database;
    private ImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        setupToolbarAndDrawer();
        setupListView();
        initializeDatabaseAndLoadData();
    }

    private void setupToolbarAndDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupListView() {
        listView = findViewById(R.id.favouritesListview);
        imageList = new ArrayList<>();
        adapter = new ImageAdapter();
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> showDetailFragment(position));
        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            confirmDeletion(position);
            return true; // Indicating the long click was handled
        });
    }

    private void initializeDatabaseAndLoadData() {
        DBHelper dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
        loadImagesFromDatabase();
    }

    private void loadImagesFromDatabase() {
        String[] columns = {DBHelper.COL_ID, DBHelper.COL_TITLE, DBHelper.COL_DATE, DBHelper.COL_FILENAME, DBHelper.COL_EXPLANATION, DBHelper.COL_HDURL};
        Cursor cursor = database.query(DBHelper.TABLE_NAME, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            imageList.add(extractImageFromCursor(cursor));
        }
        cursor.close();
    }

   @SuppressLint("Range")
    private NASAimage extractImageFromCursor(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(DBHelper.COL_ID));
         String title = cursor.getString(cursor.getColumnIndex(DBHelper.COL_TITLE));
        String dateText = cursor.getString(cursor.getColumnIndex(DBHelper.COL_DATE));
        String filename = cursor.getString(cursor.getColumnIndex(DBHelper.COL_FILENAME));
        String explanation = cursor.getString(cursor.getColumnIndex(DBHelper.COL_EXPLANATION));
        String hdurl = cursor.getString(cursor.getColumnIndex(DBHelper.COL_HDURL));
        return new NASAimage(id, title, dateText, filename, explanation, hdurl);
    }

    private void showDetailFragment(int position) {
        Bundle dataToPass = new Bundle();
        NASAimage selectedImage = imageList.get(position);
        dataToPass.putString("TITLE", selectedImage.getTitle());
        dataToPass.putString("DATE", selectedImage.getDate());
        dataToPass.putString("EXPLANATION", selectedImage.getExplanation());
        dataToPass.putString("HDURL", selectedImage.getHDurl());
        dataToPass.putString("FILEPATH", getFileStreamPath(selectedImage.getFilename()).getPath());

        FavouriteImageFragment fragment = new FavouriteImageFragment();
        fragment.setArguments(dataToPass);
        getSupportFragmentManager().beginTransaction().replace(R.id.favFragFrame, fragment).commit();
    }

    private void confirmDeletion(int position) {
        NASAimage imageToDelete = imageList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.likeToDelete)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> deleteImage(position))
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private void deleteImage(int position) {
        NASAimage imageToDelete = imageList.get(position);
        database.delete(DBHelper.TABLE_NAME, "_id=?", new String[]{String.valueOf(imageToDelete.getId())});
        imageList.remove(position);
        deleteLocalFile(imageToDelete.getFilename());
        adapter.notifyDataSetChanged();
    }

    private void deleteLocalFile(String filename) {
        File file = getFileStreamPath(filename);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.navMenuHome) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (itemId == R.id.navMenuFavourites) {
            startActivity(new Intent(this, FavouriteImage.class));
        }
        ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.helpbtn) {
            new AlertDialog.Builder(this)
                    .setNegativeButton(R.string.close, null)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    private class ImageAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public NASAimage getItem(int position) {
            return imageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.favourite_row, parent, false);
            }
            NASAimage nasaImage = getItem(position);
            ((ImageView) view.findViewById(R.id.favRowImage)).setImageBitmap(BitmapFactory.decodeFile(getFileStreamPath(nasaImage.getFilename()).getPath()));
            ((TextView) view.findViewById(R.id.favRowTitle)).setText(nasaImage.getTitle());
            ((TextView) view.findViewById(R.id.favRowDate)).setText(nasaImage.getDate());
            return view;
        }
    }
}
