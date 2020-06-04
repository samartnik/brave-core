/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

import { Reducer } from 'redux'

// Constants
import { types } from '../constants/rewards_internals_types'

// Utils
import * as storage from '../storage'

const rewardsInternalsReducer: Reducer<RewardsInternals.State | undefined> = (state: RewardsInternals.State | undefined, action) => {
  if (state === undefined) {
    state = storage.load()
  }

  switch (action.type) {
    case types.GET_REWARDS_ENABLED:
      chrome.send('brave_rewards_internals.getRewardsEnabled')
      break
    case types.GET_REWARDS_INTERNALS_INFO:
      chrome.send('brave_rewards_internals.getRewardsInternalsInfo')
      break
    case types.ON_GET_REWARDS_ENABLED:
      state = { ...state }
      state.isRewardsEnabled = action.payload.enabled
      break
    case types.ON_GET_REWARDS_INTERNALS_INFO:
      state = { ...state }
      state.info = action.payload.info
      break
    case types.GET_BALANCE:
      chrome.send('brave_rewards_internals.getBalance')
      break
    case types.ON_BALANCE:
      state = { ...state }
      state.balance = action.payload.balance
      break
    case types.GET_CONTRIBUTIONS:
      chrome.send('brave_rewards_internals.getContributions')
      break
    case types.ON_CONTRIBUTIONS:
      state = { ...state }
      state.contributions = action.payload.contributions
      break
    case types.GET_PROMOTIONS:
      chrome.send('brave_rewards_internals.getPromotions')
      break
    case types.ON_PROMOTIONS:
      state = { ...state }
      state.promotions = action.payload.promotions
      break
    case types.GET_PARTIAL_LOG:
      chrome.send('brave_rewards_internals.getPartialLog')
      break
    case types.ON_GET_PARTIAL_LOG:
      state = { ...state }
      state.log = action.payload.log
      break
    case types.GET_FULL_LOG:
      chrome.send('brave_rewards_internals.getFullLog')
      break
    case types.ON_GET_FULL_LOG:
      state = { ...state }
      state.fullLog = action.payload.log
      break
    case types.CLEAR_LOG:
      chrome.send('brave_rewards_internals.clearLog')
      break
    case types.DOWNLOAD_COMPLETED:
      state.fullLog = ''
      break
    default:
      break
  }

  return state
}

export default rewardsInternalsReducer
