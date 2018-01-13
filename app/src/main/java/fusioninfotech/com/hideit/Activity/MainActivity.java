package fusioninfotech.com.hideit.Activity;

import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.Toast;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;

import java.util.ArrayList;
import java.util.HashMap;

import fusioninfotech.com.hideit.R;


public class MainActivity extends AppCompatActivity {

    LinearLayout linear_app_lock, linear_image, linear_video, linear_contact,linear_document;
    Dialog dialog;
    SimpleAdapter simpleAdapter;
    int[] icon = new int[]{R.drawable.ic_pattern, R.drawable.ic_pattern, R.drawable.ic_pattern, R.drawable.ic_pattern};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linear_contact = (LinearLayout) findViewById(R.id.linear_contact);
        linear_app_lock = (LinearLayout) findViewById(R.id.linear_app_lock);
        linear_image = (LinearLayout) findViewById(R.id.linear_image);
        linear_video = (LinearLayout) findViewById(R.id.linear_video);
        linear_document= (LinearLayout) findViewById(R.id.linear_document);

        checkAllpermission();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            if (!isAccessGranted()) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }

        linear_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, HideDocumentActivity.class);
                startActivity(i);
            }
        });


        linear_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ContactSmsActivity.class);
                startActivity(i);
            }
        });


        linear_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, HideImagesActivity.class);
                startActivity(i);

            }
        });

        linear_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, HideVideoActivity.class);
                startActivity(i);
            }
        });

        linear_app_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, ApplicationListActivity.class);
                startActivity(i);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_setting:
                Intent i = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(i);
                return true;

            case R.id.menu_change_icon:


                ArrayList array_color = new ArrayList();
                array_color.add("Black");
                array_color.add("Blue");
                array_color.add("Yellow");
                array_color.add("White");

                ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
                for (int j = 0; j < array_color.size(); j++) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("name", array_color.get(j).toString());
                    hashMap.put("image", icon[j] + "");
                    arrayList.add(hashMap);
                }
                String[] from = {"name", "image"};
                int[] to = {R.id.tv_icon_name, R.id.image_icon};
                simpleAdapter = new SimpleAdapter(this, arrayList, R.layout.list_change_icon, from, to);//Create object and set the parameters for simpleAdapter
                createDialog();
                initDialog();
                dialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void checkAllpermission() {

        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this,
                new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        // Proceed with initialization
                        // Toast.makeText(MainActivity.this, "User allow following permiossion", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onDenied(String permission) {
                        // Notify the user that you need all of the permissions

                        Toast.makeText(MainActivity.this, "User denied following permiossion" + permission, Toast.LENGTH_SHORT).show();
                        System.out.println();

                    }
                });

    }

    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    public void createDialog() {
        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_change_icon);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

    }


    public void initDialog() {
        ListView lv_change_icon = (ListView) dialog.findViewById(R.id.lv_change_icon);
        lv_change_icon.setAdapter(simpleAdapter);



        lv_change_icon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i){
                    case  0:
                        getPackageManager().setComponentEnabledSetting(new ComponentName("fusioninfotech.com.hideit", "fusioninfotech.com.hideit.Activity.INTRO_PASSWORD.ChoosePasswordPatternActivity_newalias"),
                                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                        break;



                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK
                && null != data) {

            Log.d("PASSWORD ", "user password is " + data.getData());
        }

    }

}
