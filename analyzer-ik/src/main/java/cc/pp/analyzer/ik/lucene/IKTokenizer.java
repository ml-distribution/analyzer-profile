package cc.pp.analyzer.ik.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeFactory;

import cc.pp.analyzer.ik.core.IKSegmenter;
import cc.pp.analyzer.ik.core.Lexeme;

/**
 * IK分词器 Lucene Tokenizer适配器类
 *
 * @author wanggang
 *
 */
public final class IKTokenizer extends Tokenizer {

	// IK分词器实现
	private IKSegmenter iKImplement;
	// 词元文本属性
	private CharTermAttribute termAtt;
	// 词元位移属性
	private OffsetAttribute offsetAtt;
	// 词元分类属性（该属性分类参考org.wltea.analyzer.core.Lexeme中的分类常量）
	private TypeAttribute typeAtt;
	// 记录最后一个词元的结束位置
	private int endPosition;

	/**
	 * Tokenizer适配器类构造函数
	 */
	public IKTokenizer() {
		this(Boolean.FALSE);
	}

	public IKTokenizer(boolean useSmart) {
		init(useSmart);
	}

	public IKTokenizer(AttributeFactory factory) {
		this(factory, Boolean.FALSE);
	}

	public IKTokenizer(AttributeFactory factory, boolean useSmart) {
		super(factory);
		init(useSmart);
	}

	private void init(boolean useSmart) {
		this.offsetAtt = addAttribute(OffsetAttribute.class);
		this.termAtt = addAttribute(CharTermAttribute.class);
		this.typeAtt = addAttribute(TypeAttribute.class);
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
			endPosition = nextLexeme.getEndPosition();
			//记录词元分类
			typeAtt.setType(nextLexeme.getLexemeTypeString());
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
		int finalOffset = correctOffset(this.endPosition);
		offsetAtt.setOffset(finalOffset, finalOffset);
	}

	@Override
	public void close() throws IOException {
		super.close();
	}

}
