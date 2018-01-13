package fusioninfotech.com.hideit.Activity;


import android.app.FragmentTransaction;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import fusioninfotech.com.hideit.Adapter.TabsPagerAdapter;
import fusioninfotech.com.hideit.R;

public class ContactSmsActivity extends AppCompatActivity  {

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;

    // Tab titles
    private String[] tabs_text = { "Contact", "Sms" };
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_sms);

        viewPager = (ViewPager) findViewById(R.id.pager);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);


        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        if (viewPager != null)
            viewPager.setAdapter(mAdapter);

        if (mTabLayout != null) {
            mTabLayout.setupWithViewPager(viewPager);

            // setupTabIcons();
            for (int i = 0; i < mTabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = mTabLayout.getTabAt(i);
                if (tab != null)
                    tab.setText(tabs_text[i]);
            }

        }

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



    }


}
