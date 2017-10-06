package co.tink.diet.app.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.tink.diet.app.R;
import co.tink.diet.app.activities.ActivityMain;
import co.tink.diet.app.adapters.AdapterPhotoStripe;
import co.tink.diet.app.view.CustomMarkerView;

/**
 * Created by Cantador on 31.07.17.
 */

public class FragmentGraph extends android.support.v4.app.Fragment
    implements View.OnClickListener
    , OnChartValueSelectedListener {

  private LineChart chart;
  Button
      day_button,
      week_button,
      month_button;

  RecyclerView photoRecycler;

  AdapterPhotoStripe adapterPhotoStripe;

  List<String> uriList;
  String uploaded_image_url = "";

  public FragmentGraph() {
  }

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_graph, container, false);
    initViews(rootView);
//    firebase();
    buttons();
    photoStripe();
    return rootView;
  }

  private void initViews(View rootView) {
    chart = (LineChart) rootView.findViewById(R.id.chart);
    day_button = (Button) rootView.findViewById(R.id.day_button);
    week_button = (Button) rootView.findViewById(R.id.week_button);
    month_button = (Button) rootView.findViewById(R.id.month_button);
    photoRecycler = (RecyclerView) rootView.findViewById(R.id.photo_recycler);
  }

  @Override
  public void onStart() {
    super.onStart();
    getView().post(new Runnable() {
      @Override
      public void run() {
        drawGradientChart();
      }
    });
  }

  private void drawGradientChart() {
    chart();
    chartMarker();
    chart.setLayerType(View.LAYER_TYPE_HARDWARE, null);

//    Paint paintShadow = chart.getRenderer().getPaintRender();
//    paintShadow.setShadowLayer(10, 0, 5, Color.BLACK);


    Paint paint = chart.getRenderer().getPaintRender();
    int height = chart.getHeight();

    LinearGradient linGrad = new LinearGradient(0, 0, 0, height,
        getResources().getColor(R.color.pink),
        getResources().getColor(R.color.colorAccent),
        Shader.TileMode.REPEAT);
    paint.setShader(linGrad);

    chart();

    // Don't forget to refresh the drawing
    chart.invalidate();
  }

  private void chart() {
    chart.setPinchZoom(true);
    chart.getLegend().setEnabled(false);
    chart.getDescription().setEnabled(false);
    chart.setViewPortOffsets(0, 0, 0, 40);
    chart.setOnChartValueSelectedListener(this);
    chart.setTouchEnabled(true);

    XAxis xAxis = chart.getXAxis();
    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    xAxis.setTextSize(10f);
    xAxis.setTextColor(getActivity().getResources().getColor(R.color.primary));
    xAxis.setDrawAxisLine(true);
    xAxis.setDrawGridLines(false);

    YAxis left = chart.getAxisLeft();
    YAxis right = chart.getAxisRight();
    left.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
    right.setDrawLabels(false);
    left.setDrawAxisLine(false);
    right.setDrawAxisLine(false);
    left.setTextColor(getActivity().getResources().getColor(R.color.primary));

    ArrayList<Entry> values = new ArrayList<Entry>();

    for (int i = 0; i < 100; i++) {
      float val = (float) (Math.random() * 100) + 3;
      values.add(new Entry(i, val));
    }

    LineDataSet set1;

    if (chart.getData() != null &&
        chart.getData().getDataSetCount() > 0) {
      set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
      set1.setValues(values);
      chart.getData().notifyDataChanged();
      chart.notifyDataSetChanged();
    } else {
      // create a dataset and give it a type
      set1 = new LineDataSet(values, "DataSet 1");

      set1.setDrawIcons(false);
      set1.setDrawValues(false);
      set1.setMode(set1.getMode() == LineDataSet.Mode.CUBIC_BEZIER
          ? LineDataSet.Mode.LINEAR
          : LineDataSet.Mode.CUBIC_BEZIER);

      set1.setColor(Color.BLACK);
      set1.setCircleColor(Color.BLACK);
      set1.setLineWidth(3f);
      set1.setCircleRadius(3f);
      set1.setDrawCircleHole(false);
      set1.setDrawCircles(false);
      set1.setValueTextSize(16f);
      set1.setDrawFilled(true);
      set1.setFormLineWidth(1f);
      set1.setFormSize(15.f);
      set1.setDrawHorizontalHighlightIndicator(false);
      set1.setDrawVerticalHighlightIndicator(false);

      // fill drawable only supported on api level 18 and above
      Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.fade_black);
      set1.setFillDrawable(drawable);


      ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
      dataSets.add(set1); // add the datasets

      // create a data object with the datasets
      LineData data = new LineData(dataSets);

      // set data
      chart.setData(data);
      chart.animateXY(1000, 1000);
      data.setHighlightEnabled(true);

    }
  }

  private void chartMarker(){
    CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view);
    mv.setChartView(chart); // For bounds control
    chart.setMarker(mv); // Set the marker to the chart
  }

  private void buttons() {
    day_button.setSelected(true);

    day_button.setOnClickListener(this);
    week_button.setOnClickListener(this);
    month_button.setOnClickListener(this);
  }

  public void onClick(View v) {

    View view = getActivity().getCurrentFocus();
    if (view != null) {
      InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    switch (v.getId()) {

      case R.id.day_button:
        day_button.setSelected(true);
        week_button.setSelected(false);
        month_button.setSelected(false);
        drawGradientChart();
        chart.animateXY(1000, 1000);
        break;

      case R.id.week_button:
        day_button.setSelected(false);
        week_button.setSelected(true);
        month_button.setSelected(false);
        drawGradientChart();
        chart.animateXY(1000, 1000);
        break;

      case R.id.month_button:
        day_button.setSelected(false);
        week_button.setSelected(false);
        month_button.setSelected(true);
        drawGradientChart();
        chart.animateXY(1000, 1000);
        break;

    }
  }

  @Override
  public void onValueSelected(Entry e, Highlight h) {
//    Log.i("Entry selected", e.toString());
//    Log.i("LOWHIGH", "low: " + mChart.getLowestVisibleX() + ", high: " + mChart.getHighestVisibleX());
//    Log.i("MIN MAX", "xmin: " + mChart.getXChartMin() + ", xmax: " + mChart.getXChartMax() + ", ymin: " + mChart.getYChartMin() + ", ymax: " + mChart.getYChartMax());

  }

  @Override
  public void onNothingSelected() {
//    Log.i("Nothing selected", "Nothing selected.");
  }

  private void photoStripe(){

    uriList = new ArrayList<>();

    ((ActivityMain) getActivity()).getFireBase().child("photos").addChildEventListener(new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        uriList.add(0, String.valueOf(dataSnapshot.getValue()));
        adapterPhotoStripe.notifyDataSetChanged();
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        for (int i = 0; i < uriList.size(); i++) {
          if (uriList.get(i).equals(dataSnapshot.getKey())) {
            uriList.remove(i);
            break;
          }
        }
        uriList.add(0, String.valueOf(dataSnapshot.getValue()));
        adapterPhotoStripe.notifyDataSetChanged();
      }

      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        for (int i = 0; i < uriList.size(); i++) {
          if (uriList.get(i).equals(dataSnapshot.getKey())) {
            uriList.remove(i);
            break;
          }
        }
      }

      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {

      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });

    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
    photoRecycler.setLayoutManager(layoutManager);
    adapterPhotoStripe = new AdapterPhotoStripe(this, getActivity(), uriList);
    photoRecycler.setAdapter(adapterPhotoStripe);
  }

  public void upload(String uri){

    final ProgressDialog progressDialog = new ProgressDialog(getActivity());
    progressDialog.setTitle(getActivity().getResources().getString(R.string.upload));
    progressDialog.show();

    File path = new File(Environment.getExternalStorageDirectory(), "temp/image_cache");

    StorageReference storageref = ((ActivityMain) getActivity()).getFirebaseStorage().getReference().child("photos");
    StorageReference ref = storageref.child(uri);

    ref.putFile(Uri.fromFile(new File(path + "/" + (uri)))).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
      @Override
      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        progressDialog.dismiss();
        uploaded_image_url = taskSnapshot.getDownloadUrl().toString();

        ((ActivityMain) getActivity()).getFireBase().addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {

            ((ActivityMain) getActivity()).getFireBase().child("photos").push().setValue(uploaded_image_url);

          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
        });
      }
    })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            progressDialog.dismiss();
            ((ActivityMain) getActivity()).snack(getActivity().getString(R.string.error_upload));

          }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
      @Override
      public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
        progressDialog.setMessage(getActivity().getResources().getString(R.string.progress) + " " + ((int) progress) + " %");
      }
    });

  }

}
