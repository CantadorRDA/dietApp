package co.tink.diet.app.view;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import co.tink.diet.app.R;

/**
 * Created by Cantador on 31.07.17.
 */

public class CustomMarkerView extends MarkerView {

  private TextView tvContent;

  public CustomMarkerView(Context context, int layoutResource) {
    super(context, layoutResource);

    tvContent = (TextView) findViewById(R.id.tvContent);
  }

  // callbacks everytime the MarkerView is redrawn, can be used to update the
  // content (user-interface)
  @Override
  public void refreshContent(Entry e, Highlight highlight) {

      tvContent.setText("" + Utils.formatNumber(e.getY(), 0, true));

    super.refreshContent(e, highlight);
  }

  @Override
  public MPPointF getOffset() {
    return new MPPointF(-(getWidth() / 2), -getHeight());
  }
}