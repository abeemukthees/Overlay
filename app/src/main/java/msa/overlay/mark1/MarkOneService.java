
/*
 * Copyright 2017, Abhi Muktheeswarar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package msa.overlay.mark1;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import jp.co.recruit_lifestyle.android.floatingview.FloatingViewListener;
import jp.co.recruit_lifestyle.android.floatingview.FloatingViewManager;
import msa.overlay.R;

public class MarkOneService extends Service implements FloatingViewListener {

    private static final String TAG = MarkOneService.class.getSimpleName();

    private FloatingViewManager mFloatingViewManager;

    public MarkOneService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        if (!checkOverlayPermission()) return START_STICKY_COMPATIBILITY;


        if (mFloatingViewManager != null) {
            return START_STICKY;
        }

        final DisplayMetrics metrics = new DisplayMetrics();
        final WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        final LayoutInflater inflater = LayoutInflater.from(this);
        final FrameLayout iconView = (FrameLayout) inflater.inflate(R.layout.widget_chathead, null, false);
        final ImageView imageView = iconView.findViewById(R.id.image_add);
        imageView.setImageResource(R.drawable.ic_add_white);
        iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick");
                Intent markOneCallActivityIntent = new Intent(MarkOneService.this, MarkOneCallActivity.class);
                markOneCallActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(markOneCallActivityIntent);
                stopSelf();

            }
        });

        mFloatingViewManager = new FloatingViewManager(this, this);
        mFloatingViewManager.setFixedTrashIconImage(R.drawable.ic_head_close);
        //mFloatingViewManager.setActionTrashIconImage(R.drawable.ic_trash_action);
        final FloatingViewManager.Options options = new FloatingViewManager.Options();
        options.overMargin = (int) (-4 * metrics.density);
        options.animateInitialMove = true;
        final int offset = (int) (48 + 8 * metrics.density);
        options.floatingViewX = (metrics.widthPixels - offset);
        options.floatingViewY = (int) (metrics.heightPixels * 0.5 - offset);
        mFloatingViewManager.addViewToWindow(iconView, options);

        // 常駐起動
        //startForeground(NOTIFICATION_ID, createNotification());

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onFinishFloatingView() {
        stopSelf();

    }

    @Override
    public void onTouchFinished(boolean isFinishing, int x, int y) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (mFloatingViewManager != null) {
            mFloatingViewManager.removeAllViewToWindow();
            mFloatingViewManager = null;
        }

    }

    @SuppressLint("NewApi")
    private boolean checkOverlayPermission() {
        return Settings.canDrawOverlays(getApplicationContext());

    }


}
