/* Copyright (c) 2020 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

#ifndef BAT_ADS_INTERNAL_ELIGIBLE_ADS_ELIGIBLE_ADS_H_
#define BAT_ADS_INTERNAL_ELIGIBLE_ADS_ELIGIBLE_ADS_H_

#include <vector>

#include "bat/ads/creative_ad_info.h"

namespace ads {

class EligibleAdsFilter {
 public:
  virtual ~EligibleAdsFilter() = default;

  virtual bool Apply(
      const std::vector<CreativeAdInfo>& ads) = 0;
};

}  // namespace ads

#endif  // BAT_ADS_INTERNAL_ELIGIBLE_ADS_ELIGIBLE_ADS_H_
