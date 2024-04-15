/* Copyright (c) 2020 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/. */

#include "brave/components/l10n/common/localization_util.h"
#include "chrome/browser/ui/singleton_tabs.h"

namespace {

const char kBraveStubSessionTag[] = "brave_stub_more_session_tag";
const char kBraveSyncedTabsUrl[] = "brave://history/syncedTabs";

}  //  namespace

// Patched because this inserting should be done before BuildLocalEntries() in
// ctor and only once.
#define BRAVE_RECENT_TABS_SUB_MENU_MODEL_BUILD         \
  InsertItemWithStringIdAt(history_separator_index_++, \
                           IDC_CLEAR_BROWSING_DATA, IDS_CLEAR_BROWSING_DATA);

#include "src/chrome/browser/ui/tabs/recent_tabs_sub_menu_model.cc"

#undef BRAVE_RECENT_TABS_SUB_MENU_MODEL_BUILD

#include "brave/browser/ui/toolbar/brave_recent_tabs_sub_menu_model.h"

// Methods of BraveRecentTabsSubMenuModel are implemented below instead of
// brave_recent_tabs_sub_menu_model.cc to have the access to functions in
// anonymous namespace in recent_tabs_sub_menu_model.cc

BraveRecentTabsSubMenuModel::BraveRecentTabsSubMenuModel(
    ui::AcceleratorProvider* accelerator_provider,
    Browser* browser)
    : RecentTabsSubMenuModel(accelerator_provider, browser) {}

BraveRecentTabsSubMenuModel::~BraveRecentTabsSubMenuModel() {}

void BraveRecentTabsSubMenuModel::ExecuteCommand(int command_id,
                                                 int event_flags) {
  if (IsTabModelCommandId(command_id)) {
    TabNavigationItems* tab_items = GetTabVectorForCommandId(command_id);
    const TabNavigationItem& item = (*tab_items)[command_id];
    DCHECK(item.tab_id.is_valid() && item.url.is_valid());

    if (item.session_tag == kBraveStubSessionTag) {
      ShowSingletonTabOverwritingNTP(browser_, GURL(kBraveSyncedTabsUrl));
      return;
    }
  }

  if (command_id == IDC_CLEAR_BROWSING_DATA) {
    chrome::ExecuteCommand(browser_, command_id);
    return;
  }

  RecentTabsSubMenuModel::ExecuteCommand(command_id, event_flags);
}
