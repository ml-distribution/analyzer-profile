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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SegGraph {

  /**
   * 用一个ArrayList记录startOffset相同的Token，这个startOffset就是Token的key
   */
  private Map<Integer, List<SegToken>> tokenListTable = new HashMap<Integer, List<SegToken>>();

  private int maxStart = -1;

  /**
   * 查看startOffset为s的Token是否存在，如果没有则说明s处没有Token或者还没有添加
   * 
   * @param s startOffset
   * @return
   */
  public boolean isStartExist(int s) {
    return tokenListTable.get(s) != null;
  }

  /**
   * 取出startOffset为s的所有Tokens，如果没有则返回null
   * 
   * @param s
   * @return 所有相同startOffset的Token的序列
   */
  public List<SegToken> getStartList(int s) {
    return tokenListTable.get(s);
  }

  public int getMaxStart() {
    return maxStart;
  }

  /**
   * 为SegGraph中的所有Tokens生成一个统一的index，index从0开始，
   * 按照startOffset递增的顺序排序，相同startOffset的Tokens按照放置先后顺序排序
   */
  public List<SegToken> makeIndex() {
    List<SegToken> result = new ArrayList<SegToken>();
    int s = -1, count = 0, size = tokenListTable.size();
    List<SegToken> tokenList;
    short index = 0;
    while (count < size) {
      if (isStartExist(s)) {
        tokenList = tokenListTable.get(s);
        for (SegToken st : tokenList) {
          st.index = index;
          result.add(st);
          index++;
        }
        count++;
      }
      s++;
    }
    return result;
  }

  /**
   * 向Map中增加一个Token，这些Token按照相同startOffset放在同一个列表中，
   * 
   * @param token
   */
  public void addToken(SegToken token) {
    int s = token.startOffset;
    if (!isStartExist(s)) {
      ArrayList<SegToken> newlist = new ArrayList<SegToken>();
      newlist.add(token);
      tokenListTable.put(s, newlist);
    } else {
      tokenListTable.get(s).add(token);
    }
    if (s > maxStart)
      maxStart = s;
  }

  /**
   * 获取SegGraph中不同起始（Start）位置Token类的个数，每个开始位置可能有多个Token，因此位置数与Token数并不一致
   * 
   * @return
   */
  public int getStartCount() {
    return tokenListTable.size();
  }

  /**
   * 将Map中存储的所有Token按照起始位置从小到大的方式组成一个列表
   * 
   * @return
   */
  public List<SegToken> toTokenList() {
    List<SegToken> result = new ArrayList<SegToken>();
    int s = -1, count = 0, size = tokenListTable.size();
    List<SegToken> tokenList;

    while (count < size) {
      if (isStartExist(s)) {
        tokenList = tokenListTable.get(s);
        for (SegToken st : tokenList)
          result.add(st);
        count++;
      }
      s++;
    }
    return result;
  }

  public String toString() {
    List<SegToken> tokenList = this.toTokenList();
    StringBuffer sb = new StringBuffer();
    for (SegToken t : tokenList) {
      sb.append(t + "\n");
    }
    return sb.toString();
  }
}
