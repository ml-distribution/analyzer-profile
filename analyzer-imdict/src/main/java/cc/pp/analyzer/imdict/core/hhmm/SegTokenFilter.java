/**
 * Copyright 2009 www.imdict.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.pp.analyzer.imdict.core.hhmm;

import cc.pp.analyzer.imdict.core.Utility;

public class SegTokenFilter {

  public SegToken filter(SegToken token) {
    switch (token.wordType) {
      case FULLWIDTH_NUMBER:
      case FULLWIDTH_STRING:
        for (int i = 0; i < token.charArray.length; i++) {
          if (token.charArray[i] >= 0xFF10)
            token.charArray[i] -= 0xFEE0;

          if (token.charArray[i] >= 0x0041 && token.charArray[i] <= 0x005A)
            token.charArray[i] += 0x0020;
        }
        break;
      case STRING:
        for (int i = 0; i < token.charArray.length; i++) {
          if (token.charArray[i] >= 0x0041 && token.charArray[i] <= 0x005A)
            token.charArray[i] += 0x0020;
        }
        break;
      case DELIMITER:
        token.charArray = Utility.COMMON_DELIMITER;
        break;
      default:
        break;
    }
    return token;
  }
}
