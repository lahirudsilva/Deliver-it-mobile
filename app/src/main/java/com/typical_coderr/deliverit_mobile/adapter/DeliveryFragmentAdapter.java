package com.typical_coderr.deliverit_mobile.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.typical_coderr.deliverit_mobile.DeliveryRequestFragment;
import com.typical_coderr.deliverit_mobile.WarehouseFragment;
import com.typical_coderr.deliverit_mobile.PickupRequestFragment;

/**
 * Created by Android Studio.
 * User: Lahiru
 * Date: Thu
 * Time: 10:07 PM
 */
public class DeliveryFragmentAdapter extends FragmentStateAdapter {
    public DeliveryFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position)
        {
            case 0:
                return new PickupRequestFragment();
            case 1:
                return new WarehouseFragment();
            case 2:
                return new DeliveryRequestFragment();
            default: throw new IllegalArgumentException();
        }


    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
