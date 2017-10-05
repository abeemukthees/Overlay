
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

package msa.overlay;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import msa.overlay.listener.IncomingCallReceiver;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int CHATHEAD_OVERLAY_PERMISSION_REQUEST_CODE = 100;

    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initializeBubble();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                //addBubble();
                checkOverlayPermission();
            }
        });

        checkBox = findViewById(R.id.checkbox_phone);

        checkBox.setChecked(checkIfPhoneListenerEnabled());

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setPhoneListener(b);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHATHEAD_OVERLAY_PERMISSION_REQUEST_CODE) {
            Log.d(MainActivity.class.getSimpleName(), "Is permission granted =" + resultCode);
            Toast.makeText(this, "Is permission granted =" + resultCode, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NewApi")
    private void checkOverlayPermission() {
        if (Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Permission available", Toast.LENGTH_SHORT).show();

        } else {
            final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CHATHEAD_OVERLAY_PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkIfPhoneListenerEnabled() {
        Log.d(TAG, "checkIfPhoneListenerEnabled");
        ComponentName component = new ComponentName(this, IncomingCallReceiver.class);
        int status = getPackageManager().getComponentEnabledSetting(component);
        if (status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            Log.d(TAG, "receiver is enabled");
            return true;
        } else if (status == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            Log.d(TAG, "receiver is disabled");
            return false;
        } else {
            Log.d(TAG, "receiver is DISABLED");
            return false;
        }
    }

    private void setPhoneListener(boolean shouldEnable) {

        ComponentName component = new ComponentName(this, IncomingCallReceiver.class);

        if (shouldEnable) {
            //Enable
            getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        } else {
            //Disable
            getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        }
    }
}
