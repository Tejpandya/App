package fusioninfotech.com.hideit.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import fusioninfotech.com.hideit.Fragment.ContactFragment;
import fusioninfotech.com.hideit.Fragment.SmsFragment;

/**
 * Created by MAIN on 08-12-2017.
 */

public class TabsPagerAdapter  extends FragmentPagerAdapter {
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new ContactFragment();
            case 1:
                // Games fragment activity
                return new SmsFragment();

        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

}
