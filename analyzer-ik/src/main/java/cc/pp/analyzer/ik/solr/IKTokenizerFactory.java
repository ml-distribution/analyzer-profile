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
package cc.pp.analyzer.ik.solr;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoaderAware;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeSource.AttributeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.pp.analyzer.ik.lucene.IKTokenizer;

/**
 * IK中文分词
 * Solr 3.6.1 分词器工厂实现
 *
 * 2012-3-6
 *
 */
public class IKTokenizerFactory extends TokenizerFactory implements ResourceLoaderAware {

	private static Logger logger = LoggerFactory.getLogger(IKTokenizerFactory.class);

	/* 线程共享 */
	private final ThreadLocal<IKTokenizer> tokenizerLocal = new ThreadLocal<>();
	private boolean useSmart = false;

	public IKTokenizerFactory(Map<String, String> args) {
		super(args);
		// args不消耗，不知啥原因；官方自带的分词器可以消耗这些参数
		//		if (!args.isEmpty()) {
		//			throw new IllegalArgumentException("Unknown parameters: " + args);
		//		}
	}

	@Override
	public Tokenizer create(AttributeFactory factory, Reader input) {
		IKTokenizer tokenizer = tokenizerLocal.get();
		if (tokenizer == null) {
			tokenizer = newTokenizer(input);
		} else {
			try {
				tokenizer.setReader(input);
			} catch (IOException e) {
				tokenizer = newTokenizer(input);
				logger.info("IKTokenizer.reset i/o error by: " + e.getMessage());
			}
		}

		return tokenizer;
	}

	private IKTokenizer newTokenizer(Reader input) {
		IKTokenizer tokenizer = new IKTokenizer(input, useSmart);
		tokenizerLocal.set(tokenizer);
		return tokenizer;
	}

	@Override
	public void inform(ResourceLoader loader) {
		String useSmartParam = getOriginalArgs().get("useSmart");
		useSmart = (useSmartParam != null ? Boolean.parseBoolean(useSmartParam) : false);
		logger.info("IKTokenizerFactory loading resource ...");
	}

}
