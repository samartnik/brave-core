/* Copyright (c) 2019 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/. */

#ifndef BRAVE_COMPONENTS_BRAVE_ADS_CORE_ADS_H_
#define BRAVE_COMPONENTS_BRAVE_ADS_CORE_ADS_H_

#include <cstdint>
#include <string>
#include <vector>

#include "brave/components/brave_ads/common/interfaces/ads.mojom-forward.h"
#include "brave/components/brave_ads/core/ad_content_action_types.h"
#include "brave/components/brave_ads/core/ads_callback.h"
#include "brave/components/brave_ads/core/category_content_action_types.h"
#include "brave/components/brave_ads/core/export.h"
#include "brave/components/brave_ads/core/history_filter_types.h"
#include "brave/components/brave_ads/core/history_item_info.h"
#include "brave/components/brave_ads/core/history_sort_types.h"
#include "third_party/abseil-cpp/absl/types/optional.h"

class GURL;

namespace base {
class Time;
class TimeDelta;
}  // namespace base

namespace brave_ads {

class AdsClient;
struct NotificationAdInfo;

class ADS_EXPORT Ads {
 public:
  Ads() = default;

  Ads(const Ads& other) = delete;
  Ads& operator=(const Ads& other) = delete;

  Ads(Ads&& other) noexcept = delete;
  Ads& operator=(Ads&& other) noexcept = delete;

  virtual ~Ads() = default;

  static Ads* CreateInstance(AdsClient* ads_client);

  // Called to initialize ads. The callback takes one argument - |bool| is set
  // to |true| if successful otherwise |false|.
  virtual void Initialize(InitializeCallback callback) = 0;

  // Called to shutdown ads. The callback takes one argument - |bool| is set to
  // |true| if successful otherwise |false|.
  virtual void Shutdown(ShutdownCallback callback) = 0;

  // Called to get diagnostics to help identify issues. The callback takes one
  // argument - |base::Value::List| containing info of the obtained diagnostics.
  virtual void GetDiagnostics(GetDiagnosticsCallback callback) = 0;

  // Called when a user has been idle for the threshold set in
  // |prefs::kIdleTimeThreshold|. NOTE: This should not be called on mobile
  // devices.
  virtual void OnUserDidBecomeIdle() = 0;

  // Called when a user is no longer idle. |idle_time| is the amount of time in
  // seconds that the user was idle. |screen_was_locked| should be |true| if the
  // screen was locked, otherwise |false|. NOTE: This should not be called on
  // mobile devices.
  virtual void OnUserDidBecomeActive(base::TimeDelta idle_time,
                                     bool screen_was_locked) = 0;

  // Called when a page navigation was initiated by a user gesture.
  // |page_transition_type| containing the page transition type, see enums for
  // |PageTransitionType|.
  virtual void TriggerUserGestureEvent(int32_t page_transition_type) = 0;

  // Called when the user's Brave Rewards wallet has changed.
  virtual void OnRewardsWalletDidChange(const std::string& payment_id,
                                        const std::string& recovery_seed) = 0;

  // Called to get the statement of accounts. The callback takes one argument -
  // |mojom::StatementInfo| containing info of the obtained statement of
  // accounts.
  virtual void GetStatementOfAccounts(
      GetStatementOfAccountsCallback callback) = 0;

  // Should be called to serve an inline content ad for the specified
  // |dimensions|. The callback takes two arguments - |std::string| containing
  // the dimensions and |InlineContentAdInfo| containing the info for the ad.
  virtual void MaybeServeInlineContentAd(
      const std::string& dimensions,
      MaybeServeInlineContentAdCallback callback) = 0;

  // Called when a user views or interacts with an inline content ad to trigger
  // an |event_type| event for the specified |placement_id| and
  // |creative_instance_id|. |placement_id| should be a 128-bit random GUID in
  // the form of version 4. See RFC 4122, section 4.4. The same |placement_id|
  // generated for the viewed event should be used for all other events for the
  // same ad placement.
  virtual void TriggerInlineContentAdEvent(
      const std::string& placement_id,
      const std::string& creative_instance_id,
      mojom::InlineContentAdEventType event_type) = 0;

  // Should be called to serve a new tab page ad. The callback takes one
  // argument - |NewTabPageAdInfo| containing the info for the ad.
  virtual void MaybeServeNewTabPageAd(
      MaybeServeNewTabPageAdCallback callback) = 0;

  // Called when a user views or interacts with a new tab page ad to trigger an
  // |event_type| event for the specified |placement_id| and
  // |creative_instance_id|. |placement_id| should be a 128-bit random GUID in
  // the form of version 4. See RFC 4122, section 4.4. The same |placement_id|
  // generated for the viewed event should be used for all other events for the
  // same ad placement.
  virtual void TriggerNewTabPageAdEvent(
      const std::string& placement_id,
      const std::string& creative_instance_id,
      mojom::NewTabPageAdEventType event_type) = 0;

  // Called to get the notification ad specified by |placement_id|. Returns
  // |NotificationAdInfo| containing the info of the ad.
  virtual absl::optional<NotificationAdInfo> MaybeGetNotificationAd(
      const std::string& placement_id) = 0;

  // Called when a user views or interacts with a notification ad or the ad
  // notification times out to trigger an |event_type| event for the specified
  // |placement_id|. |placement_id| should be a 128-bit random GUID in the form
  // of version 4. See RFC 4122, section 4.4. The same |placement_id| generated
  // for the viewed event should be used for all other events for the same ad
  // placement.
  virtual void TriggerNotificationAdEvent(
      const std::string& placement_id,
      mojom::NotificationAdEventType event_type) = 0;

  // Called when a user views or interacts with a promoted content ad to trigger
  // an |event_type| event for the specified |placement_id| and
  // |creative_instance_id|. |placement_id| should be a 128-bit random GUID in
  // the form of version 4. See RFC 4122, section 4.4. The same |placement_id|
  // generated for the viewed event should be used for all other events for the
  // same ad placement.
  virtual void TriggerPromotedContentAdEvent(
      const std::string& placement_id,
      const std::string& creative_instance_id,
      mojom::PromotedContentAdEventType event_type) = 0;

  // Called when a user views or interacts with a search result ad to trigger an
  // |event_type| event for the ad specified in |ad_mojom|.
  virtual void TriggerSearchResultAdEvent(
      mojom::SearchResultAdInfoPtr ad_mojom,
      mojom::SearchResultAdEventType event_type) = 0;

  // Called to purge orphaned served ad events. NOTE: You should call before
  // triggering new ad events for the specified |ad_type|.
  virtual void PurgeOrphanedAdEventsForType(
      mojom::AdType ad_type,
      PurgeOrphanedAdEventsForTypeCallback callback) = 0;

  // Called to get history filtered by |filter_type| and sorted by |sort_type|
  // between |from_time| and |to_time| date range. Returns |HistoryItemList|
  // containing info of the obtained history.
  virtual HistoryItemList GetHistory(HistoryFilterType filter_type,
                                     HistorySortType sort_type,
                                     base::Time from_time,
                                     base::Time to_time) = 0;

  // Called to remove all history. The callback takes one argument - |bool| is
  // set to |true| if successful otherwise |false|.
  virtual void RemoveAllHistory(RemoveAllHistoryCallback callback) = 0;

  // Called to like an advertiser. This is a toggle, so calling it again returns
  // the setting to the neutral state. Returns |AdContentLikeActionType|
  // containing the current state.
  virtual AdContentLikeActionType ToggleAdThumbUp(base::Value::Dict value) = 0;

  // Called to dislike an advertiser. This is a toggle, so calling it again
  // returns the setting to the neutral state. Returns |AdContentLikeActionType|
  // containing the current state.
  virtual AdContentLikeActionType ToggleAdThumbDown(
      base::Value::Dict value) = 0;

  // Called to no longer receive ads for the specified category. This is a
  // toggle, so calling it again returns the setting to the neutral state.
  // Returns |CategoryContentOptActionType| containing the current state.
  virtual CategoryContentOptActionType ToggleAdOptIn(
      const std::string& category,
      const CategoryContentOptActionType& action_type) = 0;

  // Called to receive ads for the specified category. This is a toggle, so
  // calling it again returns the setting to the neutral state. Returns
  // |CategoryContentOptActionType| containing the current state.
  virtual CategoryContentOptActionType ToggleAdOptOut(
      const std::string& category,
      const CategoryContentOptActionType& action_type) = 0;

  // Called to save an ad for later viewing. This is a toggle, so calling it
  // again removes the ad from the saved list. Returns |true| if the ad was
  // saved otherwise |false|.
  virtual bool ToggleSavedAd(base::Value::Dict value) = 0;

  // Called to mark an ad as inappropriate. This is a toggle, so calling it
  // again unmarks the ad. Returns |true| if the ad was marked otherwise
  // |false|.
  virtual bool ToggleFlaggedAd(base::Value::Dict value) = 0;
};

}  // namespace brave_ads

#endif  // BRAVE_COMPONENTS_BRAVE_ADS_CORE_ADS_H_
