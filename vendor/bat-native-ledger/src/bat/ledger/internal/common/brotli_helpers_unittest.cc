/* Copyright (c) 2020 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

#include "bat/ledger/internal/common/brotli_helpers.h"
#include "testing/gtest/include/gtest/gtest.h"

// npm run test -- brave_unit_tests --filter='BraveLedgerBrotliHelpersTest.*'

namespace {

constexpr char kCompressed[] = {
    0x0b, 0x15, 0x80, 0x54, 0x68, 0x65, 0x20, 0x71, 0x75,
    0x69, 0x63, 0x6b, 0x20, 0x62, 0x72, 0x6f, 0x77, 0x6e,
    0x20, 0x66, 0x6f, 0x78, 0x20, 0x6a, 0x75, 0x6d, 0x70,
    0x73, 0x20, 0x6f, 0x76, 0x65, 0x72, 0x20, 0x74, 0x68,
    0x65, 0x20, 0x6c, 0x61, 0x7a, 0x79, 0x20, 0x64, 0x6f,
    0x67, 0x03
};

constexpr char kUncompressed[] =
    "The quick brown fox jumps over the lazy dog";

}  // namespace

namespace braveledger_helpers {

class BraveLedgerBrotliHelpersTest : public testing::Test {
 protected:
  static std::string GetInput() {
    return std::string(kCompressed, sizeof(kCompressed) / sizeof(char));
  }
};

TEST_F(BraveLedgerBrotliHelpersTest, TestDecode) {
  std::string s;

  EXPECT_TRUE(DecodeBrotliString(GetInput(), 43, &s));
  EXPECT_EQ(s, std::string(kUncompressed));

  // Empty input
  EXPECT_FALSE(DecodeBrotliString("", 43, &s));

  // Uncompressed size not large enough
  EXPECT_FALSE(DecodeBrotliString(GetInput(), 16, &s));

  // Not Brotli
  EXPECT_FALSE(DecodeBrotliString("not brotli", 16, &s));
}

TEST_F(BraveLedgerBrotliHelpersTest, TestDecodeWithBuffer) {
  std::string s;

  EXPECT_TRUE(DecodeBrotliStringWithBuffer(GetInput(), 16, &s));
  EXPECT_EQ(s, std::string(kUncompressed));

  // Empty input
  EXPECT_FALSE(DecodeBrotliStringWithBuffer("", 16, &s));

  // Incomplete input
  EXPECT_FALSE(DecodeBrotliStringWithBuffer(GetInput().substr(0, 32), 16, &s));

  // Not Brotli
  EXPECT_FALSE(DecodeBrotliStringWithBuffer("not brotli", 16, &s));
}

}  // namespace braveledger_helpers
