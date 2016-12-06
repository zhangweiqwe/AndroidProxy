/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package cn.wsgwz.gravity.view.slidingTabLayout;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import cn.wsgwz.gravity.fragment.DefinedShellFragment;

/**
* Created by arne on 18.11.14.
*/
public class ScreenSlidePagerForDefinedShellAdapter extends FragmentStatePagerAdapter {

    private final Resources res;
    private Bundle mFragmentArguments;
    private List<String>  titleList = new ArrayList<>();
    private List<DefinedShellFragment> fragmentList = new ArrayList<>();

    public ScreenSlidePagerForDefinedShellAdapter(FragmentManager fm, Context c) {
        super(fm);
        res = c.getResources();
    }

    @Override
    public Fragment getItem(int position) {
            return  fragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList==null?0:fragmentList.size();
    }

    public void addTab(String name, DefinedShellFragment definedShellFragment) {
        titleList.add(name);
        fragmentList.add(definedShellFragment);
    }

}
