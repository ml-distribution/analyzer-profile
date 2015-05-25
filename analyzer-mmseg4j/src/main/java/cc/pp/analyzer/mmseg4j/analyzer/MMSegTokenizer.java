package cc.pp.analyzer.mmseg4j.analyzer;

import java.io.IOException;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeFactory;

import cc.pp.analyzer.mmseg4j.MMSeg;
import cc.pp.analyzer.mmseg4j.Seg;
import cc.pp.analyzer.mmseg4j.Word;

public class MMSegTokenizer extends Tokenizer {

	private MMSeg mmSeg;

	private CharTermAttribute termAtt;
	private OffsetAttribute offsetAtt;
	private TypeAttribute typeAtt;

	/**
	 * 为适应solr4.6添加的
	 */
	public MMSegTokenizer(Seg seg) {
		init(seg);
	}

	public MMSegTokenizer(AttributeFactory factory, Seg seg) {
		super(factory);
		init(seg);
	}

	private void init(Seg seg) {
		mmSeg = new MMSeg(input, seg);
		this.termAtt = addAttribute(CharTermAttribute.class);
		this.offsetAtt = addAttribute(OffsetAttribute.class);
		this.typeAtt = addAttribute(TypeAttribute.class);
	}

	/*//lucene 2.9 以下
	 	public Token next(Token reusableToken) throws IOException {
			Token token = null;
			Word word = mmSeg.next();
			if(word != null) {
				//lucene 2.3
				reusableToken.clear();
				reusableToken.setTermBuffer(word.getSen(), word.getWordOffset(), word.getLength());
				reusableToken.setStartOffset(word.getStartOffset());
				reusableToken.setEndOffset(word.getEndOffset());
				reusableToken.setType(word.getType());

				token = reusableToken;

				//lucene 2.4
				//token = reusableToken.reinit(word.getSen(), word.getWordOffset(), word.getLength(), word.getStartOffset(), word.getEndOffset(), word.getType());
			}

			return token;
		}*/

	//lucene 2.9/3.0
	@Override
	public final boolean incrementToken() throws IOException {
		clearAttributes();
		Word word = mmSeg.next();
		if (word != null) {
			//lucene 3.0
			//termAtt.setTermBuffer(word.getSen(), word.getWordOffset(), word.getLength());
			//lucene 3.1
			termAtt.copyBuffer(word.getSen(), word.getWordOffset(), word.getLength());
			offsetAtt.setOffset(word.getStartOffset(), word.getEndOffset());
			typeAtt.setType(word.getType());
			return true;
		} else {
			end();
			return false;
		}
	}

	@Override
	public void reset() throws IOException {
		super.reset(); // 为适应solr4.6添加的
		//lucene 4.0
		//org.apache.lucene.analysis.Tokenizer.setReader(Reader)
		//setReader 自动被调用, input 自动被设置。
		mmSeg.reset(input);
	}

	@Override
	public void end() throws IOException {
		super.end();
	}

	@Override
	public void close() throws IOException {
		super.close();
	}

}
