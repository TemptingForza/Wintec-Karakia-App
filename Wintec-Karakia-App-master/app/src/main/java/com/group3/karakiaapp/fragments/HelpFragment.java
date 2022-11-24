package com.group3.karakiaapp.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.group3.karakiaapp.HelpVideo;
import com.group3.karakiaapp.MainActivity;
import com.group3.karakiaapp.R;
import com.group3.karakiaapp.ResourceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.navigation.Navigation;

public class HelpFragment extends FragmentBase {
    LinearLayout container;
    View self;
    public HelpFragment() { super(R.layout.fragment_help); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        self = view;
        container = view.findViewById(R.id.help_menu);
        for (HelpVideo v : ResourceManager.Instance().helpVideos.values())
            AddItem(v.name).setOnClickListener((x) -> MainActivity.instance.OpenTorial(v));
    }

    public CardView AddItem(String Message) {
        CardView card = (CardView)View.inflate(self.getContext(),R.layout.item_help,new CardView(self.getContext()));
        card.setId(View.generateViewId());
        container.addView(card);
        ((TextView)card.findViewById(R.id.help_name)).setText(Message);
        return card;
    }
}
