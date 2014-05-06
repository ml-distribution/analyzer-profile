package cc.pp.analyzer.fudan.core;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Set;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.fudan.ml.classifier.struct.inf.ConstraintViterbi;
import edu.fudan.ml.classifier.struct.inf.LinearViterbi;
import edu.fudan.ml.types.Dictionary;
import edu.fudan.ml.types.Instance;
import edu.fudan.nlp.cn.tag.AbstractTagger;
import edu.fudan.nlp.pipe.Pipe;
import edu.fudan.nlp.pipe.SeriesPipes;
import edu.fudan.nlp.pipe.seq.DictLabel;
import edu.fudan.nlp.pipe.seq.DictPOSLabel;
import edu.fudan.util.exception.LoadModelException;

/**
 * 词性标注器
 * 先分词，再做词性标注
 * @version 1.0
 */
public class POSTagger extends AbstractTagger {

	private static Logger logger = LoggerFactory.getLogger(POSTagger.class);

	private DictLabel dictPipe = null;
	private Pipe oldfeaturePipe = null;

	/**
	 * 分词模型
	 */
	public CWSTagger cws;

	/**
	 * 构造函数
	 * @param cwsmodel 分词模型文件
	 * @param str 词性模型文件
	 * @throws Exception
	 */
	public POSTagger(String cwsmodel, String str) throws Exception {
		super(str);
		cws = new CWSTagger(cwsmodel);
	}

	//分词词典也被dict指定
	public POSTagger(String cwsmodel, String str, Dictionary dict) throws Exception {
		super(str);
		cws = new CWSTagger(cwsmodel);
		setDictionary(dict, true);
	}

	/**
	 * 构造函数
	 * @param str 词性模型文件
	 * @throws Exception
	 */
	public POSTagger(String str) throws Exception {
		super(str);
		logger.info("只能处理分好词的句子");
	}

	/**
	 * 不建立分词模型，只能处理分好词的句子
	 * @param str
	 * @param dict
	 * @throws Exception
	 */
	public POSTagger(String str, Dictionary dict) throws Exception {
		super(str);
		setDictionary(dict, false);
	}

	/**
	 * 构造函数
	 * @param cws 分词模型
	 * @param str 词性模型文件
	 * @throws Exception
	 */
	public POSTagger(CWSTagger cws, String str) throws Exception {
		super(str);
		if (cws == null)
			throw new Exception("分词模型不能为空");
		this.cws = cws;
	}

	public POSTagger(CWSTagger cws, String str, Dictionary dict, boolean isSetSegDict) throws Exception {
		super(str);
		if (cws == null)
			throw new Exception("分词模型不能为空");
		this.cws = cws;
		setDictionary(dict, isSetSegDict);
	}

	/**
	 * 设置词典, 参数指定是否同时设置分词词典
	 * @param dict 词典
	 * @throws LoadModelException
	 */
	public void setDictionary(Dictionary dict, boolean isSetSegDict) throws LoadModelException {
		if (cws != null && isSetSegDict)
			cws.setDictionary(dict);

		if (dictPipe == null) {
			dictPipe = new DictPOSLabel(dict, labels);
			oldfeaturePipe = featurePipe;
			featurePipe = new SeriesPipes(new Pipe[] { dictPipe, featurePipe });
			LinearViterbi dv = new ConstraintViterbi((LinearViterbi) cl.getInferencer());
			cl.setInferencer(dv);
		} else {
			dictPipe.setDict(dict);
		}
	}

	/**
	 * 移除词典, 参数指定是否同时移除分词词典
	 */
	public void removeDictionary(boolean isRemoveSegDict) {
		if (cws != null && isRemoveSegDict)
			cws.removeDictionary();

		if (oldfeaturePipe != null) {
			featurePipe = oldfeaturePipe;
		}
		LinearViterbi dv = new LinearViterbi((LinearViterbi) cl.getInferencer());
		cl.setInferencer(dv);

		dictPipe = null;
		oldfeaturePipe = null;
	}

	/**
	 * @param src 字符串
	 * @return
	 */
	public String[][] tag2Array(String src) {
		if (src == null || src.length() == 0)
			return null;
		if (cws == null) {
			logger.info("只能处理分好词的句子");
			return null;
		}
		String[] words = cws.tag2Array(src);
		if (words.length == 0)
			return null;
		String[] target = null;
		Instance inst = new Instance(words);
		doProcess(inst);
		int[] pred = (int[]) cl.predict(inst).getPredAt(0);
		target = labels.lookupString(pred);

		String[][] tags = new String[2][];
		tags[0] = words;
		tags[1] = target;
		return tags;
	}

	public String[][][] tag2DoubleArray(String src) {
		if (cws == null) {
			logger.info("只能处理分好词的句子");
			return null;
		}
		String[][] words = cws.tag2DoubleArray(src);
		String[][][] tags = new String[words.length][2][];

		for (int i = 0; i < words.length; i++) {
			tags[i][0] = words[i];
			tags[i][1] = tagSeged(words[i]);
		}

		return tags;
	}

	@Override
	public String tag(String src) {
		if (src == null || src.length() == 0)
			return src;
		if (cws == null) {
			logger.info("只能处理分好词的句子");
			return null;
		}
		String[] words = cws.tag2Array(src);
		if (words.length == 0)
			return src;
		StringBuilder sb = new StringBuilder();

		Instance inst = new Instance(words);
		doProcess(inst);
		int[] pred = (int[]) cl.predict(inst).getPredAt(0);
		String[] target = labels.lookupString(pred);
		for (int j = 0; j < words.length; j++) {
			sb.append(words[j]);
			sb.append("/");
			sb.append(target[j]);
			if (j < words.length - 1)
				sb.append(delim);
		}
		return sb.toString();
	}

	/**
	 * 处理分好词的句子
	 */
	public String[] tagSeged(String[] src) {
		String[] target = null;
		try {
			Instance inst = new Instance(src);
			doProcess(inst);
			int[] pred = (int[]) cl.predict(inst).getPredAt(0);
			target = labels.lookupString(pred);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return target;
	}

	/**
	 * 处理分好词的句子
	 */
	public String tagSeged2String(String[] src) {
		StringBuilder sb = new StringBuilder();
		String[] target = tagSeged(src);
		for (int j = 0; j < target.length; j++) {
			sb.append(target[j]);
			if (j < target.length - 1)
				sb.append(" ");
		}
		return sb.toString();
	}

	/**
	 * 得到支持的词性标签集合
	 * @return 词性标签集合
	 */
	public Set<String> getSupportedTags() {
		return labels.toSet();
	}

	public static void main(String[] args) throws Exception {
		Options opt = new Options();

		opt.addOption("h", false, "Print help for this application");
		opt.addOption("f", false, "segment file. Default string mode.");
		opt.addOption("s", false, "segment string");
		BasicParser parser = new BasicParser();
		CommandLine cl = parser.parse(opt, args);

		if (args.length == 0 || cl.hasOption('h')) {
			HelpFormatter f = new HelpFormatter();
			f.printHelp("Tagger:\n"
					+ "java edu.fudan.nlp.tag.POSTagger -f cws_model_file pos_model_file input_file output_file;\n"
					+ "java edu.fudan.nlp.tag.POSTagger -s cws_model_file pos_model_file string_to_segement", opt);
			return;
		}
		String[] arg = cl.getArgs();
		String cws_model_file, pos_model_file;
		String input;
		String output = null;
		if (cl.hasOption("f") && arg.length == 4) {
			cws_model_file = arg[0];
			pos_model_file = arg[1];
			input = arg[2];
			output = arg[3];
		} else if (arg.length == 3) {
			cws_model_file = arg[0];
			pos_model_file = arg[1];
			input = arg[2];
		} else {
			logger.error("paramenters format error!");
			logger.error("Print option \"-h\" for help.");
			return;
		}
		POSTagger pos = new POSTagger(cws_model_file, pos_model_file);
		if (cl.hasOption("f")) {
			String s = pos.tagFile(input);
			OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(output), "utf8");
			w.write(s);
			w.close();
		} else {
			String s = pos.tag(input);
			logger.info(s);
		}
	}

}
