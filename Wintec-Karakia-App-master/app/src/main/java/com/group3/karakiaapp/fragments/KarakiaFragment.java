package com.group3.karakiaapp.fragments;

import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import static com.group3.karakiaapp.MainActivity.Utils.*;

import com.group3.karakiaapp.*;
import com.group3.karakiaapp.R;

import androidx.annotation.*;
import androidx.fragment.app.*;

public class KarakiaFragment extends FragmentBase {
    VideoView player;
    TextView title;
    TextView contents;
    static boolean audioOnly;
    static boolean wordsInEnglish;
    Karakia song;
    public KarakiaFragment() {
        super(R.layout.fragment_karakia);
    }
    // TODO: See what can be done to add animations for changing between different bits of text in the words display.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        player = (VideoView)view.findViewById(R.id.video_karakia);
        title = (TextView)view.findViewById(R.id.text_karakiaName);
        contents = (TextView)view.findViewById(R.id.text_karakiaWords);

        Bundle args = getArguments();
        if (args != null)
            song = ResourceManager.Instance().karakias.get(args.getInt("karakiaId"));
        if (song == null)
            song = ResourceManager.Instance().karakias.get(0);

        MediaController controller = new MediaController(getContext());
        controller.setAnchorView(player);
        player.setMediaController(controller);
        if (song != null) {
            UpdateVideo();
            Switch audio = view.findViewById(R.id.switch_video);
            if (audio != null) {
                audio.setChecked(audioOnly);
                audio.setOnCheckedChangeListener((x, y) -> {
                    audioOnly = y;
                    UpdateVideo();
                });
            }

            if (title != null)
                title.setText(song.name);
            if (contents != null) {
                UpdateWords();
                Switch translate = view.findViewById(R.id.button_karakiaTranslate);
                if (translate != null) {
                    translate.setChecked(wordsInEnglish);
                    translate.setOnCheckedChangeListener((x, y) -> {
                        wordsInEnglish = y;
                        UpdateWords();
                    });
                }
            }
            Button origins = view.findViewById(R.id.button_karakiaOrigins);
            origins.setOnClickListener((x) -> MainActivity.instance.OpenInfoCard(song.origins));
        }
        MainActivity.instance.TryOpenTorial(ResourceManager.Instance().helpVideos.get("karakia"));
    }

    public void UpdateWords() {
        contents.setText(wordsInEnglish ? song.wordsEnglish : song.wordsMaori);
    }
    public void UpdateVideo() {
        int pos = player.getCurrentPosition();
        player.setVideoURI(Uri.parse("android.resource://" + getContext().getPackageName() + "/" +
                (audioOnly ? song.audio : song.video)));
        try {
            player.seekTo(pos);
        } catch (Exception e) {}
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("player_playing",player.isPlaying());
        outState.putInt("player_position",player.getCurrentPosition());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        TryGetValue(savedInstanceState,"player_position",(Integer x) -> player.seekTo(x));
        TryGetValue(savedInstanceState,"player_playing",(Boolean x) -> {
            if (x)
                player.start();
            else
                player.pause();
        });
    }
}
