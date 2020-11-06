/* Copyright (c) 2020 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

#include "brave/browser/extensions/api/moonpay_api.h"

#include <string>
#include <memory>
#include <utility>

#include "brave/browser/profiles/profile_util.h"
#include "brave/components/moonpay/browser/regions.h"
#include "brave/components/moonpay/common/pref_names.h"
#include "brave/components/ntp_widget_utils/browser/ntp_widget_utils_region.h"
#include "chrome/browser/profiles/profile.h"
#include "components/prefs/pref_service.h"

namespace {
bool IsMoonpayAPIAvailable(content::BrowserContext* context) {
  return brave::IsRegularProfile(context);
}
}

namespace extensions {
namespace api {

ExtensionFunction::ResponseAction
MoonpayIsBitcoinDotComSupportedFunction::Run() {
  Profile* profile = Profile::FromBrowserContext(browser_context());

  if (!IsMoonpayAPIAvailable(browser_context())) {
    return RespondNow(Error("Not available in Tor/incognito/guest profile"));
  }

  bool is_supported = ntp_widget_utils::IsRegionSupported(
      profile->GetPrefs(), moonpay::bitcoin_dot_com_supported_regions, true);
  return RespondNow(OneArgument(base::Value(is_supported)));
}

ExtensionFunction::ResponseAction
MoonpayOnBuyBitcoinDotComCryptoFunction::Run() {
  Profile* profile = Profile::FromBrowserContext(browser_context());

  if (!IsMoonpayAPIAvailable(browser_context())) {
    return RespondNow(Error("Not available in Tor/incognito/guest profile"));
  }

  profile->GetPrefs()->SetBoolean(kMoonpayHasBoughtBitcoinDotComCrypto, true);
  profile->GetPrefs()->SetBoolean(kMoonpayHasInteractedBitcoinDotCom, true);

  return RespondNow(NoArguments());
}

ExtensionFunction::ResponseAction
MoonpayOnInteractionBitcoinDotComFunction::Run() {
  Profile* profile = Profile::FromBrowserContext(browser_context());

  if (!IsMoonpayAPIAvailable(browser_context())) {
    return RespondNow(Error("Not available in Tor/incognito/guest profile"));
  }

  profile->GetPrefs()->SetBoolean(kMoonpayHasInteractedBitcoinDotCom, true);

  return RespondNow(NoArguments());
}

ExtensionFunction::ResponseAction
MoonpayGetBitcoinDotComInteractionsFunction::Run() {
  Profile* profile = Profile::FromBrowserContext(browser_context());

  if (!IsMoonpayAPIAvailable(browser_context())) {
    return RespondNow(Error("Not available in Tor/incognito/guest profile"));
  }

  bool has_bought = profile->GetPrefs()->GetBoolean(
      kMoonpayHasBoughtBitcoinDotComCrypto);
  bool has_interacted = profile->GetPrefs()->GetBoolean(
      kMoonpayHasInteractedBitcoinDotCom);

  base::DictionaryValue interactions;
  interactions.SetBoolean("boughtCrypto", has_bought);
  interactions.SetBoolean("interacted", has_interacted);

  return RespondNow(OneArgument(std::move(interactions)));
}

}  // namespace api
}  // namespace extensions
