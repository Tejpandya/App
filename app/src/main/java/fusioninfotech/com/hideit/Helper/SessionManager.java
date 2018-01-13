package fusioninfotech.com.hideit.Helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


import java.util.HashSet;
import java.util.Set;

import fusioninfotech.com.hideit.Activity.INTRO_PASSWORD.ChoosePasswordPatternActivity;


/**
 * Created by lucsonmacpc5 on 18/10/16.
 */
public class SessionManager {

    // Shared Preferences
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;


    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "hideit_prefs";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        // commit changes
        editor.commit();
    }


    public void setApplicationStatus(String appname ,boolean status) {

        Set<String> set =  new HashSet<String>();
        set.add(appname);
        set.add(String.valueOf(status));

        editor.putStringSet("application_status",set);

        // commit changes
        editor.commit();
    }


    public void setBlockedList(Set<String> set ) {


        editor.putStringSet("blocked_list",set);

        // commit changes
        editor.commit();
    }

    public Set<String> getBlockedList() {

        Set<String> set =  new HashSet<String>();
        set.add("");


        return pref.getStringSet("blocked_list",set);
    }


    public void setType(String type) {

        editor.putString("type", type);
        // commit changes
        editor.commit();
    }

    public String getType() {
        return pref.getString("type", "password");
    }
    public Set<String> getApplicationStatus() {

        Set<String> set =  new HashSet<String>();
        set.add("sad");
        set.add(String.valueOf(false));

        return pref.getStringSet("application_status",set);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();



        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, ChoosePasswordPatternActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring LoginActivity Activity
        _context.startActivity(i);
    }
}