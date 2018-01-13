package fusioninfotech.com.hideit.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import fusioninfotech.com.hideit.R;

/**
 * Created by MAIN on 29-11-2017.
 */

public class DocumentAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    Activity context;
    String media;
    ArrayList array_Document;


    public DocumentAdapter(Activity context , ArrayList array_Document) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.array_Document = array_Document;
        this.context = context;
        
    }

    public int getCount() {
        return array_Document.size();
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
            convertView = mInflater.inflate(R.layout.layout_document, null);
            holder.tv_documentname = (TextView) convertView.findViewById(R.id.tv_documentname);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

         holder.tv_documentname.setText(array_Document.get(position).toString());

        return convertView;
    }


    private class ViewHolder {

        TextView tv_documentname;

    }
}


