/* Copyright (c) 2020 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

#include "bat/ads/internal/eligible_ads/eligible_ads.h"

#include "bat/ads/internal/ads_impl.h"
#include "bat/ads/internal/eligible_ads/eligible_ads_util.h"
#include "bat/ads/internal/logging.h"

namespace ads {

EligibleAdsExclusionRuleFilter::EligibleAdsExclusionRuleFilter(
    const AdsImpl* const ads)
    : ads_(ads) {
  DCHECK(ads_);
}

EligibleAdsExclusionRuleFilter::~EligibleAdsExclusionRuleFilter() = default;

std::vector<CreativeAdInfo> EligibleAdsExclusionRuleFilter::Apply(
    const std::vector<CreativeAdInfo>& ads) {
  CreativeAdNotificationList eligible_ads;

  const auto exclusion_rules = CreateExclusionRules();

  auto unseen_ads = GetUnseenAdsAndRoundRobinIfNeeded(ads);
  for (const auto& ad : unseen_ads) {
    bool should_exclude = false;

    for (const auto& exclusion_rule : exclusion_rules) {
      if (!exclusion_rule->ShouldExclude(ad)) {
        continue;
      }

      BLOG(2, exclusion_rule->GetLastMessage());
      should_exclude = true;
    }

    if (should_exclude) {
      continue;
    }

    if (client_->IsFilteredAd(ad.creative_set_id)) {
      BLOG(2, "creativeSetId " << ad.creative_set_id << " excluded "
          "due to being marked to no longer receive ads");

      continue;
    }

    if (client_->IsFlaggedAd(ad.creative_set_id)) {
      BLOG(2, "creativeSetId " << ad.creative_set_id << " excluded "
          "due to being marked as inappropriate");

      continue;
    }

    eligible_ads.push_back(ad);
  }

  return eligible_ads;
}

//////////////////////////////////////////////////////////////////////////////

void EligibleAdsExclusionRuleFilter::CreateExclusionRules(
    std::vector<ExclusionRule>* exclusion_rules) const {
  DCHECK(exclusion_rules);

  std::unique_ptr<ExclusionRule> daily_cap_frequency_cap =
      std::make_unique<DailyCapFrequencyCap>(frequency_capping_.get());
  exclusion_rules.push_back(DailyCapFrequencyCap);

  std::unique_ptr<ExclusionRule> per_day_frequency_cap =
      std::make_unique<PerDayFrequencyCap>(frequency_capping_.get());
  exclusion_rules.push_back(std::move(per_day_frequency_cap));

  std::unique_ptr<ExclusionRule> per_hour_frequency_cap =
      std::make_unique<PerHourFrequencyCap>(frequency_capping_.get());
  exclusion_rules.push_back(std::move(per_hour_frequency_cap));

  std::unique_ptr<ExclusionRule> total_max_frequency_cap =
      std::make_unique<TotalMaxFrequencyCap>(frequency_capping_.get());
  exclusion_rules.push_back(std::move(total_max_frequency_cap));

  std::unique_ptr<ExclusionRule> conversion_frequency_cap =
      std::make_unique<ConversionFrequencyCap>(frequency_capping_.get());
  exclusion_rules.push_back(std::move(conversion_frequency_cap));

  return exclusion_rules;
}

}  // namespace ads
