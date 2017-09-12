package com.aioplayer.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.aioplayer.MainActivity;

/**
 * Created by akankshadhanda on 14/07/17.
 */

public class BaseFragment extends Fragment{
    public static String TAG="basefragment";
    private MainActivity mainActivity;

    public static void setTAG(String TAG) {
        BaseFragment.TAG = TAG;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        setRetainInstance(true);
        this.mainActivity=(MainActivity)context;
    }

    public String getTAG() {
        return TAG;
    }
}
