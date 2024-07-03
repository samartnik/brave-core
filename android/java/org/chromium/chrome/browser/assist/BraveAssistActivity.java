/* Copyright (c) 2024 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/. */

package org.chromium.chrome.browser.assist;


import android.view.View;

import org.chromium.chrome.browser.brave_leo.BraveLeoActivity;
import org.chromium.chrome.browser.customtabs.CustomTabActivity;

/**
 * Activity that handles the Brave Assist feature. Here we just redirect to the required action and
 * close activity itself.
 */
public class BraveAssistActivity extends CustomTabActivity {
    public static final String BRAVE_AI_CHAT_URL = "chrome-untrusted://chat";

    // @Override
    // protected void doLayoutInflation() {
    //     View placeHolderView = new View(this);
    //     setContentView(placeHolderView);
    // }

    @Override
    public void finishNativeInitialization() {
        super.finishNativeInitialization();

        org.chromium.base.Log.e("SAM", "SAMSAM: finishNativeInitialization");
        // For now we just open Leo. Going forward we may want to add different actions.
        BraveLeoActivity.showPage(this, BRAVE_AI_CHAT_URL);
        finish();
    }
}
