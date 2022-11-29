package com.filenko.conspect.common;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RootDirectory {
    private final Context context;
    private final List<String> paths = new ArrayList<>();

    public RootDirectory(Context context) {
        this.context = context;

        this.paths.add("database");
        this.paths.add("json");

    }

    private void checkPath(List<String> paths) {
        for(String strPath : paths) {
            createFolders(strPath);
        }
    }

    private boolean createFolders(String nameFolder) {
        File dir = this.context.getExternalFilesDir(null);
        File fl = new File(dir.getPath()+File.separator+nameFolder);
        return fl.mkdirs();
    }


}
