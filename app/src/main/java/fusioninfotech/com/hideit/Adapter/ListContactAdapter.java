package fusioninfotech.com.hideit.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fusioninfotech.com.hideit.Helper.Constant;
import fusioninfotech.com.hideit.Helper.ContactModel;
import fusioninfotech.com.hideit.R;

/**
 * Created by MAIN on 08-12-2017.
 */

public class ListContactAdapter extends RecyclerView.Adapter<ListContactAdapter.MyViewHolder> {



    private Activity context;
    public  List list_checked;
    private SparseBooleanArray mSelectedItemsIds;


    public class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkbox;
        TextView tv_contactname, tv_contactnumber;

        public MyViewHolder(View itemView) {
            super(itemView);

            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            tv_contactname = (TextView) itemView.findViewById(R.id.tv_contactname);
            tv_contactnumber = (TextView) itemView.findViewById(R.id.tv_number);

        }
    }

    public ListContactAdapter(Activity context, List<ContactModel> list_contact_name) {
        Constant.Constant_list_contact_model = list_contact_name;
        mSelectedItemsIds = new SparseBooleanArray();
        this.context = context;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_contact, parent, false);

        list_checked = new ArrayList();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.tv_contactnumber.setText( Constant.Constant_list_contact_model.get(position).getContact_number());
        holder.tv_contactname.setText( Constant.Constant_list_contact_model.get(position).getContact_name());


        System.out.println(" getttt "+ Constant.Constant_list_contact_model.get(position).getContact_name());

        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (holder.checkbox.isChecked()) {
                    Constant.Constant_list_contact_model.get(position).setIs_selected(true);
                    System.out.println("data checked  "+ Constant.Constant_list_contact_model.get(position).getContact_name());

                }else {
                    Constant.Constant_list_contact_model.get(position).setIs_selected(false);
                    System.out.println("data unnchecked"+ Constant.Constant_list_contact_model.get(position).getContact_name());
                }



            }
        });


    }

    @Override
    public int getItemCount() {
        return  Constant.Constant_list_contact_model.size();
    }
}
