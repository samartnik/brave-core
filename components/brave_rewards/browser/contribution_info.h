/* Copyright (c) 2020 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

#ifndef BRAVE_COMPONENTS_BRAVE_REWARDS_BROWSER_CONTRIBUTION_INFO_H_
#define BRAVE_COMPONENTS_BRAVE_REWARDS_BROWSER_CONTRIBUTION_INFO_H_

#include <string>
#include <vector>

#include "brave/components/brave_rewards/browser/contribution_publisher.h"

namespace brave_rewards {

struct ContributionInfo {
  ContributionInfo();
  ~ContributionInfo();
  ContributionInfo(const ContributionInfo& info);

  std::string contribution_id_;
  double amount_;
  int type_;
  int step_;
  int retry_count_;
  uint64_t created_at_;
  int processor_;

  std::vector<ContributionPublisher> publishers_;
};

}  // namespace brave_rewards

#endif  // BRAVE_COMPONENTS_BRAVE_REWARDS_BROWSER_CONTRIBUTION_INFO_H_
