/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

export const enum types {
  GET_REWARDS_ENABLED = '@@rewards_internals/GET_REWARDS_ENABLED',
  ON_GET_REWARDS_ENABLED = '@@rewards_internals/ON_GET_REWARDS_ENABLED',
  GET_REWARDS_INTERNALS_INFO = '@@rewards_internals/GET_REWARDS_INTERNALS_INFO',
  ON_GET_REWARDS_INTERNALS_INFO = '@@rewards_internals/ON_GET_REWARDS_INTERNALS_INFO',
  GET_BALANCE = '@@rewards_internals/GET_BALANCE',
  ON_BALANCE = '@@rewards_internals/ON_BALANCE',
  GET_CONTRIBUTIONS = '@@rewards_internals/GET_CONTRIBUTIONS',
  ON_CONTRIBUTIONS = '@@rewards_internals/ON_CONTRIBUTIONS',
  GET_PROMOTIONS = '@@rewards_internals/GET_PROMOTIONS',
  ON_PROMOTIONS = '@@rewards_internals/ON_PROMOTIONS',
  GET_PARTIAL_LOG = '@@rewards_internals/GET_PARTIAL_LOG',
  ON_GET_PARTIAL_LOG = '@@rewards_internals/ON_GET_PARTIAL_LOG',
  GET_FULL_LOG = '@@rewards_internals/GET_FULL_LOG',
  ON_GET_FULL_LOG = '@@rewards_internals/ON_GET_FULL_LOG',
  CLEAR_LOG = '@@rewards_internals/CLEAR_LOG',
  DOWNLOAD_COMPLETED = '@@rewards_internals/DOWNLOAD_COMPLETED'
}
