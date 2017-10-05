
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

package msa.overlay.listener;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;

import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;
import com.txusballesteros.bubbles.OnInitializedCallback;

import java.util.Date;

import msa.overlay.R;
import msa.overlay.mark1.MarkOneFloatingActivity;
import msa.overlay.mark1.MarkOneService;

/**
 * Created by Abhimuktheeswarar on 05-10-2017.
 */

public class IncomingCallReceiver extends PhonecallReceiver {

    private static final String TAG = IncomingCallReceiver.class.getSimpleName();

    private BubblesManager bubblesManager;

    private Context context;

    private boolean isInitialzed;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        this.context = context;
        //initializeBubble();

    }

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        super.onIncomingCallStarted(ctx, number, start);
        Log.d(TAG, "onIncomingCallStarted -> " + number);
        this.context = ctx;
        context.startService(new Intent(context, MarkOneService.class));
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        super.onOutgoingCallStarted(ctx, number, start);
        Log.d(TAG, "onOutgoingCallStarted -> " + number);
        this.context = ctx;
        context.startService(new Intent(context, MarkOneService.class));
        //if (isInitialzed) addBubble();
        //else initializeBubble();

    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        super.onIncomingCallEnded(ctx, number, start, end);
        Log.d(TAG, "onIncomingCallEnded -> " + number);
        this.context = ctx;
        context.stopService(new Intent(context, MarkOneService.class));
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        super.onOutgoingCallEnded(ctx, number, start, end);
        Log.d(TAG, "onOutgoingCallEnded -> " + number);
        this.context = ctx;
        context.stopService(new Intent(context, MarkOneService.class));
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        super.onMissedCall(ctx, number, start);
        Log.d(TAG, "onMissedCall -> " + number);
        this.context = ctx;
        context.stopService(new Intent(context, MarkOneService.class));
    }

    @Override
    public void onCallStateChanged(Context context, int state, String number) {
        super.onCallStateChanged(context, state, number);
        Log.d(TAG, "onCallStateChanged -> " + state + " = " + number);
    }

    private void initializeBubble() {
        Log.d(TAG, "initializeBubble");
        bubblesManager = new BubblesManager.Builder(context.getApplicationContext())
                .setTrashLayout(R.layout.bubble_trash)
                .setInitializationCallback(new OnInitializedCallback() {
                    @Override
                    public void onInitialized() {
                        isInitialzed = true;
                        Log.d(TAG, "isInitialzed = " + isInitialzed);
                        //addBubble();
                    }
                })
                .build();
        bubblesManager.initialize();
    }

    private void addBubble() {
        Log.d(TAG, "addBubble");
        BubbleLayout bubbleView = (BubbleLayout) LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.bubble_mark_one, null);
        bubbleView.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {

            @Override
            public void onBubbleClick(BubbleLayout bubble) {
                //Toast.makeText(getApplicationContext(), "Clicked !", Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, MarkOneFloatingActivity.class));
            }
        });
        bubbleView.setShouldStickToWall(true);
        bubblesManager.addBubble(bubbleView, 60, 20);
    }
}
