/* Copyright (c) 2020 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

#include "brave/components/brave_rewards/browser/contribution_info.h"

namespace brave_rewards {

ContributionInfo::ContributionInfo()
    : amount_(0.0),
      type_(0),
      step_(0),
      retry_count_(0),
      created_at_(0),
      processor_(0) {}

ContributionInfo::~ContributionInfo() {}

ContributionInfo::ContributionInfo(const ContributionInfo& info) {
  contribution_id_ = info.contribution_id_;
  amount_ = info.amount_;
  type_ = info.type_;
  step_ = info.step_;
  retry_count_ = info.retry_count_;
  processor_ = info.processor_;
  publishers_ = info.publishers_;
}

}  // namespace brave_rewards
