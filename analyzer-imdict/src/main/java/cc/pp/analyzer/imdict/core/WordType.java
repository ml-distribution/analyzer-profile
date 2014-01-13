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

public enum WordType {
  SENTENCE_BEGIN, SENTENCE_END, // 句子的开头和结束
  CHINESE_WORD, // 中文词
  STRING, NUMBER, // ascii字符串和数字
  DELIMITER, // 所有标点符号
  FULLWIDTH_STRING, FULLWIDTH_NUMBER;// 含有全角字符的字符串，含全角数字的数字
}
