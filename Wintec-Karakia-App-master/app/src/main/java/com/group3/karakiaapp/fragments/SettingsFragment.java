package com.group3.karakiaapp.fragments;

import com.group3.karakiaapp.R;

import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.annotation.*;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.*;
import androidx.navigation.*;

import com.group3.karakiaapp.*;

public class SettingsFragment extends FragmentBase {
    LinearLayout container;
    View self;
    public SettingsFragment() { super(R.layout.fragment_settings); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        self = view;
        container = view.findViewById(R.id.setting_menu);
        Switch ToS = AddItem("Agreed to Terms Of Service");
        ToS.setChecked(true);
        ToS.setOnClickListener((x) -> {
            ToS.setChecked(true);
            MainActivity.Settings.agreedToToS = false;
            ResourceManager.Instance().WriteSettings();
            Navigation.findNavController(view).navigate(SettingsFragmentDirections.actionSettingsFragmentToTOSFragment());
        });
    }

    public Switch AddItem(String Message) {
        CardView card = (CardView)View.inflate(self.getContext(),R.layout.item_setting,new CardView(self.getContext()));
        card.setId(View.generateViewId());
        container.addView(card);
        Switch s = card.findViewById(R.id.setting_text);
        s.setText(Message);
        return s;
    }
}
