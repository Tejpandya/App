package fusioninfotech.com.hideit.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import fusioninfotech.com.hideit.R;

public class SettingActivity extends AppCompatActivity {


    ListView lv_setting;
    ArrayList array_setting;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        lv_setting = (ListView)findViewById(R.id.lv_setting);
        array_setting = new ArrayList();

        array_setting.add("Change Password");


        ArrayAdapter adar = new ArrayAdapter(SettingActivity.this,android.R.layout.simple_list_item_1,array_setting);
        lv_setting.setAdapter(adar);


    }
}
