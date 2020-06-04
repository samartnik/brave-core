/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

import * as React from 'react'

import { ContributionPublishers } from './contributionPublishers'

interface Props {
  contribution: RewardsInternals.ContributionInfo
}

// Utils
import { getLocale } from '../../../../common/locale'

const getContributionTypeString = (contributionType: number) => {
  switch (contributionType) {
    case 2:
      return getLocale('rewardsTypeAuto')
    case 8:
      return getLocale('rewardsTypeOneTimeTip')
    case 21:
      return getLocale('rewardsTypeRecurringTip')
  }

  return getLocale('rewardsTypeUnknown')
}

const getProcessorString = (processor: number) => {
  switch (processor) {
    case 1:
      return getLocale('processorBraveTokens')
    case 2:
      return getLocale('processorUphold')
    case 3:
      return getLocale('processorBraveUserFunds')
  }

  return ''
}

const getRetryStepString = (retryStep: number) => {
  switch (retryStep) {
    case 1:
      return getLocale('retryStepReconcile')
    case 2:
      return getLocale('retryStepCurrent')
    case 3:
      return getLocale('retryStepPayload')
    case 4:
      return getLocale('retryStepRegister')
    case 5:
      return getLocale('retryStepViewing')
    case 6:
      return getLocale('retryStepWinners')
    case 7:
      return getLocale('retryStepPrepare')
    case 8:
      return getLocale('retryStepProof')
    case 9:
      return getLocale('retryStepVote')
    case 10:
      return getLocale('retryStepFinal')
  }

  return getLocale('retryStepUnknown')
}

export const Contribution = (props: Props) => (
  <div>
    <h3>{props.contribution.id}</h3>
    {getLocale('contributionType')} {getContributionTypeString(props.contribution.type)}
    <br/>
    {getLocale('amount')} {props.contribution.amount} {getLocale('bat')}
    <br/>
    {getLocale('retryStep')} {getRetryStepString(props.contribution.step)}
    <br/>
    {getLocale('retryCount')} {props.contribution.retryCount}
    <br/>
    {getLocale('contributionProcessor')} {getProcessorString(props.contribution.processor)}
    <br/>
    <blockquote>
      <ContributionPublishers items={props.contribution.publishers} />
    </blockquote>
  </div>
)
