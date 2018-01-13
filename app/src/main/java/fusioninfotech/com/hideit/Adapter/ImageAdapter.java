package fusioninfotech.com.hideit.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import fusioninfotech.com.hideit.R;

/**
 * Created by MAIN on 29-11-2017.
 */

public class ImageAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    Activity context;
    String media;
    ArrayList<Bitmap> array_bitmap;
    private SparseBooleanArray mSelectedItemsIds;


    public ImageAdapter(Activity context , ArrayList<Bitmap> array_bitmap) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.array_bitmap = array_bitmap;
        this.context = context;
        this.media = media;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    public int getCount() {
        return array_bitmap.size();
    }

    public Bitmap getItem(int position) {
        return array_bitmap.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.layout_image, null);
            holder.img_gallery = (ImageView) convertView.findViewById(R.id.img_gallery);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

         holder.img_gallery.setImageBitmap(array_bitmap.get(position));

        return convertView;
    }


    private class ViewHolder {

        ImageView img_gallery;

    }


    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    private void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();

    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;

    }
}


