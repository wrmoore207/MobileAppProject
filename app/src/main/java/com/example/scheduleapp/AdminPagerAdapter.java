package com.example.scheduleapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
public class AdminPagerAdapter extends FragmentStateAdapter{
    public AdminPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public AdminPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public AdminPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 1: return new MakeSchedule();
            case 2: return new Requests();
            case 3: return new Profile();
            default: return new Today();
        }    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
