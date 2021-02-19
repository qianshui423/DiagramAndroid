package com.diagram.diagramandroid;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.diagram.diagramandroid.sbeauty.GlobalPermissionCompatDelegate;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ActivityCompat.setPermissionCompatDelegate(new ActivityCompat.PermissionCompatDelegate() {
            @Override
            public boolean requestPermissions(@NonNull Activity activity, @NonNull String[] permissions, int requestCode) {
                return false;
            }

            @Override
            public boolean onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
                return false;
            }
        });
    }
}
