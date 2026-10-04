package com.example.grampanchayatjambhai.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.grampanchayatjambhai.R;

public class ThemeManager {

    private static final String PREF_NAME = "grampanchayat_theme_prefs";
    private static final String KEY_NIGHT_MODE = "key_night_mode"; // 0: System, 1: Light, 2: Dark
    private static final String KEY_COLOR_THEME = "key_color_theme"; // 0 to 11

    public static final int MODE_SYSTEM = 0;
    public static final int MODE_LIGHT = 1;
    public static final int MODE_DARK = 2;

    public static final String[] THEME_NAMES = new String[]{
            "१. ग्रामपंचायत हिरवा (Civic Green)",
            "२. रॉयल निळा (Royal Blue)",
            "३. भगवा / केसरी (Saffron Orange)",
            "४. जांभळा (Deep Purple)",
            "५. मोरपिंकी / टील (Teal Cyan)",
            "६. लाल / क्रिमसन (Crimson Red)",
            "७. इंडिगो (Indigo Night)",
            "८. पाचू / एमराल्ड (Emerald Mint)",
            "९. सुवर्ण / अंबर (Golden Amber)",
            "१०. कोकम / मॅरून (Kokum Maroon)",
            "११. समुद्र निळा (Ocean Blue)",
            "१२. चारकोल ग्रे (Charcoal Classic)"
    };

    public static final String[] THEME_HEX_COLORS = new String[]{
            "#1B5E20", // Green
            "#0D47A1", // Blue
            "#E65100", // Saffron
            "#4A148C", // Purple
            "#004D40", // Teal
            "#B71C1C", // Red
            "#1A237E", // Indigo
            "#00695C", // Emerald
            "#FF6F00", // Amber
            "#880E4F", // Maroon
            "#0277BD", // Ocean
            "#263238"  // Charcoal
    };

    public static void applyTheme(Activity activity) {
        if (activity == null) return;

        Context context = activity.getApplicationContext();
        int nightMode = getNightMode(context);
        applyNightMode(nightMode);

        int colorTheme = getColorTheme(context);
        activity.setTheme(getThemeStyleRes(colorTheme));
    }

    public static int getNightMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_NIGHT_MODE, MODE_SYSTEM);
    }

    public static void setNightMode(Context context, int mode) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_NIGHT_MODE, mode).apply();
        applyNightMode(mode);
    }

    public static void applyNightMode(int mode) {
        if (mode == MODE_LIGHT) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (mode == MODE_DARK) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    public static int getColorTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(KEY_COLOR_THEME, 0); // Default to Green (0)
    }

    public static void setColorTheme(Context context, int themeIndex) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putInt(KEY_COLOR_THEME, themeIndex).apply();
    }

    public static int getThemeStyleRes(int themeIndex) {
        switch (themeIndex) {
            case 1:
                return R.style.Theme_GrampanchayatJambhai_Blue;
            case 2:
                return R.style.Theme_GrampanchayatJambhai_Saffron;
            case 3:
                return R.style.Theme_GrampanchayatJambhai_Purple;
            case 4:
                return R.style.Theme_GrampanchayatJambhai_Teal;
            case 5:
                return R.style.Theme_GrampanchayatJambhai_Red;
            case 6:
                return R.style.Theme_GrampanchayatJambhai_Indigo;
            case 7:
                return R.style.Theme_GrampanchayatJambhai_Emerald;
            case 8:
                return R.style.Theme_GrampanchayatJambhai_Amber;
            case 9:
                return R.style.Theme_GrampanchayatJambhai_Maroon;
            case 10:
                return R.style.Theme_GrampanchayatJambhai_Ocean;
            case 11:
                return R.style.Theme_GrampanchayatJambhai_Charcoal;
            case 0:
            default:
                return R.style.Theme_GrampanchayatJambhai_Green;
        }
    }
}