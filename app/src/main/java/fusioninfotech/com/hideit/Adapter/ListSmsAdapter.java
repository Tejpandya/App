package fusioninfotech.com.hideit.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import fusioninfotech.com.hideit.Helper.Constant;
import fusioninfotech.com.hideit.Helper.SMSData;
import fusioninfotech.com.hideit.R;

/**
 * Created by MAIN on 08-12-2017.
 */

public class ListSmsAdapter extends RecyclerView.Adapter<ListSmsAdapter.MyViewHolder> {



    private Activity context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkbox;
        TextView tv_smsname, tv_smsbody;

        public MyViewHolder(View itemView) {
            super(itemView);

            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            tv_smsname = (TextView) itemView.findViewById(R.id.tv_contactname);
            tv_smsbody = (TextView) itemView.findViewById(R.id.tv_number);

        }
    }

    public ListSmsAdapter(Activity context, List<SMSData> list_contact_name) {
        Constant.Constant_list_sms_model = list_contact_name;

        this.context = context;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_contact, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

    holder.tv_smsname.setText( Constant.Constant_list_sms_model.get(position).getNumber());
        holder.tv_smsbody.setText( Constant.Constant_list_sms_model.get(position).getBody());
        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (holder.checkbox.isChecked()) {
                    Constant.Constant_list_sms_model.get(position).setIs_selected(true);
                    System.out.println("data checked  "+ Constant.Constant_list_sms_model.get(position).getNumber());

                }else {
                    Constant.Constant_list_sms_model.get(position).setIs_selected(false);
                    System.out.println("data unnchecked"+ Constant.Constant_list_sms_model.get(position).getNumber());
                }



            }
        });


    }

    @Override
    public int getItemCount() {
        return  Constant.Constant_list_sms_model.size();
    }
}
