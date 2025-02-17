/* Copyright (c) 2021 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/. */

package org.chromium.chrome.browser.settings;

import android.app.Activity;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.View;

import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;

import org.chromium.base.Log;
import org.chromium.base.supplier.ObservableSupplier;
import org.chromium.base.supplier.ObservableSupplierImpl;
import org.chromium.base.task.PostTask;
import org.chromium.base.task.TaskTraits;
import org.chromium.build.annotations.Nullable;
import org.chromium.brave.browser.search_engines.settings.CustomSearchEnginesPreference;
import org.chromium.brave.browser.search_engines.settings.CustomSearchEnginesUtil;
import org.chromium.chrome.R;
import org.chromium.chrome.browser.BraveConfig;
import org.chromium.chrome.browser.preferences.BravePref;
import org.chromium.chrome.browser.preferences.Pref;
import org.chromium.components.browser_ui.settings.ChromeSwitchPreference;
import org.chromium.components.browser_ui.settings.SettingsUtils;
import org.chromium.components.user_prefs.UserPrefs;
import org.chromium.components.web_discovery.WebDiscoveryPrefs;

public class BraveSearchEnginesPreferences extends BravePreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    private static final String PREF_STANDARD_SEARCH_ENGINE = "standard_search_engine";
    private static final String PREF_PRIVATE_SEARCH_ENGINE = "private_search_engine";

    private static final String PREF_SEARCH_SUGGESTIONS = "search_suggestions";
    private static final String PREF_SHOW_AUTOCOMPLETE_IN_ADDRESS_BAR =
            "show_autocomplete_in_address_bar";
    private static final String PREF_SEND_WEB_DISCOVERY = "send_web_discovery";

    private static final String PREF_CUSTOM_SEARCH_ENGINES_CATEGORY =
            "custom_search_engines_category";
    private static final String PREF_CUSTOM_SEARCH_ENGINE_LIST = "custom_search_engine_list";

    private ChromeManagedPreferenceDelegate mManagedPreferenceDelegate;

    private ChromeSwitchPreference mShowAutocompleteInAddressBar;
    private ChromeSwitchPreference mSearchSuggestions;
    private @Nullable ChromeSwitchPreference mSendWebDiscovery;

    private final ObservableSupplierImpl<String> mPageTitle = new ObservableSupplierImpl<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageTitle.set(getString(R.string.brave_search_engines));
        SettingsUtils.addPreferencesFromResource(this, R.xml.brave_search_engines_preferences);
    }

    @Override
    public ObservableSupplier<String> getPageTitle() {
        return mPageTitle;
    }

    @Override
    public void onResume() {
        super.onResume();
        // updateSearchEnginePreference method does a lot of preference finding,
        // listener setting, and state updates. There are native callse inside.
        // Defer it's execution to ensure that preference screen is fully
        // inflated before complex updates.
        PostTask.postTask(TaskTraits.UI_DEFAULT, this::updateSearchEnginePreference);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        mManagedPreferenceDelegate = createManagedPreferenceDelegate();
    }

    private void removePreferenceIfPresent(String key) {
        Preference preference = getPreferenceScreen().findPreference(key);
        if (preference != null) {
            getPreferenceScreen().removePreference(preference);
        }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        PreferenceCategory preferenceCategory =
                (PreferenceCategory) findPreference(PREF_CUSTOM_SEARCH_ENGINES_CATEGORY);
        if (preferenceCategory != null
                && CustomSearchEnginesUtil.getCustomSearchEngines().size() > 0) {
            CustomSearchEnginesPreference customSearchEnginesPreference =
                    new CustomSearchEnginesPreference(requireContext());
            customSearchEnginesPreference.initialize(getProfile());
            customSearchEnginesPreference.setKey(PREF_CUSTOM_SEARCH_ENGINE_LIST);
            customSearchEnginesPreference.setOrder(1);
            preferenceCategory.addPreference(customSearchEnginesPreference);
        }
    }

    private ChromeManagedPreferenceDelegate createManagedPreferenceDelegate() {
        return new ChromeManagedPreferenceDelegate(getProfile()) {
            @Override
            public boolean isPreferenceControlledByPolicy(Preference preference) {
                String key = preference.getKey();
                if (PREF_SEARCH_SUGGESTIONS.equals(key)) {
                    return UserPrefs.get(getProfile())
                            .isManagedPreference(Pref.SEARCH_SUGGEST_ENABLED);
                }
                return false;
            }
        };
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateSearchEnginePreference() {
        // Check if fragment is still attached before updating preferences
        Activity activity = getActivity();
        if (activity == null || !isAdded() || activity.isFinishing() || getProfile() == null) {
            return;
        }

        Preference searchEnginePreference = findPreference(PREF_STANDARD_SEARCH_ENGINE);
        if (searchEnginePreference != null) {
            searchEnginePreference.setEnabled(true);
            searchEnginePreference.setSummary(
                    BraveSearchEngineUtils.getDSEShortName(getProfile(), false));
        }

        searchEnginePreference = findPreference(PREF_PRIVATE_SEARCH_ENGINE);
        if (searchEnginePreference != null) {
            searchEnginePreference.setEnabled(true);
            searchEnginePreference.setSummary(
                    BraveSearchEngineUtils.getDSEShortName(
                            getProfile().getPrimaryOtrProfile(/* createIfNeeded= */ true), true));
        }

        mSearchSuggestions = (ChromeSwitchPreference) findPreference(PREF_SEARCH_SUGGESTIONS);
        if (mSearchSuggestions != null) {
            mSearchSuggestions.setOnPreferenceChangeListener(this);
            mSearchSuggestions.setManagedPreferenceDelegate(mManagedPreferenceDelegate);
        }

        mShowAutocompleteInAddressBar =
                (ChromeSwitchPreference) findPreference(PREF_SHOW_AUTOCOMPLETE_IN_ADDRESS_BAR);
        if (mShowAutocompleteInAddressBar != null) {
            mShowAutocompleteInAddressBar.setOnPreferenceChangeListener(this);
        }

        boolean autocompleteEnabled =
                UserPrefs.get(getProfile()).getBoolean(BravePref.AUTOCOMPLETE_ENABLED);
        if (mSearchSuggestions != null) {
            mSearchSuggestions.setVisible(autocompleteEnabled);
        }

        if (mShowAutocompleteInAddressBar != null) {
            mShowAutocompleteInAddressBar.setChecked(autocompleteEnabled);
        }
        if (mSearchSuggestions != null) {
            mSearchSuggestions.setChecked(
                    UserPrefs.get(getProfile()).getBoolean(Pref.SEARCH_SUGGEST_ENABLED));
        }

        if (BraveConfig.WEB_DISCOVERY_ENABLED) {
            // Check if web discovery is managed by policy
            boolean isWebDiscoveryManaged =
                    UserPrefs.get(getProfile())
                            .isManagedPreference(WebDiscoveryPrefs.WEB_DISCOVERY_ENABLED);
            if (!isWebDiscoveryManaged) {
                mSendWebDiscovery =
                        (ChromeSwitchPreference) findPreference(PREF_SEND_WEB_DISCOVERY);
                if (mSendWebDiscovery != null) {
                    mSendWebDiscovery.setOnPreferenceChangeListener(this);
                }
            }
        }

        if (mSendWebDiscovery != null) {
            mSendWebDiscovery.setTitle(
                    activity.getResources().getString(R.string.send_web_discovery_title));
            mSendWebDiscovery.setSummary(
                    activity.getResources().getString(R.string.send_web_discovery_summary));
            mSendWebDiscovery.setChecked(
                    UserPrefs.get(getProfile())
                            .getBoolean(WebDiscoveryPrefs.WEB_DISCOVERY_ENABLED));
        } else {
            removePreferenceIfPresent(PREF_SEND_WEB_DISCOVERY);
        }
        // Preference customSearchEnginePreference =
        // getPreferenceScreen().findPreference(PREF_CUSTOM_SEARCH_ENGINES);
        // if (customSearchEnginePreference != null) {
        //     getPreferenceScreen().removePreference(customSearchEnginePreference);
        // }

        // Preference customSearchEnginePreference2 =
        // getPreferenceScreen().findPreference(PREF_CUSTOM_SEARCH_ENGINES);
        // if (customSearchEnginePreference2 != null) {
        //     getPreferenceScreen().addPreference(customSearchEnginePreference2);
        // }

        // Preference customPreference = findPreference(PREF_CUSTOM_SEARCH_ENGINES);
        // if (customPreference
        //         instanceof
        //         org.chromium.brave.browser.search_engines.settings.CustomSearchEnginesPreference)
        // {
        //     View view = getView();
        //     if (view != null) {
        //         RecyclerView recyclerView = view.findViewById(R.id.custom_search_engine_list);
        //         RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        //         if (adapter != null) {
        //             adapter.notifyDataSetChanged(); // Refresh UI
        //         }
        //     }
        // }

        // CustomSearchEnginesPreference customSearchEnginesPreference =
        //         (CustomSearchEnginesPreference) findPreference(PREF_CUSTOM_SEARCH_ENGINES);
        // customSearchEnginesPreference.initialize(getProfile());

        Preference customSearchEnginesListPreference =
                findPreference(PREF_CUSTOM_SEARCH_ENGINE_LIST);
        if (customSearchEnginesListPreference != null
                && customSearchEnginesListPreference instanceof CustomSearchEnginesPreference) {
            Log.e("brave_search", "updateCustomSearchEngines");
            ((CustomSearchEnginesPreference) customSearchEnginesListPreference)
                    .updateCustomSearchEngines();
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        if (PREF_SEARCH_SUGGESTIONS.equals(key)) {
            UserPrefs.get(getProfile()).setBoolean(Pref.SEARCH_SUGGEST_ENABLED, (boolean) newValue);
        } else if (PREF_SHOW_AUTOCOMPLETE_IN_ADDRESS_BAR.equals(key)) {
            boolean autocompleteEnabled = (boolean) newValue;
            mSearchSuggestions.setVisible(autocompleteEnabled);
            UserPrefs.get(getProfile())
                    .setBoolean(BravePref.AUTOCOMPLETE_ENABLED, autocompleteEnabled);
        } else if (PREF_SEND_WEB_DISCOVERY.equals(key)) {
            UserPrefs.get(getProfile())
                    .setBoolean(WebDiscoveryPrefs.WEB_DISCOVERY_ENABLED, (boolean) newValue);
        }
        return true;
    }
}
