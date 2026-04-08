/* Copyright (c) 2026 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/. */

package org.chromium.chrome.browser.autofill.options;

import android.os.Bundle;

import androidx.preference.PreferenceScreen;

import org.chromium.base.supplier.ObservableSuppliers;
import org.chromium.base.supplier.SettableMonotonicObservableSupplier;
import org.chromium.build.annotations.NullMarked;
import org.chromium.build.annotations.Nullable;
import org.chromium.chrome.browser.autofill.R;
import org.chromium.chrome.browser.preferences.BravePref;
import org.chromium.chrome.browser.settings.ChromeBaseSettingsFragment;
import org.chromium.components.browser_ui.settings.ChromeSwitchPreference;
import org.chromium.components.browser_ui.settings.SettingsFragment;
import org.chromium.components.user_prefs.UserPrefs;

/** Brave extension of {@link AutofillOptionsFragment} adding an "Autofill in private tabs" toggle. */
@NullMarked
public class BraveAutofillOptionsFragment extends ChromeBaseSettingsFragment {
    static final String PREF_AUTOFILL_PRIVATE_WINDOW = "autofill_private_window";

    private final SettableMonotonicObservableSupplier<String> mPageTitle =
            ObservableSuppliers.createMonotonic();

    // AutofillOptionsFragment overrides these at runtime after bytecode rewriting.
    @Override
    public SettableMonotonicObservableSupplier<String> getPageTitle() {
        return mPageTitle;
    }

    @Override
    public @SettingsFragment.AnimationType int getAnimationType() {
        return SettingsFragment.AnimationType.PROPERTY;
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        // Intentionally empty: AutofillOptionsFragment.onCreatePreferences() loads the XML.
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // super.onCreate() triggers onCreatePreferences() via PreferenceFragmentCompat, so
        // the upstream XML preferences are already added to the screen at this point.
        addAutofillPrivateWindowPreference();
    }

    private void addAutofillPrivateWindowPreference() {
        ChromeSwitchPreference preference = new ChromeSwitchPreference(requireContext());
        preference.setKey(PREF_AUTOFILL_PRIVATE_WINDOW);
        preference.setTitle(R.string.prefs_autofill_private_window_title);
        preference.setSummary(R.string.prefs_autofill_private_window_summary);
        preference.setOrder(100);
        boolean isAutofillPrivateWindow =
                UserPrefs.get(getProfile()).getBoolean(BravePref.BRAVE_AUTOFILL_PRIVATE_WINDOWS);
        preference.setChecked(isAutofillPrivateWindow);
        preference.setOnPreferenceChangeListener(
                (pref, newValue) -> {
                    UserPrefs.get(getProfile())
                            .setBoolean(
                                    BravePref.BRAVE_AUTOFILL_PRIVATE_WINDOWS, (boolean) newValue);
                    return true;
                });
        PreferenceScreen screen = getPreferenceScreen();
        screen.addPreference(preference);
    }
}
