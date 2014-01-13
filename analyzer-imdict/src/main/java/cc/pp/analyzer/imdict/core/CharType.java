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

package cc.pp.analyzer.imdict.core;

public enum CharType {

  DELIMITER, LETTER, DIGIT, HANZI, SPACE_LIKE,
  // (全角半角)标点符号，半角（字母，数字），汉字，空格，"\t\r\n"等空格或换行字符
  FULLWIDTH_LETTER, FULLWIDTH_DIGIT, // 全角字符，字母，数字
  OTHER;

}
