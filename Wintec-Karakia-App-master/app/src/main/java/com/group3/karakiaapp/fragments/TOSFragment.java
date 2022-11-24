package com.group3.karakiaapp.fragments;

import com.group3.karakiaapp.R;

import android.os.*;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.*;
import com.group3.karakiaapp.*;

public class TOSFragment extends FragmentBase {
    public TOSFragment() { super(R.layout.fragment_tos); }
    NavController nav;
    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            MainActivity.instance.drawer.close();
        }
        @Override
        public void onDrawerOpened(@NonNull View drawerView) {
            MainActivity.instance.drawer.close();
        }
        @Override
        public void onDrawerClosed(@NonNull View drawerView) {}
        @Override
        public void onDrawerStateChanged(int newState) {}
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nav = Navigation.findNavController(view);
        if (MainActivity.Settings.agreedToToS) {
            MainActivity.Settings.agreedToToS = false;
            ResourceManager.Instance().WriteSettings();
        }
        ((Button)view.findViewById(R.id.button_agreeToS)).setOnClickListener((x) -> {
            MainActivity.Settings.agreedToToS = true;
            ResourceManager.Instance().WriteSettings();
            nav.navigateUp();
        });
        ((Button)view.findViewById(R.id.button_declineToS)).setOnClickListener((x) -> {
            getActivity().finish();
            System.exit(0);
        });
        MainActivity.instance.toolbar.setVisibility(View.GONE);
        MainActivity.instance.drawer.addDrawerListener(listener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity.instance.toolbar.setVisibility(View.VISIBLE);
        MainActivity.instance.drawer.removeDrawerListener(listener);
    }


}
