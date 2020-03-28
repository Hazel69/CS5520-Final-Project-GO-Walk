package edu.neu.madcourse.gowalk.activity;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class RecordCollectionAdapter extends FragmentStateAdapter {

    private static final String[] intervals = {"DAILY", "WEEKLY", "MONTHLY"};

    public RecordCollectionAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return a NEW fragment instance in createFragment(int)
        Fragment fragment = new RecordsFragment();
        Bundle args = new Bundle();
        args.putString(RecordsFragment.ARG_INTERVAL, intervals[position]);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return intervals.length;
    }
}
