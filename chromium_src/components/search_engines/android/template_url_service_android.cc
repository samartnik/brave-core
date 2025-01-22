/* Copyright (c) 2019 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

#include "components/search_engines/android/template_url_service_android.h"

#define DoesDefaultSearchEngineHaveLogo \
  DoesDefaultSearchEngineHaveLogo_ChromiumImpl

#include <components/search_engines/android/template_url_service_android.cc>

#undef DoesDefaultSearchEngineHaveLogo

jboolean TemplateUrlServiceAndroid::DoesDefaultSearchEngineHaveLogo(
    JNIEnv* env) {
  if (IsDefaultSearchEngineGoogle(env)) {
    return false;
  }

  return DoesDefaultSearchEngineHaveLogo_ChromiumImpl(env);
}

jboolean TemplateUrlServiceAndroid::AddSearchEngine(JNIEnv* env) {
  TemplateURLData template_url_data;
  template_url_data.SetShortName(u"test1");
  template_url_data.SetKeyword(u"test.com");
  template_url_data.SetURL("http://test.com/search?t={searchTerms}");
  template_url_data.prepopulate_id = 99;
  TemplateURL* template_url = template_url_service_->Add(
      std::make_unique<TemplateURL>(template_url_data));
  return !template_url;
}
