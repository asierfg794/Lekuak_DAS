package com.example.lekuak.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.lekuak.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference idiomaPref = findPreference("cambiar_idioma_pref");
        if (idiomaPref != null) {
            idiomaPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    LocaleListCompat currentLocale = AppCompatDelegate.getApplicationLocales();
                    if (currentLocale.isEmpty() || currentLocale.get(0).getLanguage().equals("es")) {
                        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("eu"));
                    } else {
                        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("es"));
                    }
                    return true;
                }
            });
        }

        Preference infoPref = findPreference("info_app_pref");
        if (infoPref != null) {
            infoPref.setTitle(leerTxt(requireContext(), R.raw.info_app));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ("modo_oscuro_activado".equals(key)) {
            boolean isChecked = sharedPreferences.getBoolean(key, false);
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }

    private String leerTxt(Context context, int resourceId) {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = context.getResources().openRawResource(resourceId);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                stringBuilder.append(linea).append("\n");
            }
        } catch (Exception e) {
            return "Error al cargar la información.";
        }
        return stringBuilder.toString();
    }
}