/* Copyright (c) 2019 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.chromium.components.browser_ui.settings;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import android.os.Build;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.content.Intent;

import org.chromium.base.ContextUtils;

public class BravePreferenceFragment extends PreferenceFragmentCompat {
    private static final String CHROME_TABBED_ACTIVITY_CLASS_NAME = "org.chromium.chrome.browser.ChromeTabbedActivity";
    protected static final int STORAGE_PERMISSION_EXPORT_REQUEST_CODE = 8000;
    protected static final int STORAGE_PERMISSION_IMPORT_REQUEST_CODE = STORAGE_PERMISSION_EXPORT_REQUEST_CODE + 1;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.exit_settings_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.close_menu_id) {
            // TODO(samartnik): double check that is works properly
            try {
                Intent intent = new Intent(getActivity(), Class.forName(CHROME_TABBED_ACTIVITY_CLASS_NAME));
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                // Fallback on activity finish
                getActivity().finish();
            }
        }
        return false;
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
    }

    protected boolean isStoragePermissionGranted(boolean isExport) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Context context = ContextUtils.getApplicationContext();
            if (context.checkSelfPermission(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                requestPermissions(
                        new String[] { android.Manifest.permission.WRITE_EXTERNAL_STORAGE },
                        isExport ? STORAGE_PERMISSION_EXPORT_REQUEST_CODE : STORAGE_PERMISSION_IMPORT_REQUEST_CODE);
                return false;
            }
        }
        return true;
    }
}
