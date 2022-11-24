package com.group3.karakiaapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.google.android.material.navigation.NavigationView;
import com.group3.karakiaapp.fragments.FragmentBase;
import com.group3.karakiaapp.fragments.TOSFragment;

import static com.group3.karakiaapp.MainActivity.Utils.*;


import java.util.function.Predicate;

import androidx.annotation.*;
import androidx.appcompat.app.*;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.*;
import androidx.drawerlayout.widget.*;
import androidx.navigation.*;
import androidx.navigation.fragment.*;
import androidx.navigation.ui.*;

public class MainActivity extends AppCompatActivity {
    Button info_link;
    public static MainActivity instance;
    NavController navController;
    AppBarConfiguration appBarConfig;
    View infocardContainer;
    TextView infocard;
    View viewOverlay;
    View torialContainer;
    TextView torialText;
    VideoView torialVideo;
    Switch torialSwitch;
    Drawable defaultIcon;
    ImageView toolBarIcon;
    NavHostFragment navHost;
    static HelpVideo openTorial;
    MediaController torialControl;
    public Toolbar toolbar;
    public DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navhome);
        FragmentBase.OnChange = this::OnChangeFragment;
        instance = this;
        toolBarIcon = findViewById(R.id.toolbar_icon);
        infocardContainer = findViewById(R.id.infocard_container);
        infocard = findViewById(R.id.infocard_text);
        findViewById(R.id.infocard_close).setOnClickListener((x) -> CloseInfoCard());

        torialContainer = findViewById(R.id.torial_container);
        torialText = findViewById(R.id.torial_text);
        torialSwitch = findViewById(R.id.torial_switch);
        torialVideo = findViewById(R.id.torial_video);
        torialControl = new MediaController(this);
        torialVideo.setMediaController(torialControl);
        ((Button)findViewById(R.id.torial_close)).setOnClickListener((x) -> CloseTorial());
        torialSwitch.setOnCheckedChangeListener((x,y) -> {
            if (openTorial == null)
                return;
            if (openTorial.autoplay != y) {
                openTorial.autoplay = y;
                ResourceManager.Instance().WriteSettings();
            }
        });

        navHost = (NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.nav_host);
        navController = navHost.getNavController();

        AppBarConfiguration.Builder config = new AppBarConfiguration.Builder(R.id.HomeFragment);
        drawer = findViewById(R.id.drawer);
        config.setOpenableLayout(drawer);
        appBarConfig = config.build();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationUI.setupActionBarWithNavController(this,navController, appBarConfig);
        //NavigationUI.setupWithNavController((BottomNavigationView)findViewById(R.id.quicknav_bar),navController);
        NavigationUI.setupWithNavController((NavigationView)findViewById(R.id.navmenu),navController);

        viewOverlay = findViewById(R.id.overlay);
        viewOverlay.setVisibility(View.GONE);
        viewOverlay.setOnClickListener((x) -> {
            if (infocardOpen)
                CloseInfoCard();
        });

        findViewById(R.id.toolbar_icon).setOnClickListener((x) -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://wintec.ac.nz")));
        });
        torialContainer.setOnClickListener((x) -> {});
        OnChangeFragment(FragmentBase.last);

        info_link = findViewById(R.id.info_link);
        info_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goLink("https://www.takai.nz/find-resources/articles/karakia/");
            }
        });
    }

    private void goLink(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    @Override
    public boolean onSupportNavigateUp() {
        return (!(FragmentBase.last instanceof TOSFragment) && NavigationUI.navigateUp(navController,appBarConfig)) || super.onSupportNavigateUp();
    }

    public void OnChangeFragment(FragmentBase newFragment) {
        if (newFragment.icon != null) {
            if (defaultIcon == null)
                defaultIcon = toolBarIcon.getDrawable();
            toolBarIcon.setImageDrawable(newFragment.icon);
        } else if (defaultIcon != null)
            toolBarIcon.setImageDrawable(defaultIcon);
    }

    @Override
    public void onBackPressed() {
        if (!(FragmentBase.last instanceof TOSFragment))
            super.onBackPressed();
    }

    boolean infocardOpen = false;
    public void OpenInfoCard(String message) { OpenInfoCard(message, false);}
    public void OpenInfoCard(String message, boolean instant) { // TODO: Set info card opening animation
        infocard.setText(message);
        if (!infocardOpen) {

            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)infocardContainer.getLayoutParams();
            params.leftMargin = -params.width;
            infocardContainer.setLayoutParams(params);
            infocardOpen = true;
            viewOverlay.setVisibility(View.VISIBLE);
        }
    }
    public void CloseInfoCard() { // TODO: Set info card closing animation
        if (infocardOpen) {
            infocard.setText("");
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)infocardContainer.getLayoutParams();
            params.leftMargin = 0;
            infocardContainer.setLayoutParams(params);
            infocardOpen = false;
            viewOverlay.setVisibility(View.GONE);
        }
    }
    boolean torialOpen = false;
    public boolean TryOpenTorial(HelpVideo video) {
        if (video.autoplay) {
            OpenTorial(video);
            return true;
        }
        return false;
    }
    public void OpenTorial(HelpVideo video) { OpenTorial(video, false);}
    public void OpenTorial(HelpVideo video, boolean instant) { // TODO: Set tutorial opening animation
        openTorial = video;
        torialSwitch.setChecked(video.autoplay);
        torialVideo.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" +
                video.video));
        torialVideo.start();
        torialText.setText(video.name);
        if (!torialOpen) {
            torialContainer.setVisibility(View.VISIBLE);
            torialOpen = true;
        }
    }
    public void CloseTorial() { // TODO: Set tutorial closing animation
        if (torialOpen) {
            torialVideo.pause();
            torialContainer.setVisibility(View.GONE);
            torialOpen = false;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("info_open",infocardOpen);
        outState.putString("info_message",infocard.getText().toString());
        outState.putBoolean("torial_open",torialOpen);
        outState.putInt("torial_video",torialVideo.getCurrentPosition());
        outState.putBoolean("torial_videoing",torialVideo.isPlaying());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        TryGetValue(savedInstanceState,"info_open", (Boolean x) -> {
            if (x)
                TryGetValue(savedInstanceState,"info_message",(String y) -> OpenInfoCard(y,true));
        });
        TryGetValue(savedInstanceState,"torial_open", (Boolean x) -> {
            if (x)
            {
                OpenTorial(openTorial,true);
                TryGetValue(savedInstanceState,"torial_videoing",(Boolean y) -> {
                   if (y)
                       torialVideo.start();
                   else
                       torialVideo.pause();
                });
                TryGetValue(savedInstanceState,"torial_video",(Integer y) -> torialVideo.seekTo(y));
            }
        });
    }

    public static class Settings {
        public static boolean agreedToToS = false;
    }

    public static class Utils {
        public static <T> boolean Any(T[] collection, Predicate<T> condition) {
            for (T i : collection)
                if (condition.test(i))
                    return true;
            return false;
        }

        public static boolean IsWhitespace(String value) {
            for (int i = 0; i < value.length(); i++)
                if (!Character.isWhitespace(value.charAt(i)))
                    return false;
            return true;
        }

        public static void RunAsync(Runnable code) {
            ResourceManager.Instance().RunAsync(code);
        }
        public static <T> boolean TryGetValue(Bundle State, String Key, Action<T> OnSuccess) {
            if (State == null)
                return false;
            T result;
            try {
                result = (T) State.get(Key);
            } catch (Exception e) {
                Log.e("Bundle:TryGetValue",Key + " - " + e);
                return false;
            }
            OnSuccess.invoke(result);
            return true;
        }
    }
}
