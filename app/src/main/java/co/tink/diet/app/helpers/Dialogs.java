package co.tink.diet.app.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;

import co.tink.diet.app.R;


public class Dialogs {

  AlertDialog.Builder builder;
  AlertDialog alert;
  LayoutInflater inflater;
  Context context;

  public Dialogs(Context context) {
    this.context = context;
  }

  public void ExitDialog() {
    builder = new AlertDialog.Builder(context, R.style.DialogWide)
        .setTitle(context.getString(R.string.exit))
        .setMessage(context.getString(R.string.exit_message))
        .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id1) {
            ((Activity) context).finish();
          }
        })
        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id1) {
                dialog.cancel();
              }
            }
        );

    alert = builder.create();

    alert.setCancelable(true);
    alert.show();
  }

}
