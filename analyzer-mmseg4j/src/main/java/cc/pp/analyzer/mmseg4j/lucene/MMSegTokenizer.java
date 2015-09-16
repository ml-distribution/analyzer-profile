package cc.pp.analyzer.mmseg4j.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeFactory;

import cc.pp.analyzer.mmseg4j.core.MMSeg;
import cc.pp.analyzer.mmseg4j.core.Seg;
import cc.pp.analyzer.mmseg4j.core.Word;

public class MMSegTokenizer extends Tokenizer {

	private MMSeg mmSeg;

	private CharTermAttribute termAtt;
	private OffsetAttribute offsetAtt;
	private TypeAttribute typeAtt;

	private Seg seg;

	public MMSegTokenizer(Seg seg) {
		init(seg);
	}

	public MMSegTokenizer(AttributeFactory factory, Seg seg) {
		super(factory);
		init(seg);
	}

	private void init(Seg seg) {
		this.seg = seg;
		//		mmSeg = new MMSeg(input, seg);
		this.termAtt = addAttribute(CharTermAttribute.class);
		this.offsetAtt = addAttribute(OffsetAttribute.class);
		this.typeAtt = addAttribute(TypeAttribute.class);
	}

	@Override
	public final boolean incrementToken() throws IOException {
		clearAttributes();
		Word word = mmSeg.next();
		if (word != null) {
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
		if (mmSeg == null) {
			mmSeg = new MMSeg(input, seg);
		} else {
			mmSeg.reset(input);
		}
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
