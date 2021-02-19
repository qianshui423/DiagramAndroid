package com.diagram.diagramandroid.sbeauty;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * PermissionCompatDelegate global class
 * support intercept onActivityResult and requestPermissions anywhere as long as you get the activity context.
 */
public class GlobalPermissionCompatDelegate implements ActivityCompat.PermissionCompatDelegate {

    private static final String TAG = "GlobalPermissionCompatD";

    private Application mApp;
    private ActivityCompat.PermissionCompatDelegate mOldDelegate;
    private Map<Activity, List<ActivityCompat.PermissionCompatDelegate>> mGlobalDelegates = new HashMap<>(8);

    public static GlobalPermissionCompatDelegate getInstance() {
        return Singleton.INSTANCE;
    }

    private GlobalPermissionCompatDelegate() {
    }

    private static class Singleton {
        private static final GlobalPermissionCompatDelegate INSTANCE = new GlobalPermissionCompatDelegate();
    }

    /**
     * register delegate to activity
     *
     * @param activity
     * @param delegate
     */
    public void register(Activity activity, ActivityCompat.PermissionCompatDelegate delegate) {
        boolean activityIsActive = checkActive(activity);
        if (!activityIsActive) {
            if (activity == null) {
                Log.d(TAG, "Activity instance is null, so register delegate failed!");
                return;
            }
            Log.d(TAG, String.format(Locale.getDefault(), "Activity(%s) is not active, so register delegate failed!", activity.getClass().getCanonicalName()));
            return;
        }

        List<ActivityCompat.PermissionCompatDelegate> delegatesForActivity = mGlobalDelegates.get(activity);
        if (delegatesForActivity == null) {
            delegatesForActivity = new ArrayList<>();
            mGlobalDelegates.put(activity, delegatesForActivity);
        }
        delegatesForActivity.add(delegate);
        if (mApp == null) {
            // first get app instance
            mApp = activity.getApplication();
            // delete delegate map while activity is destroyed
            mApp.registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {

                @Override
                public void onActivityDestroyed(@NonNull Activity activity) {
                    mGlobalDelegates.remove(activity);
                }
            });

            // save old sDelegates
            try {
                Field sDelegateField = ActivityCompat.class.getDeclaredField("sDelegate");
                sDelegateField.setAccessible(true);
                mOldDelegate = (ActivityCompat.PermissionCompatDelegate) sDelegateField.get(null);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            ActivityCompat.setPermissionCompatDelegate(GlobalPermissionCompatDelegate.getInstance());
        }
    }

    private boolean checkActive(Activity activity) {
        if (activity == null) return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return !activity.isFinishing() && !activity.isDestroyed();
        }
        return !activity.isFinishing();
    }

    @Override
    public final boolean requestPermissions(@NonNull Activity activity, @NonNull String[] permissions, int requestCode) {
        // handle old delegate
        boolean handled = false;
        if (mOldDelegate != null) {
            boolean handledForOldDelegate = mOldDelegate.requestPermissions(activity, permissions, requestCode);
            if (handledForOldDelegate) {
                handled = true;
            }
        }

        // handle append delegate
        List<ActivityCompat.PermissionCompatDelegate> targetDelegates = mGlobalDelegates.get(activity);
        if (targetDelegates == null || targetDelegates.isEmpty()) {
            return handled;
        }
        for (ActivityCompat.PermissionCompatDelegate delegate : targetDelegates) {
            boolean handledElement = delegate.requestPermissions(activity, permissions, requestCode);
            if (handledElement) {
                handled = true;
            }
        }
        return handled;
    }

    @Override
    public final boolean onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
        // handle old delegate
        boolean handled = false;
        if (mOldDelegate != null) {
            boolean handledForOldDelegate = mOldDelegate.onActivityResult(activity, requestCode, resultCode, data);
            if (handledForOldDelegate) {
                handled = true;
            }
        }

        // handle append delegate
        List<ActivityCompat.PermissionCompatDelegate> targetDelegates = mGlobalDelegates.get(activity);
        if (targetDelegates == null || targetDelegates.isEmpty()) {
            return false;
        }
        for (ActivityCompat.PermissionCompatDelegate delegate : targetDelegates) {
            boolean handledElement = delegate.onActivityResult(activity, requestCode, resultCode, data);
            if (handledElement) {
                handled = true;
            }
        }
        return handled;
    }

    private static class SimpleActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        @Override
        public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {
        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {
        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
        }
    }
}
