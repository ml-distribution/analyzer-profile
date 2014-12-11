package org.ictclas4j.segment;

import java.util.ArrayList;

import org.ictclas4j.bean.Atom;
import org.ictclas4j.bean.Dictionary;
import org.ictclas4j.bean.MidResult;
import org.ictclas4j.bean.SegNode;
import org.ictclas4j.bean.SegResult;
import org.ictclas4j.bean.Sentence;
import org.ictclas4j.utility.DebugUtil;
import org.ictclas4j.utility.POSTag;
import org.ictclas4j.utility.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Segment {

	private static Logger logger = LoggerFactory.getLogger(Segment.class);

	private final Dictionary coreDict;

	private final Dictionary bigramDict;

	private final PosTagger personTagger;

	private final PosTagger transPersonTagger;

	private final PosTagger placeTagger;

	private final PosTagger lexTagger;

	private int segPathCount = 1;// 分词路径的数目

	public Segment(int segPathCount) {

		this.segPathCount = segPathCount;
		logger.info("Load coreDict  ...");
		coreDict = new Dictionary("data/coreDict.dct");

		logger.info("Load bigramDict ...");
		bigramDict = new Dictionary("data/bigramDict.dct");

		logger.info("Load tagger dict ...");
		personTagger = new PosTagger(Utility.TAG_TYPE.TT_PERSON, "data/nr", coreDict);
		transPersonTagger = new PosTagger(Utility.TAG_TYPE.TT_TRANS_PERSON, "data/tr", coreDict);
		placeTagger = new PosTagger(Utility.TAG_TYPE.TT_TRANS_PERSON, "data/ns", coreDict);
		lexTagger = new PosTagger(Utility.TAG_TYPE.TT_NORMAL, "data/lexical", coreDict);
		logger.info("Load dict is over");
	}

	public SegResult split(String src) {

		SegResult sr = new SegResult(src);// 分词结果
		String finalResult = null;

		if (src != null) {
			finalResult = "";
			int index = 0;
			String midResult = null;
			sr.setRawContent(src);
			SentenceSeg ss = new SentenceSeg(src);
			ArrayList<Sentence> sens = ss.getSens();

			for (Sentence sen : sens) {
				logger.debug("Sentence:{}", sen);
				long start = System.currentTimeMillis();
				MidResult mr = new MidResult();
				mr.setIndex(index++);
				mr.setSource(sen.getContent());
				if (sen.isSeg()) {

					// 原子分词
					AtomSeg as = new AtomSeg(sen.getContent());
					ArrayList<Atom> atoms = as.getAtoms();
					mr.setAtoms(atoms);
					System.err.println("[atom time]:" + (System.currentTimeMillis() - start));
					start = System.currentTimeMillis();

					// 生成分词图表,先进行初步分词，然后进行优化，最后进行词性标记
					SegGraph segGraph = GraphGenerate.generate(atoms, coreDict);
					mr.setSegGraph(segGraph.getSnList());
					// 生成二叉分词图表
					SegGraph biSegGraph = GraphGenerate.biGenerate(segGraph, coreDict, bigramDict);
					mr.setBiSegGraph(biSegGraph.getSnList());
					System.err.println("[graph time]:" + (System.currentTimeMillis() - start));
					start = System.currentTimeMillis();

					// 求N最短路径
					NShortPath nsp = new NShortPath(biSegGraph, segPathCount);
					ArrayList<ArrayList<Integer>> bipath = nsp.getPaths();
					mr.setBipath(bipath);
					System.err.println("[NSP time]:" + (System.currentTimeMillis() - start));
					start = System.currentTimeMillis();

					for (ArrayList<Integer> onePath : bipath) {
						// 得到初次分词路径
						ArrayList<SegNode> segPath = getSegPath(segGraph, onePath);
						ArrayList<SegNode> firstPath = AdjustSeg.firstAdjust(segPath);
						String firstResult = outputResult(firstPath);
						mr.addFirstResult(firstResult);
						System.err.println("[first time]:" + (System.currentTimeMillis() - start));
						start = System.currentTimeMillis();

						// 处理未登陆词，进对初次分词结果进行优化
						SegGraph optSegGraph = new SegGraph(firstPath);
						ArrayList<SegNode> sns = clone(firstPath);
						personTagger.recognition(optSegGraph, sns);
						transPersonTagger.recognition(optSegGraph, sns);
						placeTagger.recognition(optSegGraph, sns);
						mr.setOptSegGraph(optSegGraph.getSnList());
						System.err.println("[unknown time]:" + (System.currentTimeMillis() - start));
						start = System.currentTimeMillis();

						// 根据优化后的结果，重新进行生成二叉分词图表
						SegGraph optBiSegGraph = GraphGenerate.biGenerate(optSegGraph, coreDict, bigramDict);
						mr.setOptBiSegGraph(optBiSegGraph.getSnList());

						// 重新求取N－最短路径
						NShortPath optNsp = new NShortPath(optBiSegGraph, segPathCount);
						ArrayList<ArrayList<Integer>> optBipath = optNsp.getPaths();
						mr.setOptBipath(optBipath);

						// 生成优化后的分词结果，并对结果进行词性标记和最后的优化调整处理
						ArrayList<SegNode> adjResult = null;
						for (ArrayList<Integer> optOnePath : optBipath) {
							ArrayList<SegNode> optSegPath = getSegPath(optSegGraph, optOnePath);
							lexTagger.recognition(optSegPath);
							String optResult = outputResult(optSegPath);
							mr.addOptResult(optResult);
							adjResult = AdjustSeg.finaAdjust(optSegPath, personTagger, placeTagger);
							String adjrs = outputResult(adjResult);
							System.err.println("[last time]:" + (System.currentTimeMillis() - start));
							start = System.currentTimeMillis();
							if (midResult == null)
								midResult = adjrs;
							break;
						}
					}
					sr.addMidResult(mr);
				} else
					midResult = sen.getContent();
				finalResult += midResult;
				midResult = null;
			}

			sr.setFinalResult(finalResult);
			DebugUtil.output2html(sr);
			logger.info(finalResult);
		}

		return sr;
	}

	private ArrayList<SegNode> clone(ArrayList<SegNode> sns) {
		ArrayList<SegNode> result = null;
		if (sns != null && sns.size() > 0) {
			result = new ArrayList<SegNode>();
			for (SegNode sn : sns)
				result.add(sn.clone());
		}
		return result;
	}

	// 根据二叉分词路径生成分词路径
	private ArrayList<SegNode> getSegPath(SegGraph sg, ArrayList<Integer> bipath) {

		ArrayList<SegNode> path = null;

		if (sg != null && bipath != null) {
			ArrayList<SegNode> sns = sg.getSnList();
			path = new ArrayList<SegNode>();

			for (int index : bipath)
				path.add(sns.get(index));

		}
		return path;
	}

	// 根据分词路径生成分词结果
	private String outputResult(ArrayList<SegNode> wrList) {
		String result = null;
		String temp = null;
		char[] pos = new char[2];
		if (wrList != null && wrList.size() > 0) {
			result = "";
			for (int i = 0; i < wrList.size(); i++) {
				SegNode sn = wrList.get(i);
				if (sn.getPos() != POSTag.SEN_BEGIN && sn.getPos() != POSTag.SEN_END) {
					int tag = Math.abs(sn.getPos());
					pos[0] = (char) (tag / 256);
					pos[1] = (char) (tag % 256);
					temp = "" + pos[0];
					if (pos[1] > 0)
						temp += "" + pos[1];
					result += sn.getSrcWord() + "/" + temp + " ";
				}
			}
		}

		return result;
	}

	public void setSegPathCount(int segPathCount) {
		this.segPathCount = segPathCount;
	}

}
