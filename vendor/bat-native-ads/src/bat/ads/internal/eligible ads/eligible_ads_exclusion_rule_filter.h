/* Copyright (c) 2020 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

#ifndef BAT_ADS_INTERNAL_ELIGIBLE_ADS_H_
#define BAT_ADS_INTERNAL_ELIGIBLE_ADS_H_

#include "bat/ads/internal/eligible_ads/eligible_ads_filter.h"

namespace ads {

class AdsImpl;

class EligibleAdsExclusionRuleFilter : EligibleAdsFilter {
 public:
  EligibleAdsExclusionRuleFilter(
      const AdsImpl* const ads);

  ~EligibleAdsExclusionRuleFilter() override;

  std::vector<CreativeAdInfo> Apply(
      const std::vector<CreativeAdInfo>& ads) override;

 private:
  const AdsImpl* const ads_;  // NOT OWNED
};

}  // namespace ads

#endif  // BAT_ADS_INTERNAL_ELIGIBLE_ADS_H_
