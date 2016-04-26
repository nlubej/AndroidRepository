package nlubej.gains.Adapters;

import android.support.v4.app.Fragment;
/**
 * Created by nlubej on 19.10.2015.
 */

public class TabPagerItem {

    private final CharSequence mTitle;
    private final Fragment mFragment;

    public TabPagerItem(CharSequence title, Fragment fragment) {
        this.mTitle = title;
        this.mFragment = fragment;
    }

    public Fragment getFragment() {
        return mFragment;
    }

    public CharSequence getTitle() {
        return mTitle;
    }
}