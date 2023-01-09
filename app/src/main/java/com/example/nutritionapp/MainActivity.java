package com.example.nutritionapp;

import static com.example.nutritionapp.other.Utils.navigate;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.nutritionapp.deprecated.Recommendations;
import com.example.nutritionapp.other.Database;
import com.example.nutritionapp.other.LocaleHelper;
import com.example.nutritionapp.ui.AboutPageFragment;
import com.example.nutritionapp.ui.ConfigurationFragment;
import com.example.nutritionapp.ui.CustomFoodFragment;
import com.example.nutritionapp.ui.JournalFragment;
import com.example.nutritionapp.ui.RecommendationFragment;
import com.example.nutritionapp.ui.StartPageFragment;
import com.example.nutritionapp.ui.WeightTrackingFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    private Database db;


    public  MainActivity(){
        super(R.layout.app_activity_main);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.main_fragment_container, StartPageFragment.class , null)
                    .commit();
        }

        db = new Database(this);

        /* Setup Toolbar */
        Toolbar toolbar = findViewById(R.id.toolbar);
        (this).setSupportActionBar(toolbar);


        /* drawer (navigation sidebar) */
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_foodJournal) {
                navigate(JournalFragment.class, this);
            } else if (itemId == R.id.nav_configuration) {
                navigate(ConfigurationFragment.class, this);
            } else if (itemId == R.id.nav_customFoods) {
                navigate(CustomFoodFragment.class, this);
            } else if (itemId == R.id.nav_analysis) {
                navigate(RecommendationFragment.class, this);
            } else if (itemId == R.id.startPageFragment) {
                navigate(StartPageFragment.class, this);
            } else if (itemId == R.id.aboutPageFragment) {
                navigate(AboutPageFragment.class, this);
            } else if (itemId == R.id.nav_weight_tracking) {
                navigate(WeightTrackingFragment.class, this);
            } else {
                Log.wtf("Click", "Unknown ID for onNavigationItemSelected: " + item.getItemId());
            }

            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /* Night Mode */
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        View navHeader = navigationView.getHeaderView(0);
        ImageButton switchToNight = navHeader.findViewById(R.id.switchToNight);
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            switchToNight.setImageResource(R.mipmap.ic_sun_foreground);
        }
        switchToNight.setOnClickListener(v -> {
            switch (currentNightMode) {
                case Configuration.UI_MODE_NIGHT_NO:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
                case Configuration.UI_MODE_NIGHT_YES:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
                default:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        });

        /* try to set language according to db, if empty set db to system-default */
        String savedLanguage = db.getLanguagePref();
        if (savedLanguage != null) LocaleHelper.setDefaultLanguage(this, savedLanguage);
        else db.setLanguagePref(LocaleHelper.getLanguage(this));

        /* Language */
        String currentLanguage = LocaleHelper.getLanguage(this);
        Button setLanguage = navHeader.findViewById(R.id.languageSelect);
        setLanguage.setText(R.string.otherLanguage);
        setLanguage.setOnClickListener(v -> {
            switch (currentLanguage) {
                case "en":
                    LocaleHelper.setLocale(this, "de");
                    db.setLanguagePref("de");
                    this.recreate();
                    break;
                case "de":
                    LocaleHelper.setLocale(this, "en");
                    db.setLanguagePref("en");
                    this.recreate();
                    break;
            }
        });

        //todo sync languagechange from configurationFragment
    }


    /* toggle navigation sidebar */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void attachBaseContext(Context base){
        super.attachBaseContext(LocaleHelper.setDefaultLanguage(base));
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        recreate();
    }

//        /* try to set language according to db, if empty set db to system-default */
//        String savedLanguage = db.getLanguagePref();
//        if (savedLanguage != null) LocaleHelper.setDefaultLanguage(this, savedLanguage);
//        else db.setLanguagePref(LocaleHelper.getLanguage(this));

//        /* Language */
//        String currentLanguage = LocaleHelper.getLanguage(this);
//        Button setLanguage = navHeader.findViewById(R.id.languageSelect);
//        setLanguage.setText(R.string.otherLanguage);
//        setLanguage.setOnClickListener(v -> {
//            switch (currentLanguage) {
//                case "en":
//                    LocaleHelper.setLocale(this, "de");
//                    db.setLanguagePref("de");
//                    this.recreate();
//                    break;
//                case "de":
//                    LocaleHelper.setLocale(this, "en");
//                    db.setLanguagePref("en");
//                    this.recreate();
//                    break;
//            }
//        });
//
//        /* Listen for LanguageChanged-notification from PersonalInformation */
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("LANGUAGE_CHANGED");
//        registerReceiver(broadcastReceiver, filter);
//    }
//
//
//
//    protected void onResume() {
//        super.onResume();
//        Recommendations.setProgressBar(currentDateParsed, this.db, this.energyBar, this.energyBarText, this);
//        Pair<PieData, ArrayList<Integer>> pieAndListData = Recommendations.generatePieChartContent(currentDateParsed, this.db, this.colors);
//        pieAndListData.first.setDrawValues(false);
//        pieChart.setData(pieAndListData.first);
//        pieChart.invalidate();
//        RecyclerView.Adapter<?> adapter = new RecommendationProteinListAdapter(getApplicationContext(), pieAndListData.first, pieAndListData.second);
//        chartList.setAdapter(adapter);
//    }
//


}
