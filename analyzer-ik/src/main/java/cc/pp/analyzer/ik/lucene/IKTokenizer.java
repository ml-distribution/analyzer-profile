/**
 * IK 中文分词  版本 5.0
 * IK Analyzer release 5.0
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 源代码由林良益(linliangyi2005@gmail.com)提供
 * 版权声明 2012，乌龙茶工作室
 * provided by Linliangyi and copyright 2012 by Oolong studio
 *

 *
 */
package cc.pp.analyzer.ik.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.AttributeFactory;

import cc.pp.analyzer.ik.core.IKSegmenter;
import cc.pp.analyzer.ik.core.Lexeme;

/**
 * IK分词器 Lucene Tokenizer适配器类
 *  兼容lucene4.6
 */
public final class IKTokenizer extends Tokenizer {

	// IK分词器实现
	private IKSegmenter iKImplement;
	// 词元文本属性
	private CharTermAttribute termAtt;
	// 词元位移属性
	private OffsetAttribute offsetAtt;
	// 记录最后一个词元的结束位置
	private int finalOffset;

	/**
	 * Lucene 3.5 Tokenizer适配器类构造函数
	 * @param in
	 * @param useSmart
	 */
	public IKTokenizer(boolean useSmart) {
		init(useSmart);
	}

	public IKTokenizer(AttributeFactory factory, boolean useSmart) {
		super(factory);
		init(useSmart);
	}

	private void init(boolean useSmart) {
		this.offsetAtt = addAttribute(OffsetAttribute.class);
		this.termAtt = addAttribute(CharTermAttribute.class);
		this.iKImplement = new IKSegmenter(input, useSmart);
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.analysis.TokenStream#incrementToken()
	 */
	@Override
	public boolean incrementToken() throws IOException {
		// 清除所有的词元属性
		clearAttributes();
		Lexeme nextLexeme = iKImplement.next();
		if (nextLexeme != null) {
			// 将Lexeme转成Attributes
			// 设置词元文本
			termAtt.append(nextLexeme.getLexemeText());
			// 设置词元长度
			termAtt.setLength(nextLexeme.getLength());
			// 设置词元位移
			offsetAtt.setOffset(nextLexeme.getBeginPosition(), nextLexeme.getEndPosition());
			// 记录分词的最后位置
			finalOffset = nextLexeme.getEndPosition();
			// 返会true告知还有下个词元
			return true;
		}
		// 返会false告知词元输出完毕
		return false;
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		iKImplement.reset(input);
	}

	@Override
	public final void end() throws IOException {
		super.end();
		// 设置最后的偏移量
		offsetAtt.setOffset(finalOffset, finalOffset);
	}

	@Override
	public void close() throws IOException {
		super.close();
	}

}
