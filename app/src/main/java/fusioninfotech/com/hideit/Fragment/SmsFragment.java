package fusioninfotech.com.hideit.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

import fusioninfotech.com.hideit.Helper.Constant;
import fusioninfotech.com.hideit.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SmsFragment extends Fragment {

    FloatingActionButton fab_sms;
    ListView lv_imported_sms;
    ArrayAdapter adar;
    ArrayList arrayList;


    public SmsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_sms, container, false);

        fab_sms = (FloatingActionButton) view.findViewById(R.id.menu_sms);
        lv_imported_sms = (ListView)view.findViewById(R.id.lv_imported_sms);

        arrayList = new ArrayList();

        for (int i = 0; i < Constant.Constant_list_sms_model.size(); i++){

            if (Constant.Constant_list_sms_model.get(i).isIs_selected()){
                arrayList.add(Constant.Constant_list_sms_model.get(i).getNumber());
            }

        }

        if (arrayList.size()!= 0){
            adar = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1,  arrayList);
            lv_imported_sms.setAdapter(adar);
        }
        fab_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getActivity(),ListSmsActivity.class);
                startActivity(i);
            }
        });



        return view;
    }

}
