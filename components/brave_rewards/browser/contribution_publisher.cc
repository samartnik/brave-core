/* Copyright (c) 2020 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

#include "brave/components/brave_rewards/browser/contribution_publisher.h"

namespace brave_rewards {

ContributionPublisher::ContributionPublisher()
    : total_amount_(0.0), contributed_amount_(0.0) {}

ContributionPublisher::~ContributionPublisher() {}

ContributionPublisher::ContributionPublisher(
    const ContributionPublisher& info) {
  contribution_id_ = info.contribution_id_;
  publisher_key_ = info.publisher_key_;
  total_amount_ = info.total_amount_;
  contributed_amount_ = info.contributed_amount_;
}

}  // namespace brave_rewards
