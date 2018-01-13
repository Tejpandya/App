package fusioninfotech.com.hideit.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import fusioninfotech.com.hideit.R;

/**
 * Created by MAIN on 08-12-2017.
 */

public class SmsAdapter extends BaseAdapter {


    private Activity context;

    private SparseBooleanArray mSelectedItemsIds;
    ArrayList<HashMap<String, String>> arrayList;
    private LayoutInflater mInflater;

    public SmsAdapter(Activity context, ArrayList<HashMap<String, String>> arrayList) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.arrayList = arrayList;
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;

    }

    public int getCount() {
        return arrayList.size();
    }

    @Override
    public String getItem(int i) {
        return arrayList.get(i).get("number");
    }

    public String getName(int i) {
        return arrayList.get(i).get("name");
    }


    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View itemView, ViewGroup parent) {
        final ViewHolder holder;
        if (itemView == null) {
            holder = new ViewHolder();
            itemView = mInflater.inflate(R.layout.list_item_hide_contact, null);



            holder.tv_contactname = (TextView) itemView.findViewById(R.id.tv_contactname);
            holder.tv_contactnumber = (TextView) itemView.findViewById(R.id.tv_number);
            itemView.setTag(holder);
        } else {
            holder = (ViewHolder) itemView.getTag();
        }

        holder.tv_contactnumber.setText(arrayList.get(position).get("number"));
        holder.tv_contactname.setText(arrayList.get(position).get("name"));

        return itemView;
    }


    private class ViewHolder {

        CheckBox checkbox;
        TextView tv_contactname, tv_contactnumber;


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
