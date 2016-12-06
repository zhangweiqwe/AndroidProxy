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

import cn.wsgwz.gravity.fragment.GraspDataFilePreviewFragment;
import cn.wsgwz.gravity.util.LogUtil;

/**
* Created by arne on 18.11.14.
*/
public class ScreenSlidePagerForGraspPreviewAdapter extends FragmentStatePagerAdapter {

    private final Resources res;
    private Bundle mFragmentArguments;
    private List<String> list;

    public void setFragmentArgs(Bundle fragmentArguments) {
        mFragmentArguments = fragmentArguments;
    }

    static class Tab {
        public Class<? extends Fragment> fragmentClass;
        String mName;

        public Tab(Class<? extends Fragment> fClass,  String name){
            mName = name;
            fragmentClass = fClass;
        }

    }


    private Vector<Tab> mTabs = new Vector<Tab>();

    public ScreenSlidePagerForGraspPreviewAdapter(FragmentManager fm, Context c) {
        super(fm);
        list = new ArrayList<>();
        res = c.getResources();
    }

    @Override
    public Fragment getItem(int position) {
        try {
            Fragment fragment = mTabs.get(position).fragmentClass.newInstance();
            if(fragment instanceof GraspDataFilePreviewFragment){
                GraspDataFilePreviewFragment graspDataFilePreviewFragment = (GraspDataFilePreviewFragment)fragment;
                graspDataFilePreviewFragment.setContent(list.get(position));
            }

            if (mFragmentArguments!=null)
                fragment.setArguments(mFragmentArguments);
            return fragment;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return  null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position).mName;
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    public void addTab(@StringRes int name, Class<? extends Fragment> fragmentClass) {
        mTabs.add(new Tab(fragmentClass, res.getString(name)));
    }
    public void addTab(String name, Class<? extends Fragment> fragmentClass) {
        mTabs.add(new Tab(fragmentClass, name));
    }

    public void addItemContentStr(String contentStr){
        list.add(contentStr);
    }

}
