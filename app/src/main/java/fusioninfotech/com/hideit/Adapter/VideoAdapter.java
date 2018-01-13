package fusioninfotech.com.hideit.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import fusioninfotech.com.hideit.Helper.Constant;
import fusioninfotech.com.hideit.R;

/**
 * Created by MAIN on 29-11-2017.
 */

public class VideoAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    Activity context;
    private SparseBooleanArray mSelectedItemsIds;

    public VideoAdapter(Activity context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        mSelectedItemsIds = new SparseBooleanArray();

    }

    public int getCount() {
        return Constant.selected_videos.size();
    }

    public Object getItem(int position) {
        return position;
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
        holder.img_gallery.setImageBitmap(Constant.videos_bitmap.get(position));
        return convertView;
    }


    private class ViewHolder {
        ImageView img_gallery;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
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


