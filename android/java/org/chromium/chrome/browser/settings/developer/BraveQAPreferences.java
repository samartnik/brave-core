/* Copyright (c) 2020 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.chromium.chrome.browser.settings.developer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;

import org.chromium.base.ContextUtils;
import org.chromium.base.FileUtils;
import org.chromium.base.Log;
import org.chromium.chrome.R;
import org.chromium.chrome.browser.BraveConfig;
import org.chromium.chrome.browser.BraveRelaunchUtils;
import org.chromium.chrome.browser.BraveRewardsNativeWorker;
import org.chromium.chrome.browser.BraveRewardsObserver;
import org.chromium.chrome.browser.BraveRewardsPanelPopup;
import org.chromium.chrome.browser.preferences.BravePrefServiceBridge;
import org.chromium.chrome.browser.settings.BravePreferenceFragment;
import org.chromium.chrome.browser.util.BraveDbUtil;
import org.chromium.components.browser_ui.settings.ChromeSwitchPreference;
import org.chromium.components.browser_ui.settings.SettingsUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * Settings fragment containing preferences for QA team.
 */
public class BraveQAPreferences extends BravePreferenceFragment
        implements OnPreferenceChangeListener, BraveRewardsObserver {
    private static final String PREF_USE_REWARDS_STAGING_SERVER = "use_rewards_staging_server";
    private static final String PREF_QA_MAXIMIZE_INITIAL_ADS_NUMBER =
            "qa_maximize_initial_ads_number";
    private static final String PREF_QA_DEBUG_NTP= "qa_debug_ntp";

    private static final String QA_ADS_PER_HOUR = "qa_ads_per_hour";
    private static final String QA_IMPORT_REWARDS_DB = "qa_import_rewards_db";
    private static final String QA_EXPORT_REWARDS_DB = "qa_export_rewards_db";
    private static final String QA_RESTORE_WALLET = "qa_restore_wallet";

    private static final int CHOOSE_FILE_FOR_IMPORT_REQUEST_CODE = STORAGE_PERMISSION_IMPORT_REQUEST_CODE + 1;

    private static final int MAX_ADS = 50;
    private static final int DEFAULT_ADS_PER_HOUR = 2;

    private ChromeSwitchPreference mIsStagingServer;
    private ChromeSwitchPreference mMaximizeAdsNumber;
    private ChromeSwitchPreference mDebugNTP;
    private Preference mRestoreWallet;

    private Preference mImportRewardsDb;
    private Preference mExportRewardsDb;
    private BraveDbUtil mDbUtil;
    private String mFileToImport;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsUtils.addPreferencesFromResource(this, R.xml.qa_preferences);

        mIsStagingServer = (ChromeSwitchPreference) findPreference(PREF_USE_REWARDS_STAGING_SERVER);
        if (mIsStagingServer != null) {
            mIsStagingServer.setOnPreferenceChangeListener(this);
        }

        mMaximizeAdsNumber =
                (ChromeSwitchPreference) findPreference(PREF_QA_MAXIMIZE_INITIAL_ADS_NUMBER);
        if (mMaximizeAdsNumber != null) {
            mMaximizeAdsNumber.setEnabled(mIsStagingServer.isChecked());
            mMaximizeAdsNumber.setOnPreferenceChangeListener(this);
        }

        mDebugNTP = (ChromeSwitchPreference) findPreference(PREF_QA_DEBUG_NTP);
        if(mDebugNTP != null) {
            mDebugNTP.setOnPreferenceChangeListener(this);
        }

        mDbUtil = BraveDbUtil.getInstance();

        mImportRewardsDb = findPreference(QA_IMPORT_REWARDS_DB);
        mExportRewardsDb = findPreference(QA_EXPORT_REWARDS_DB);
        setRewardsDbClickListeners();

        mRestoreWallet = findPreference(QA_RESTORE_WALLET);
        setRestoreClickListener();

        checkQACode();
    }

    private void setRestoreClickListener() {
        if (mRestoreWallet != null) {
            mRestoreWallet.setOnPreferenceClickListener(preference -> {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.qa_code_check, null);
                EditText input = (EditText) view.findViewById(R.id.qa_code);
                input.setInputType(InputType.TYPE_CLASS_TEXT);

                DialogInterface.OnClickListener onClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int button) {
                                if (button == AlertDialog.BUTTON_POSITIVE) {
                                    String restorePhrase = input.getText().toString();
                                    if (!restorePhrase.isEmpty()) {
                                        BraveRewardsNativeWorker.getInstance().RecoverWallet(
                                                restorePhrase);
                                    }
                                }
                            }
                        };

                AlertDialog.Builder alert =
                        new AlertDialog.Builder(getActivity(), R.style.Theme_Chromium_AlertDialog);
                AlertDialog.Builder alertDialog =
                        alert.setTitle("Enter Wallet restore phrase")
                                .setView(view)
                                .setPositiveButton(R.string.ok, onClickListener)
                                .setNegativeButton(R.string.cancel, onClickListener)
                                .setCancelable(false);
                Dialog dialog = alertDialog.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                return true;
            });
        }
    }

    private void setRewardsDbClickListeners() {
        if (mImportRewardsDb != null) {
            mImportRewardsDb.setOnPreferenceClickListener( preference -> {
                Intent intent = new Intent()
                        .setType("*/*")
                        .setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select a file"), CHOOSE_FILE_FOR_IMPORT_REQUEST_CODE);
                return true;
            });
        }

        if (mExportRewardsDb != null) {
            mExportRewardsDb.setOnPreferenceClickListener( preference -> {
                if (isStoragePermissionGranted(true)) {
                    requestRestart(false);
                }
                return true;
            });
        }
    }

    @Override
    public void onStart() {
        BraveRewardsNativeWorker.getInstance().AddObserver(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        BraveRewardsNativeWorker.getInstance().RemoveObserver(this);
        super.onStop();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (PREF_USE_REWARDS_STAGING_SERVER.equals(preference.getKey())) {
            BravePrefServiceBridge.getInstance().setUseRewardsStagingServer((boolean) newValue);
            BraveRewardsNativeWorker.getInstance().ResetTheWholeState();
            mMaximizeAdsNumber.setEnabled((boolean) newValue);
            enableMaximumAdsNumber(((boolean) newValue) && mMaximizeAdsNumber.isChecked());
            BraveRelaunchUtils.askForRelaunch(getActivity());
        } else if (PREF_QA_MAXIMIZE_INITIAL_ADS_NUMBER.equals(preference.getKey())) {
            enableMaximumAdsNumber((boolean) newValue);
        } else if (PREF_QA_DEBUG_NTP.equals(preference.getKey())) {
            setOnPreferenceValue(preference.getKey(), (boolean)newValue);
            BraveRelaunchUtils.askForRelaunch(getActivity());
        }
        return true;
    }

    private void setOnPreferenceValue(String preferenceName, boolean newValue) {
        SharedPreferences sharedPreferences = ContextUtils.getAppSharedPreferences();
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(preferenceName, newValue);
        sharedPreferencesEditor.apply();
    }

    private void checkQACode() {
        LayoutInflater inflater =
                (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.qa_code_check, null);
        final EditText input = (EditText) view.findViewById(R.id.qa_code);

        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                if (button != AlertDialog.BUTTON_POSITIVE
                        || !input.getText().toString().equals(BraveConfig.DEVELOPER_OPTIONS_CODE)) {
                    getActivity().finish();
                }
            }
        };

        input.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                input.post(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager inputMethodManager =
                                (InputMethodManager) getActivity().getSystemService(
                                        Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
            }
        });
        input.requestFocus();

        AlertDialog.Builder alert =
                new AlertDialog.Builder(getActivity(), R.style.Theme_Chromium_AlertDialog);
        if (alert == null) {
            return;
        }
        AlertDialog.Builder alertDialog = alert
                .setTitle("Enter QA code")
                .setView(view)
                .setPositiveButton(R.string.ok, onClickListener)
                .setNegativeButton(R.string.cancel, onClickListener)
                .setCancelable(false);
        Dialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void enableMaximumAdsNumber(boolean enable) {
        if (enable) {
            // Save current values
            int adsPerHour = BraveRewardsNativeWorker.getInstance().GetAdsPerHour();
            SharedPreferences sharedPreferences = ContextUtils.getAppSharedPreferences();
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putInt(QA_ADS_PER_HOUR, adsPerHour);
            sharedPreferencesEditor.apply();
            // Set max value
            BraveRewardsNativeWorker.getInstance().SetAdsPerHour(MAX_ADS);
            return;
        }
        // Set saved values
        int adsPerHour = ContextUtils.getAppSharedPreferences().getInt(
                QA_ADS_PER_HOUR, DEFAULT_ADS_PER_HOUR);
        BraveRewardsNativeWorker.getInstance().SetAdsPerHour(adsPerHour);
    }

    @Override
    public void OnResetTheWholeState(boolean success) {
        if (success) {
            SharedPreferences sharedPreferences = ContextUtils.getAppSharedPreferences();
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putBoolean(BraveRewardsPanelPopup.PREF_GRANTS_NOTIFICATION_RECEIVED, false);
            sharedPreferencesEditor.putBoolean(BraveRewardsPanelPopup.PREF_WAS_BRAVE_REWARDS_TURNED_ON, false);
            sharedPreferencesEditor.apply();
            BravePrefServiceBridge.getInstance().setSafetynetCheckFailed(false);
            BraveRelaunchUtils.askForRelaunch(getActivity());
        } else {
            BraveRelaunchUtils.askForRelaunchCustom(getActivity());
        }
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {}

    @Override
    public void OnRecoverWallet(int errorCode) {
        Context context = ContextUtils.getApplicationContext();
        String msg =
                (0 == errorCode) ? "Wallet is successfully restored" : "Wallet recovery failed";
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (STORAGE_PERMISSION_EXPORT_REQUEST_CODE == requestCode) {
                requestRestart(false);
            } else if (STORAGE_PERMISSION_IMPORT_REQUEST_CODE == requestCode) {
                requestRestart(true);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_FILE_FOR_IMPORT_REQUEST_CODE && resultCode == Activity.RESULT_OK
                && data != null) {
            try {
                InputStream in =
                        ContextUtils.getApplicationContext().getContentResolver().openInputStream(
                                data.getData());
                mFileToImport = mDbUtil.importDestinationPath() + ".prep";
                FileUtils.copyStreamToFile(in, new File(mFileToImport));
                in.close();
            } catch (IOException e) {
                Log.e(BraveDbUtil.getTag(), "Error on preparing database file: " + e);
                return;
            }
            requestRestart(true);
        }
    }

    private void requestRestart(boolean isImport) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int button) {
                if (button == AlertDialog.BUTTON_POSITIVE) {
                    if (isImport) {
                        mDbUtil.setPerformDbImportOnStart(true);
                        mDbUtil.setDbImportFile(mFileToImport);
                    } else {
                        mDbUtil.setPerformDbExportOnStart(true);
                    }
                    BraveRelaunchUtils.restart();
                } else {
                    mDbUtil.cleanUpDbOperationRequest();
                }
            }
        };
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity(), R.style.Theme_Chromium_AlertDialog)
                .setMessage(
                        "This operation requires restart. Would you like to restart application and start operation?")
                .setPositiveButton(R.string.ok, onClickListener).setNegativeButton(R.string.cancel, onClickListener);
        Dialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
