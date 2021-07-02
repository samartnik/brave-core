/* Copyright (c) 2021 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.chromium.chrome.browser;

import org.chromium.base.BraveReflectionUtil;
import org.chromium.chrome.browser.compositor.layouts.Layout;
import org.chromium.chrome.browser.compositor.layouts.LayoutManagerChrome;
import org.chromium.chrome.browser.compositor.layouts.LayoutManagerImpl;
import org.chromium.chrome.browser.compositor.layouts.phone.StackLayout;

public class BraveTabbedActivity extends ChromeTabbedActivity {
    // To delete in bytecode, members from parent class will be used instead.
    private LayoutManagerChrome mLayoutManager;

    public BraveTabbedActivity() {
        super();
    }

    public void hideOverview() {
        org.chromium.base.Log.e("SAM", "SAM: BraveTabbedActivity.hideOverview");
        Layout activeLayout = mLayoutManager.getActiveLayout();
        if (activeLayout instanceof StackLayout) {
            ((StackLayout) activeLayout).commitOutstandingModelState(LayoutManagerImpl.time());
        }
        BraveReflectionUtil.InvokeMethod(ChromeTabbedActivity.class, this, "hideOverview");
    }
}
