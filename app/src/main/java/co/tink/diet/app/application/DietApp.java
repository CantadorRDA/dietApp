package co.tink.diet.app.application;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Cantador on 31.07.17.
 */

public class DietApp extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);

  }
}