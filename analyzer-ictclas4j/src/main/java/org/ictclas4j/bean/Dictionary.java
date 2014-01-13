package org.ictclas4j.bean;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.ictclas4j.utility.GFCommon;
import org.ictclas4j.utility.GFString;
import org.ictclas4j.utility.Utility;


public class Dictionary {
	/**
	 * 词典表,共6768个,GB2312编码
	 */
	public ArrayList<WordTable> wts;

	/**
	 * 词典修改表
	 */
	public ArrayList<ModifyTable> mts;

	static Logger logger = Logger.getLogger(Dictionary.class);

	public Dictionary() {
		init();

	}

	public Dictionary(String filename) {
		init();
		load(filename);
	}

	public void init() {
		wts = new ArrayList<WordTable>();
		mts = new ArrayList<ModifyTable>();
		for (int i = 0; i < Utility.CC_NUM; i++) {
			wts.add(new WordTable());
			mts.add(new ModifyTable());
		}
	}

	public boolean load(String filename) {
		return load(filename, false);
	}

	/**
	 * 从词典表中加载词条.共6768个大的数据块(包括5个非汉字字符),每个大数据块包括若干个小数据块,
	 * 每个小数据块为一个词条,该数据块中每个词条都是共一个字开头的.
	 *
	 * @param filename
	 *            核心词典文件名
	 * @param isReset
	 *            是否要重置
	 * @return
	 */
	public boolean load(String filename, boolean isReset) {
		File file;

		int[] nBuffer = new int[3];

		file = new File(filename);
		if (!file.canRead())
			return false;// fail while opening the file

		try {
			delModified();

			DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
			for (int i = 0; i < Utility.CC_NUM; i++) {
				// logger.debug("块" + i);
				// 词典库在写二进制数据时采用低位优先(小头在前)方式,需要转换一下
				int count = GFCommon.bytes2int(Utility.readBytes(in, 4), false);
				// logger.debug(" count:" + count);
				wts.get(i).setCount(count);
				if (count <= 0)
					continue;

				WordItem[] wis = new WordItem[count];
				for (int j = 0; j < count; j++) {
					nBuffer[0] = GFCommon.bytes2int(Utility.readBytes(in, 4), false);
					nBuffer[1] = GFCommon.bytes2int(Utility.readBytes(in, 4), false);
					nBuffer[2] = GFCommon.bytes2int(Utility.readBytes(in, 4), false);

					// String print = " wordLen:" + nBuffer[1] + " frequency:" +
					// nBuffer[0] + " handle:" + nBuffer[2];

					WordItem ti = new WordItem();
					if (nBuffer[1] > 0)// String length is more than 0
					{
						byte[] word = Utility.readBytes(in, nBuffer[1]);
						ti.setWord(new String(word, "GBK"));

					} else
						ti.setWord("");

					// print += " word:(" + Utility.getGB(i) + ")" +
					// ti.getWord();
					// logger.debug(print);

					if (isReset)// Reset the frequency
						ti.setFreq(0);
					else
						ti.setFreq(nBuffer[0]);
					ti.setLen(nBuffer[1] / 2);
					ti.setHandle(nBuffer[2]);
					wis[j] = ti;
				}
				wts.get(i).setWords(wis);
			}

			in.close();
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
		return true;
	}

	/**
	 * 保存词典表.如果有修改的词条，则先要对词典表进行更新才能把内容写入文件
	 *
	 * @param filename
	 * @return
	 */
	public boolean save(String filename) {
		File file;
		int j, k;
		int[] nBuffer = new int[3];

		file = new File(filename);
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
			for (int i = 0; i < Utility.CC_NUM; i++) {
				if (mts != null) {// Modification made
					int nCount = wts.get(i).getCount() + mts.get(i).getCount() - mts.get(i).getDelete();
					out.write(GFCommon.int2bytes(nCount, false));

					j = 0;
					k = 0;
					// Output to the file after comparision
					for (; j < mts.get(i).getCount() && k < wts.get(i).getCount();) {
						WordItem mwi = mts.get(i).getWords().get(j);
						WordItem wi = wts.get(i).getWords().get(k);

						if (mwi.getLen() < wi.getLen() || (strEqual(mwi.getWord(), wi.getWord()))
								&& mwi.getHandle() < wi.getHandle()) {
							// Output the modified data to the file
							nBuffer[0] = mwi.getFreq();
							nBuffer[1] = mwi.getLen();
							nBuffer[2] = mwi.getHandle();
							for (int n : nBuffer)
								out.write(GFCommon.int2bytes(n, false));
							if (nBuffer[1] > 0)// String length is more than 0
								out.write(mwi.getWord().getBytes());

							j++;
						} else if (mwi.getFreq() == -1) {
							// The item has been removed,so skip it
							k++;
						} else if (mwi.getLen() > wi.getLen() || strEqual(mwi.getWord(), wi.getWord())
								&& mwi.getHandle() > wi.getHandle()) {
							// Output the index table data to the file
							nBuffer[0] = wi.getFreq();
							nBuffer[1] = wi.getLen();
							nBuffer[2] = wi.getHandle();
							for (int n : nBuffer)
								out.write(GFCommon.int2bytes(n, false));
							if (nBuffer[1] > 0)// String length is more than 0
								out.write(wi.getWord().getBytes());

							k++;// Get next item in the original table.
						}
					}

					if (k < wts.get(i).getCount()) {
						for (; k < wts.get(i).getCount();) {
							WordItem wi = wts.get(i).getWords().get(k);

							// Has been deleted
							if (wi.getFreq() != -1) {
								nBuffer[0] = wi.getFreq();
								nBuffer[1] = wi.getLen();
								nBuffer[2] = wi.getHandle();
								for (int n : nBuffer)
									out.write(GFCommon.int2bytes(n, false));

								// String length is more than 0
								if (nBuffer[1] > 0)
									out.write(wi.getWord().getBytes());
							}

							k++;// Get next item in the original table.
						}
					} else
						// //No Modification,Add the rest data to the file.
						for (; j < mts.get(i).getCount();) {
							WordItem wi = mts.get(i).getWords().get(j);
							nBuffer[0] = wi.getFreq();
							nBuffer[1] = wi.getLen();
							nBuffer[2] = wi.getHandle();
							for (int n : nBuffer)
								out.write(GFCommon.int2bytes(n, false));
							if (nBuffer[1] > 0)// String length is more than 0
								out.write(wi.getWord().getBytes());
						}
				} else {
					out.writeInt(wts.get(i).getCount());
					for (j = 0; j < wts.get(i).getCount(); j++) {
						WordItem wi = wts.get(i).getWords().get(j);
						nBuffer[0] = wi.getFreq();
						nBuffer[1] = wi.getLen();
						nBuffer[2] = wi.getHandle();
						for (int n : nBuffer)
							out.write(GFCommon.int2bytes(n, false));
						if (nBuffer[1] > 0)// String length is more than 0
							out.write(wi.getWord().getBytes());
					}
				}
			}
			out.close();
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
		return true;
	}

	/**
	 * 向词典库中添加词条.添加时只是先把词条放到修改表中，保存时才真正把添加的词条写入词典库中
	 *
	 * @param word
	 *            词
	 * @param handle
	 *            句柄
	 * @param frequency
	 *            频度
	 * @return
	 */
	public boolean addItem(String word, int handle, int frequency) {

		Preword pw = preProcessing(word);
		if (pw != null & pw.getWord() != null) {
			int found = findInOriginalTable(pw.getIndex(), pw.getRes(), handle);
			if (found >= 0) {
				WordItem wi = wts.get(pw.getIndex()).getWords().get(found);
				if (wi.getFreq() != -1) {
					wi.setFreq(frequency);
					if (mts == null)
						mts = new ArrayList<ModifyTable>(Utility.CC_NUM);

					mts.get(pw.getIndex()).setDelete(mts.get(pw.getIndex()).getDelete() - 1);
				} else
					wi.setFreq(wi.getFreq() + frequency);
				return true;
			}

			if (mts == null)
				mts = new ArrayList<ModifyTable>(Utility.CC_NUM);

			int found2 = findInModifyTable(pw.getIndex(), pw.getRes(), handle);
			if (found2 >= 0) {
				WordItem wi = mts.get(pw.getIndex()).getWords().get(found2);
				wi.setFreq(wi.getFreq() + frequency);
				return true;
			}

			WordItem wi = new WordItem();
			wi.setFreq(frequency);
			wi.setHandle(handle);
			wi.setLen(pw.getRes().length());
			wi.setWord(pw.getRes());

			ModifyTable mt = mts.get(pw.getIndex());
			mt.getWords().add(found2, wi);
			mt.setCount(mt.getCount() + 1);
			return true;
		}
		return false;
	}

	public boolean delItem(String word, int handle) {
		Preword pw = preProcessing(word);
		if (pw != null & pw.getWord() != null) {
			int found = findInOriginalTable(pw.getIndex(), pw.getRes(), handle);
			if (found >= 0) {
				if (mts == null)
					mts = new ArrayList<ModifyTable>(Utility.CC_NUM);

				ModifyTable mt = mts.get(pw.getIndex());
				WordItem wi = mt.getWords().get(found);
				wi.setFreq(-1);
				mt.setCount(mt.getDelete() + 1);

				if (handle == -1) {
					for (int i = found; i < mt.getCount() && strEqual(mt.getWords().get(i).getWord(), pw.getRes()); i++) {
						WordItem wi2 = mt.getWords().get(i);
						wi2.setFreq(-1);
						mt.setDelete(mt.getDelete() + 1);

					}

				}

				return true;
			}

			int found2 = findInModifyTable(pw.getIndex(), pw.getRes(), handle);
			if (found2 >= 0) {
				ModifyTable mt = mts.get(pw.getIndex());
				ArrayList<WordItem> wis = mt.getWords();
				for (int i = found2; i < wis.size(); i++) {
					WordItem wi = wis.get(i);
					if (strEqual(wi.getWord(), pw.getRes()) && (wi.getHandle() == handle || handle < 0)) {
						wis.remove(wi);
						mt.setCount(mt.getCount() - 1);
						i--;
					}
				}

				return true;
			}
		}

		return false;
	}

	// The data for modify
	protected boolean delModified() {
		mts = null;
		return true;
	}

	public boolean isExist(String word, int handle) {
		if (word != null) {
			Preword pw = preProcessing(word);
			if (pw != null) {
				if (findInOriginalTable(pw.getIndex(), pw.getRes(), handle) >= 0
						|| findInModifyTable(pw.getIndex(), pw.getRes(), handle) >= 0)
					return true;
			}
		}

		return false;
	}

	public ArrayList<WordItem> getHandle(String word) {
		ArrayList<WordItem> result = null;

		if (word != null) {
			result = new ArrayList<WordItem>();
			Preword pw = preProcessing(word);
			if (pw != null && pw.getWord() != null) {
				int found = findInOriginalTable(pw.getIndex(), pw.getRes(), -1);
				if (found >= 0) {
					WordItem wi = new WordItem();
					WordItem wi2 = wts.get(pw.getIndex()).getWords().get(found);
					wi.setHandle(wi2.getHandle());
					wi.setFreq(wi2.getFreq());
					result.add(wi);

					int temp = found + 1;
					WordTable wt = wts.get(pw.getIndex());
					while (temp < wt.getCount() && strEqual(wt.getWords().get(temp).getWord(), pw.getRes())) {
						wi = new WordItem();
						wi.setHandle(wt.getWords().get(temp).getHandle());
						wi.setFreq(wt.getWords().get(temp).getFreq());
						wi.setWord(word);
						result.add(wi);
						temp++;
					}

					return result;
				}

				int found2 = findInModifyTable(pw.getIndex(), pw.getRes(), -1);
				if (found2 >= 0) {
					ModifyTable mt = mts.get(pw.getIndex());
					ArrayList<WordItem> wis = mt.getWords();
					for (int i = found2; i < wis.size(); i++) {
						WordItem wi0 = wis.get(i);
						if (strEqual(wi0.getWord(), pw.getRes())) {
							WordItem wi = new WordItem();
							wi.setHandle(wi0.getHandle());
							wi.setFreq(wi0.getFreq());
							wi.setWord(word);
							result.add(wi);
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * 用2分法查询源词典库,看是否已存在
	 *
	 * @param index
	 *            大数据块对应的下标（所有同一个字开头的词条为一个大数据块）
	 * @param res
	 *            去掉头一个字后剩余的部分
	 * @param handle
	 * @return
	 */
	public int findInOriginalTable(int index, String res, int handle) {
		int result = -1;

		if (res != null && wts != null) {
			WordTable wt = wts.get(index);
			if (wt != null && wt.getCount() > 0) {
				int start = 0;
				int end = wt.getCount() - 1;
				int mid = (end + start) / 2;
				ArrayList<WordItem> wis = wt.getWords();
				while (start <= end) {
					WordItem wi = wis.get(mid);
					int cmpValue = GFString.compareTo(wi.getWord(), res);
					if (cmpValue == 0 && (wi.getHandle() == handle || handle == -1)) {
						if (handle == -1) {
							while (mid >= 0 && res.compareTo(wis.get(mid).getWord()) == 0) {
								mid--;
							}
							if (mid < 0 || res.compareTo(wis.get(mid).getWord()) != 0)
								mid++;
						}

						result = mid;
						return result;

					} else if (cmpValue < 0 || cmpValue == 0 && wi.getHandle() < handle && handle != -1)
						start = mid + 1;
					else if (cmpValue > 0 || cmpValue == 0 && wi.getHandle() > handle && handle != -1)
						end = mid - 1;

					mid = (start + end) / 2;
				}
			}
		}
		return result;
	}

	/**
	 * 从修改表中查询是否存在,并返回它的位置坐标
	 *
	 * @param index
	 * @param res
	 * @param handle
	 * @return 位置坐标
	 */
	protected int findInModifyTable(int index, String res, int handle) {
		int result = -1;

		//		if ((mts != null) && (mts.size() > index)) {
		if ((mts != null) && (mts.size() > index) && (index > 0)) {
			ArrayList<WordItem> wis = mts.get(index).getWords();
			if (res != null && wis != null) {
				int i = 0;
				for (; i < wis.size(); i++) {
					WordItem wi = wis.get(i);
					if (wi.getWord().length() < res.length()
							|| (wi.getWord().length() == res.length() && wi.getHandle() < handle))
						continue;
				}
				if (i < wis.size() && strEqual(wis.get(i).getWord(), res)
						&& (wis.get(i).getHandle() == handle || handle < 0))
					result = i;
			}
		}
		return result;
	}

	// TODO
	public boolean strEqual(String b1, String b2) {
		if (b1 == null && b2 == null)
			return true;
		else if (b1 != null && b2 != null) {
			return b1.equals(b2);
		}
		return false;
	}

	public int getWordType(String word) {
		if (word != null) {
			int type = Utility.charType(word);
			int len = word.length();

			if (len > 0 && type == Utility.CT_CHINESE && GFString.isAllChinese(word))
				return Utility.WT_CHINESE;
			else if (len > 0 && type == Utility.CT_DELIMITER)
				return Utility.WT_DELIMITER;

		}
		return Utility.WT_OTHER;
	}

	/**
	 * 预处理,先把词前后的空格去掉
	 *
	 * @param word
	 * @param wordRet
	 * @param isAdd
	 * @return
	 */
	public Preword preProcessing(String word) {
		Preword result = null;

		if (word != null && word.length() > 0) {

			int type = Utility.charType(word);
			word = GFString.removeSpace(word);
			int len = word.length();
			int end = len - 1, begin = 0;

			if (begin > end)
				return null;

			result = new Preword();
			result.setWord(word);

			if (type == Utility.CT_CHINESE) {// Chinese word
				result.setIndex(Utility.CC_ID(word));
				if (word != null)
					result.setRes(word.length() > 1 ? word.substring(1) : "");

			}

			else if (type == Utility.CT_DELIMITER) {// Delimiter
				result.setIndex(3755);
				result.setRes(word);

			} else
				result.setIndex(-1);
		}
		return result;// other invalid
	}

	public boolean mergePOS(int handle) {
		mts = new ArrayList<ModifyTable>();

		for (int i = 0; i < Utility.CC_NUM; i++) {

		}

		return false;
	}

	/**
	 * 从词典库中找出最匹配的一个
	 *
	 * @param word
	 * @return
	 */
	public WordItem getMaxMatch(String word) {
		WordItem result = null;

		if (word != null) {

			Preword pw = preProcessing(word);
			if (pw != null & pw.getWord() != null && pw.getIndex() >= 0) {
				String firstChar = pw.getWord().substring(0, 1);
				int found = findInOriginalTable(pw.getIndex(), pw.getRes(), -1);
				if (found == -1) {
					ArrayList<WordItem> wis = wts.get(pw.getIndex()).getWords();
					for (int j = 0; j < wis.size(); j++) {
						int compValue = GFString.compareTo(wis.get(j).getWord(), pw.getRes());
						if (compValue == 1) {
							found = j;
							break;
						}
					}
				}
				// 从源词典表中找出去掉第一个开头的字之后相等的词
				if (found >= 0 && wts != null && wts.get(pw.getIndex()) != null) {
					// 至少有一个
					ArrayList<WordItem> wis = wts.get(pw.getIndex()).getWords();
					if (wis == null)
						return null;

					result = new WordItem();
					WordItem wi = wis.get(found);
					String wordRet = firstChar + wi.getWord();
					result.setWord(wordRet);
					result.setFreq(wi.getFreq());
					result.setHandle(wi.getHandle());
					result.setLen(wi.getLen());
					return result;

				}

				ArrayList<WordItem> wis = null;
				if (mts != null && mts.get(pw.getIndex()) != null) {
					wis = mts.get(pw.getIndex()).getWords();

					if (wis != null)
						for (WordItem wi : wis) {
							if (pw.getRes() != null && pw.getRes().equals(wi.getWord())) {
								result = new WordItem();
								String wordRet = firstChar + wi.getWord();
								result.setWord(wordRet);
								result.setHandle(wi.getHandle());
								result.setFreq(wi.getFreq());
								result.setLen(wi.getLen());
								return result;
							}
						}

				}
			}
		}
		return result;
	}

	public int getFreq(String word, int handle) {
		if (word != null && word.length() > 0) {
			Preword pw = preProcessing(word);
			if (pw != null) {
				int found = findInOriginalTable(pw.getIndex(), pw.getRes(), handle);
				if (found >= 0 && wts != null) {
					WordTable wt = wts.get(pw.getIndex());
					WordItem wi = wt.getWords().get(found);
					return wi.getFreq();
				}

				int found2 = findInModifyTable(pw.getIndex(), pw.getRes(), handle);
				if (found2 >= 0 && mts != null) {
					ModifyTable mt = mts.get(pw.getIndex());
					WordItem wi = mt.getWords().get(found);
					return wi.getFreq();
				}
			}
		}
		return 0;
	}

	// ---------------------------------------------------------//
	// 暂时不会用到的方法
	public boolean optimum() {
		return false;
	}

	public boolean merge(Dictionary dict2, int nRatio) {
		return false;
	}

	public boolean outputChars(String sFilename) {
		return false;
	}

	public boolean output(String sFilename) {
		return false;
	}

	public boolean getPOSString(int nPOS, String sPOSRet) {
		return false;
	}

	public int getPOSValue(byte[] sPOS) {
		return 0;
	}

}
