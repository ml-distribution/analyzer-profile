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
package cc.pp.analyzer.paoding.analyzer;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

import cc.pp.analyzer.paoding.analyzer.impl.MostWordsTokenCollector;
import cc.pp.analyzer.paoding.knife.Beef;
import cc.pp.analyzer.paoding.knife.Collector;
import cc.pp.analyzer.paoding.knife.Knife;
import cc.pp.analyzer.paoding.knife.Paoding;


/**
 * PaodingTokenizer是基于“庖丁解牛”框架的TokenStream实现，为PaodingAnalyzer使用。
 * <p>
 *
 * @author Zhiliang Wang [qieqie.wang@gmail.com]
 *
 * @see Beef
 * @see Knife
 * @see Paoding
 * @see Tokenizer
 * @see PaodingAnalyzer
 *
 * @see Collector
 * @see TokenCollector
 * @see MAxTokenCollector
 * @see MostWordsTokenCollector
 *
 * @since 1.0
 */
public final class PaodingTokenizer extends TokenStream implements Collector {

	// -------------------------------------------------

	/**
	 * 文本字符源
	 *
	 * @see #next()
	 */
	private final Reader input;

	/**
	 * 从input读入的总字符数
	 */
	private int inputLength;

	/**
	 *
	 */
	private static final int bufferLength = 128;

	/**
	 * 接收来自{@link #input}的文本字符
	 *
	 * @see #next()
	 */
	private final char[] buffer = new char[bufferLength];

	/**
	 * {@link buffer}[0]在{@link #input}中的偏移
	 *
	 * @see #collect(String, int, int)
	 * @see #next()
	 */
	private int offset;

	/**
	 *
	 */
	private final Beef beef = new Beef(buffer, 0, 0);

	/**
	 *
	 */
	private int dissected;

	/**
	 * 用于分解beef中的文本字符，由PaodingAnalyzer提供
	 *
	 * @see #next()
	 */
	private final Knife knife;

	/**
	 *
	 */
	private TokenCollector tokenCollector;

	/**
	 * tokens迭代器，用于next()方法顺序读取tokens中的Token对象
	 *
	 * @see #tokens
	 * @see #next()
	 */
	private Iterator/* <Token> */<?> tokenIteractor;

	// -------------------------------------------------

	/**
	 *
	 * @param input
	 * @param knife
	 * @param tokenCollector
	 */
	public PaodingTokenizer(Reader input, Knife knife, TokenCollector tokenCollector) {
		this.input = input;
		this.knife = knife;
		this.tokenCollector = tokenCollector;
	}

	// -------------------------------------------------

	public TokenCollector getTokenCollector() {
		return tokenCollector;
	}

	public void setTokenCollector(TokenCollector tokenCollector) {
		this.tokenCollector = tokenCollector;
	}

	// -------------------------------------------------


	@Override
	public void collect(String word, int offset, int end) {
		tokenCollector.collect(word, this.offset + offset, this.offset + end);
	}

	// -------------------------------------------------
	@Override
	public Token next() throws IOException {
		// 已经穷尽tokensIteractor的Token对象，则继续请求reader流入数据
		while (tokenIteractor == null || !tokenIteractor.hasNext()) {
			//System.out.println(dissected);
			int read = 0;
			int remainning = -1;//重新从reader读入字符前，buffer中还剩下的字符数，负数表示当前暂不需要从reader中读入字符
			if (dissected >= beef.length()) {
				remainning = 0;
			}
			else if (dissected < 0){
				remainning = bufferLength + dissected;
			}
			if (remainning >= 0) {
				if (remainning > 0) {
					System.arraycopy(buffer, -dissected, buffer, 0, remainning);
				}
				read = input.read(buffer, remainning, bufferLength - remainning);
				inputLength += read;
				int charCount = remainning + read;
				if (charCount < 0) {
					// reader已尽，按接口next()要求返回null.
					return null;
				}
				if (charCount < bufferLength) {
					buffer[charCount ++] = 0;
				}
				// 构造“牛”，并使用knife“解”之
				beef.set(0, charCount);
				offset += Math.abs(dissected);
				//offset -= remainning;
				dissected = 0;
			}
			dissected = knife.dissect(this, beef, dissected);
//			offset += read;// !!!
			tokenIteractor = tokenCollector.iterator();
		}
		// 返回tokensIteractor下一个Token对象
		return (Token) tokenIteractor.next();
	}

	public int getInputLength() {
		return inputLength;
	}

	// -------------------------------------------------

	@Override
	public void close() throws IOException {
		super.close();
		input.close();
	}

}
