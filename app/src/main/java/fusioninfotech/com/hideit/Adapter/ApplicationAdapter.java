package fusioninfotech.com.hideit.Adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fusioninfotech.com.hideit.Helper.Constant;
import fusioninfotech.com.hideit.R;

/**
 * Created by MAIN on 30-11-2017.
 */

public class ApplicationAdapter extends BaseAdapter {
    private List<ApplicationInfo> appsList = null;
    private Context context;
    private PackageManager packageManager;


    public ApplicationAdapter(Context context,List<ApplicationInfo> appsList) {
        this.context = context;
        this.appsList = appsList;
        packageManager = context.getPackageManager();
        Constant.positionArray = new ArrayList<Boolean>(appsList.size());
        for(int i =0;i<appsList.size();i++){
            Constant.positionArray.add(false);
        }
    }

    @Override
    public int getCount() {
        return appsList.size();
    }

    @Override
    public ApplicationInfo getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_applist, null);
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.chkbox_app.setOnCheckedChangeListener(null);
            holder.chkbox_app.setChecked( Constant.positionArray.get(position));

        }
        ApplicationInfo applicationInfo = appsList.get(position);
        if (null != applicationInfo) {

            holder.appName.setText(applicationInfo.loadLabel(packageManager));
            holder.packageName.setText(applicationInfo.packageName);
            holder.iconview.setImageDrawable(applicationInfo.loadIcon(packageManager));
        }
        holder.chkbox_app.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked ){
                    Constant.positionArray.set(position, true);
                }else
                    Constant.positionArray.set(position, false);
            }
        });


        return convertView;
    }

    private ViewHolder createViewHolder(View convertView) {

        ViewHolder holder = new ViewHolder();
        holder.appName = (TextView) convertView.findViewById(R.id.app_name);
        holder.packageName = (TextView) convertView.findViewById(R.id.app_paackage);
        holder.iconview = (ImageView) convertView.findViewById(R.id.app_icon);
        holder.chkbox_app = (CheckBox) convertView.findViewById(R.id.chkbox_app);

        return holder;
    }

    private class ViewHolder {

        TextView appName, packageName;
        ImageView iconview;
        CheckBox chkbox_app;
    }
};
