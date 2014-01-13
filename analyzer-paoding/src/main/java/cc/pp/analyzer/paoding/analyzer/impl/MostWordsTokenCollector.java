/**
 * Copyright 2007 The Apache Software Foundation
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
package cc.pp.analyzer.paoding.analyzer.impl;

import java.util.Iterator;

import org.apache.lucene.analysis.Token;

import cc.pp.analyzer.paoding.analyzer.TokenCollector;

/**
 *
 * @author Zhiliang Wang [qieqie.wang@gmail.com]
 *
 * @since 1.1
 */
public class MostWordsTokenCollector implements TokenCollector, Iterator<Object> {

	private LinkedToken firstToken;
	private LinkedToken lastToken;

	/**
	 * Collector接口实现。<br>
	 * 构造词语Token对象，并放置在tokens中
	 *
	 */
	@Override
	public void collect(String word, int begin, int end) {
		LinkedToken tokenToAdd = new LinkedToken(word, begin, end);
		if (firstToken == null) {
			firstToken = tokenToAdd;
			lastToken = tokenToAdd;
			return;
		}
		if (tokenToAdd.compareTo(lastToken) > 0) {
			tokenToAdd.pre = lastToken;
			lastToken.next = tokenToAdd;
			lastToken = tokenToAdd;
			//
		} else {
			LinkedToken curTokenToTry = lastToken.pre;
			while (curTokenToTry != null
					&& tokenToAdd.compareTo(curTokenToTry) < 0) {
				curTokenToTry = curTokenToTry.pre;
			}
			if (curTokenToTry == null) {
				firstToken.pre = tokenToAdd;
				tokenToAdd.next = firstToken;
				firstToken = tokenToAdd;
			} else {
				tokenToAdd.next = curTokenToTry.next;
				curTokenToTry.next.pre = tokenToAdd;
				tokenToAdd.pre = curTokenToTry;
				curTokenToTry.next = tokenToAdd;

			}
		}
	}

	private LinkedToken nextLinkedToken;

	@Override
	public Iterator/* <Token> */<Object> iterator() {
		nextLinkedToken = firstToken;
		firstToken = null;
		return this;
	}

	@Override
	public boolean hasNext() {
		return nextLinkedToken != null;
	}

	@Override
	public Object next() {
		LinkedToken ret = nextLinkedToken;
		nextLinkedToken = nextLinkedToken.next;
		return ret;
	}

	@Override
	public void remove() {
	}

	private static class LinkedToken extends Token implements Comparable<Object> {
		public LinkedToken pre;
		public LinkedToken next;

		public LinkedToken(String word, int begin, int end) {
			super(word, begin, end);
		}

		@Override
		public int compareTo(Object obj) {
			LinkedToken that = (LinkedToken) obj;
			// 简单/单单/简简单单/
			if (this.endOffset() > that.endOffset())
				return 1;
			if (this.endOffset() == that.endOffset()) {
				return that.startOffset() - this.startOffset();
			}
			return -1;
		}
	}

}
