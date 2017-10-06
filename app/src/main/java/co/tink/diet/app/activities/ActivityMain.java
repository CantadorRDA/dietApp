package co.tink.diet.app.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import co.tink.diet.app.R;
import co.tink.diet.app.fragments.FragmentGraph;
import co.tink.diet.app.helpers.Dialogs;

public class ActivityMain extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener
    , FragmentManager.OnBackStackChangedListener{

  FragmentManager fragmentManager;
  Handler handler;
  DatabaseReference fireDataBase;
  FirebaseStorage fireStorage;

  FragmentGraph fragmentGraph;

  Toolbar toolbar;
  DrawerLayout drawer;
  NavigationView navigationView;
  CoordinatorLayout mainCoordinator;
  FrameLayout contentFrame;
  ActionBarDrawerToggle toggle;


  String filename = "";
  String picturePath;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    fireDataBase = FirebaseDatabase.getInstance().getReference();
    fireStorage = FirebaseStorage.getInstance();
    initViews();
    drawer();
    toolbar();
    handler = new Handler();
  }

  private void initViews() {
    mainCoordinator = (CoordinatorLayout) findViewById(R.id.main_coordinator);
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    navigationView = (NavigationView) findViewById(R.id.nav_view);
    contentFrame = (FrameLayout) findViewById(R.id.content_frame);
  }

  private void drawer() {
    toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    navigationView.setNavigationItemSelectedListener(this);
  }

  private void toolbar() {
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @Override
  public void onBackPressed() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    if (fragmentManager.getBackStackEntryCount() > 0) {
      fragmentManager.popBackStack();
    } else {
      if (drawer.isDrawerOpen(GravityCompat.START)) {
        drawer.closeDrawer(GravityCompat.START);
      } else {
        Dialogs dialogs = new Dialogs(this);
        dialogs.ExitDialog();
      }
    }
  }

  @Override
  public void onBackStackChanged() {
    int stack_size = getSupportFragmentManager().getBackStackEntryCount();
    if (stack_size > 0) {
      toggle.setDrawerIndicatorEnabled(false); //disable "hamburger to arrow" drawable
      toggle.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp); //set your own
    } else {
      toggle.setDrawerIndicatorEnabled(true); //enable "hamburger to arrow" drawable
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == android.R.id.home) {
      fragmentManager = getSupportFragmentManager();
      if (fragmentManager.getBackStackEntryCount() > 0) {
        fragmentManager.popBackStack();
      } else {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
          drawer.closeDrawer(GravityCompat.START);
        } else {
          drawer.openDrawer(GravityCompat.START);
        }
      }

      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.nav_graph) {
      fragmentGraph = new FragmentGraph();
      show_fragment(getResources().getString(R.string.graph), fragmentGraph, false, "add");
    } else if (id == R.id.nav_fridge) {

    } else if (id == R.id.nav_history) {

    } else if (id == R.id.nav_articles) {

    } else if (id == R.id.nav_chat) {

    } else if (id == R.id.nav_exit) {
      Dialogs dialogs = new Dialogs(this);
      dialogs.ExitDialog();
    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  public void show_fragment(final String string, final Fragment fragment, final boolean add, final String tag) {

    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
//                Bundle data = new Bundle();
//                data.putStringArray("titles", titles);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//                fragment.setArguments(data);
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

        if (add) {
          transaction.replace(R.id.content_frame, fragment, tag).addToBackStack(null);
          toolbar.setTitle(string);
        } else {
          transaction.replace(R.id.content_frame, fragment, tag);
          toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
          toolbar.setTitle(string);
        }

        transaction.commit();
      }
    }, 300L);
  }

  public void snack(String message) {
    Snackbar snackbar = Snackbar.make(mainCoordinator, message, Snackbar.LENGTH_LONG).setAction("Action", null);
    View sbView = snackbar.getView();
    sbView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
    textView.setTextColor(getResources().getColor(android.R.color.white));
    snackbar.show();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
      Uri selectedImage = data.getData();
      String[] filePathColumn = {MediaStore.Images.Media.DATA};

      Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
      cursor.moveToFirst();

      int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
      picturePath = cursor.getString(columnIndex);
      cursor.close();

      filename = picturePath.substring(picturePath.lastIndexOf("/") + 1);

      File path = new File(Environment.getExternalStorageDirectory(), "temp/image_cache");
      if (!path.exists()) {
        path.mkdirs();
      }

      Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
      File imagePath = new File(path + "/" + filename);
      FileOutputStream fos;
      try {
        fos = new FileOutputStream(imagePath);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
      } catch (FileNotFoundException e) {

      } catch (IOException e) {

      }

      if (fragmentGraph != null) {
        fragmentGraph.upload(filename);
      }
    }

  }

  public DatabaseReference getFireBase() {
    return fireDataBase;
  }

  public FirebaseStorage getFirebaseStorage() {
    return fireStorage;
  }

  public String getToolbarTitle() {
    return toolbar.getTitle().toString();
  }

  public void setToolbarTitle(String title) {
    toolbar.setTitle(title);
  }

  public ActionBarDrawerToggle getActionBarDrawerToggle() {
    return toggle;
  }
}
