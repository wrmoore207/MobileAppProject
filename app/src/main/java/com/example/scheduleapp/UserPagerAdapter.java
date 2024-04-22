package com.example.scheduleapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class UserPagerAdapter extends FragmentStateAdapter {
    public UserPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 1: return new WeeklySchedule();
            case 2: return new Requests();
            case 3: return new Profile();
            default: return new Today();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}