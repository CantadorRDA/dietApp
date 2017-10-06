package co.tink.diet.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import co.tink.diet.app.R;
import co.tink.diet.app.activities.ActivityMain;
import co.tink.diet.app.fragments.FragmentGraph;

/**
 * Created by Cantador on 31.07.17.
 */

public class AdapterPhotoStripe extends RecyclerView.Adapter<AdapterPhotoStripe.HorizontalViewHolder> {

  private List<String> uriList;
  private Context context;
  FragmentGraph fragmentGraph;

  private final int VIEW_TYPE_ITEM = 0;
  private final int VIEW_TYPE_ADD = 1;

  public AdapterPhotoStripe(FragmentGraph fragmentGraph, Context context, List<String> uriList) {
    this.uriList = uriList;
    this.context = context;
    this.fragmentGraph = fragmentGraph;
  }

  @Override
  public HorizontalViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    if (viewType == VIEW_TYPE_ITEM) {
      View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_photo, viewGroup, false);
      return new HorizontalViewHolder(view);
    } else if (viewType == VIEW_TYPE_ADD) {
      View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_photo_add, viewGroup, false);
      return new HorizontalViewHolder(view);
    }
    return null;
  }

  @Override
  public void onBindViewHolder(final HorizontalViewHolder viewHolder, int i) {

    if (getItemViewType(i) == VIEW_TYPE_ITEM) {
      String uri = uriList.get(i - 1);
      Glide.with(context).load(uri).into(viewHolder.photo);

    } else if (getItemViewType(i) == VIEW_TYPE_ADD) {

      viewHolder.photo.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          loadImage();
        }
      });

    }


  }

  @Override
  public int getItemCount() {
    return (null != uriList ? uriList.size() + 1 : 1);
  }

  @Override
  public int getItemViewType(int position) {
    return position == 0 ? VIEW_TYPE_ADD : VIEW_TYPE_ITEM;
  }

  public static class HorizontalViewHolder extends RecyclerView.ViewHolder {
    private ImageView photo;


    public HorizontalViewHolder(View view) {
      super(view);
      this.photo = (ImageView) view.findViewById(R.id.photo);
    }
  }

  private void loadImage() {
    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    ((ActivityMain) context).startActivityForResult(i, 1);
  }
}