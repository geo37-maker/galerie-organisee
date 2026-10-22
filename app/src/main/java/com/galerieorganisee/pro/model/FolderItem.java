package com.galerieorganisee.pro.storage;

import android.content.Context;
import android.content.SharedPreferences;
import com.galerieorganisee.pro.model.FolderItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FolderRepository {
    private static final String PREF_NAME = "galerie_prefs";
    private static final String KEY_FOLDERS = "folders";
    private static final String KEY_MAX_LEVELS = "max_levels";

    private SharedPreferences prefs;
    private Gson gson;

    public FolderRepository(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public List<FolderItem> getFolders() {
        String json = prefs.getString(KEY_FOLDERS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<ArrayList<FolderItem>>() {}.getType();
        List<FolderItem> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    public void saveFolders(List<FolderItem> folders) {
        String json = gson.toJson(folders);
        prefs.edit().putString(KEY_FOLDERS, json).apply();
    }

    public int getMaxLevels() {
        return prefs.getInt(KEY_MAX_LEVELS, 3);
    }

    public void setMaxLevels(int maxLevels) {
        prefs.edit().putInt(KEY_MAX_LEVELS, maxLevels).apply();
    }
}
