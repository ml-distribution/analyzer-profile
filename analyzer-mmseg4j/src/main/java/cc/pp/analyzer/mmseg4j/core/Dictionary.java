package cc.pp.analyzer.mmseg4j.core;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 词典类. 词库目录单例模式.<br/>
 * 保存单字与其频率,还有词库.<br/>
 * 有检测词典变更的接口，外部程序可以使用 {@link #wordsFileIsChange()} 和 {@link #reload()} 来完成检测与加载的工作
 *
 * @author wanggang
 *
 */
public class Dictionary {

	private static final Logger logger = LoggerFactory.getLogger(Dictionary.class);

	// 词库目录
	private File dicPath;
	private volatile Map<Character, CharNode> dict;
	// 单个字的单位
	private volatile Map<Character, Object> unit;

	/** 记录 word 文件的最后修改时间 */
	private Map<File, Long> wordsLastTime = null;
	private long lastLoadTime = 0;

	/** 不要直接使用, 通过 {@link #getDefalutPath()} 使用*/
	private static File defalutPath = null;
	private static final ConcurrentHashMap<File, Dictionary> dics = new ConcurrentHashMap<>();

	@Override
	protected void finalize() throws Throwable {
		/*
		 * 使 class reload 的时也可以释放词库
		 */
		destroy();
	}

	/**
	 * 从默认目录加载词库文件.<p/>
	 * 查找默认目录顺序:
	 * <ol>
	 * <li>从系统属性mmseg.dic.path指定的目录中加载</li>
	 * <li>从classpath/data目录</li>
	 * <li>从user.dir/data目录</li>
	 * </ol>
	 * @see #getDefalutPath()
	 */
	public static Dictionary getInstance() {
		File path = getDefalutPath();
		return getInstance(path);
	}

	/**
	 * @param path 词典的目录
	 */
	public static Dictionary getInstance(String path) {
		return getInstance(new File(path));
	}

	/**
	 * @param path 词典的目录
	 */
	public static Dictionary getInstance(File path) {
		logger.info("try to load dir=" + path);
		File normalizeDir = normalizeFile(path);
		Dictionary dic = dics.get(normalizeDir);
		if (dic == null) {
			dic = new Dictionary(normalizeDir);
			dics.put(normalizeDir, dic);
		}

		return dic;
	}

	public static File normalizeFile(File file) {
		if (file == defalutPath) {
			return defalutPath;
		}
		try {
			return file.getCanonicalFile();
		} catch (IOException e) {
			throw new RuntimeException("normalize file=[" + file + "] fail", e);
		}
	}

	/**
	 * 销毁, 释放资源. 此后此对像不再可用
	 */
	void destroy() {
		clear(dicPath);

		dicPath = null;
		dict = null;
		unit = null;
	}

	/**
	 * @see Dictionary#clear(File)
	 */
	public static Dictionary clear(String path) {
		return clear(new File(path));
	}

	/**
	 * 从单例缓存中去除
	 *
	 * @param path
	 * @return 没有返回 null
	 */
	public static Dictionary clear(File path) {
		File normalizeDir = normalizeFile(path);
		return dics.remove(normalizeDir);
	}

	/**
	 * 词典的目录
	 */
	private Dictionary(File path) {
		init(path);
	}

	private void init(File path) {
		dicPath = path;
		wordsLastTime = new HashMap<>();
		// 加载词典
		reload();
	}

	private static long now() {
		return System.currentTimeMillis();
	}

	/**
	 * 只要 wordsXXX.dic的文件
	 *
	 * @return
	 */
	protected File[] listWordsFiles() {
		return dicPath.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("words") && name.endsWith(".dic");
			}

		});
	}

	private Map<Character, CharNode> loadDic(File wordsPath) throws IOException {
		InputStream charsIn = null;
		File charsFile = new File(wordsPath, "chars.dic");
		if (charsFile.exists()) {
			charsIn = new FileInputStream(charsFile);
			// chars.dic 也检测是否变更
			addLastTime(charsFile);
			// 从 jar 里加载
		} else {
			charsIn = this.getClass().getResourceAsStream("/data/chars.dic");
			charsFile = new File(this.getClass().getResource("/data/chars.dic").getFile()); // only for log
		}
		final Map<Character, CharNode> dic = new HashMap<Character, CharNode>();
		int lineNum = 0;
		long s = now();
		long ss = s;
		lineNum = load(charsIn, new FileLoading() { // 单个字的

					@Override
					public void row(String line, int n) {
						if (line.length() < 1) {
							return;
						}
						String[] w = line.split(" ");
						CharNode cn = new CharNode();
						switch (w.length) {
						case 2:
							try {
								// 字频计算出自由度
								cn.setFreq((int) (Math.log(Integer.parseInt(w[1])) * 100));
							} catch (NumberFormatException e) {
								// eat...
							}
						case 1:

							dic.put(w[0].charAt(0), cn);
						}
					}

				});
		logger.info("chars loaded time=" + (now() - s) + "ms, line=" + lineNum + ", on file=" + charsFile);

		// try load words.dic in jar
		InputStream wordsDicIn = this.getClass().getResourceAsStream("/data/words.dic");
		if (wordsDicIn != null) {
			File wordsDic = new File(this.getClass().getResource("/data/words.dic").getFile());
			loadWord(wordsDicIn, dic, wordsDic);
		}

		// 只要 wordsXXX.dic的文件
		File[] words = listWordsFiles();
		// 扩展词库目录
		if (words != null) {
			for (File wordsFile : words) {
				loadWord(new FileInputStream(wordsFile), dic, wordsFile);

				// 用于检测是否修改
				addLastTime(wordsFile);
			}
		}

		logger.info("load all dic use time=" + (now() - ss) + "ms");

		return dic;
	}

	/**
	 * @param is 词库文件流
	 * @param dic 加载的词保存在结构中
	 * @param wordsFile	日志用
	 * @throws IOException from {@link #load(InputStream, FileLoading)}
	 */
	private void loadWord(InputStream is, Map<Character, CharNode> dic, File wordsFile) throws IOException {
		long s = now();
		// 正常的词库
		int lineNum = load(is, new WordsFileLoading(dic));
		logger.info("words loaded time=" + (now() - s) + "ms, line=" + lineNum + ", on file=" + wordsFile);
	}

	private Map<Character, Object> loadUnit(File path) throws IOException {
		InputStream fin = null;
		File unitFile = new File(path, "units.dic");
		if (unitFile.exists()) {
			fin = new FileInputStream(unitFile);
			addLastTime(unitFile);
			// 在jar包里的/data/unit.dic
		} else {
			fin = Dictionary.class.getResourceAsStream("/data/units.dic");
			unitFile = new File(Dictionary.class.getResource("/data/units.dic").getFile());
		}

		final Map<Character, Object> unit = new HashMap<Character, Object>();

		long s = now();
		int lineNum = load(fin, new FileLoading() {

			@Override
			public void row(String line, int n) {
				if (line.length() != 1) {
					return;
				}
				unit.put(line.charAt(0), Dictionary.class);
			}
		});
		logger.info("unit loaded time=" + (now() - s) + "ms, line=" + lineNum + ", on file=" + unitFile);

		return unit;
	}

	/**
	 * 加载 wordsXXX.dic 文件类。
	 */
	private static class WordsFileLoading implements FileLoading {

		final Map<Character, CharNode> dic;

		/**
		 * @param dic 加载的词，保存在此结构中。
		 */
		public WordsFileLoading(Map<Character, CharNode> dic) {
			this.dic = dic;
		}

		@Override
		public void row(String line, int n) {
			if (line.length() < 2) {
				return;
			}
			CharNode cn = dic.get(line.charAt(0));
			if (cn == null) {
				cn = new CharNode();
				dic.put(line.charAt(0), cn);
			}
			cn.addWordTail(tail(line));
		}

	}

	/**
	 * 加载词文件的模板
	 *
	 * @return 文件总行数
	 */
	public static int load(InputStream fin, FileLoading loading) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(fin), "UTF-8"));
		String line = null;
		int n = 0;
		while ((line = br.readLine()) != null) {
			if (line == null || line.startsWith("#")) {
				continue;
			}
			n++;
			loading.row(line, n);
		}
		return n;
	}

	/**
	 * 取得 str 除去第一个char的部分
	 */
	private static char[] tail(String str) {
		char[] cs = new char[str.length() - 1];
		str.getChars(1, str.length(), cs, 0);
		return cs;
	}

	public static interface FileLoading {
		/**
		 * @param line 读出的一行
		 * @param n 当前第几行
		 */
		void row(String line, int n);
	}

	/**
	 * 把 wordsFile 文件的最后更新时间加记录下来
	 *
	 * @param wordsFile 非 null
	 */
	private synchronized void addLastTime(File wordsFile) {
		if (wordsFile != null) {
			wordsLastTime.put(wordsFile, wordsFile.lastModified());
		}
	}

	/**
	 * 词典文件是否有修改过
	 */
	public synchronized boolean wordsFileIsChange() {
		// 检查是否有修改文件,包括删除的
		for (Entry<File, Long> flt : wordsLastTime.entrySet()) {
			File words = flt.getKey();
			// 可能是删除了
			if (!words.canRead()) {
				return true;
			}
			// 更新了文件
			if (words.lastModified() > flt.getValue()) {
				return true;
			}
		}
		// 检查是否有新文件
		File[] words = listWordsFiles();
		if (words != null) {
			for (File wordsFile : words) {
				// 有新词典文件
				if (!wordsLastTime.containsKey(wordsFile)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 全新加载词库，没有成功加载会回滚。<P/>
	 * 注意：重新加载时，务必有两倍的词库树结构的内存，默认词库是 50M/个 左右。否则抛出 OOM。
	 *
	 * @return 是否成功加载
	 */
	public synchronized boolean reload() {
		Map<File, Long> oldWordsLastTime = new HashMap<>(wordsLastTime);
		Map<Character, CharNode> oldDict = dict;
		Map<Character, Object> oldUnit = unit;

		try {
			wordsLastTime.clear();
			dict = loadDic(dicPath);
			unit = loadUnit(dicPath);
			lastLoadTime = System.currentTimeMillis();
		} catch (IOException e) {
			// rollback
			wordsLastTime.putAll(oldWordsLastTime);
			dict = oldDict;
			unit = oldUnit;

			logger.error("reload dic error! dic={}, and rollbacked.", dicPath);

			return false;
		}

		return true;
	}

	/**
	 * word 能否在词库里找到
	 */
	public boolean match(String word) {
		if (word == null || word.length() < 2) {
			return false;
		}
		CharNode cn = dict.get(word.charAt(0));

		return search(cn, word.toCharArray(), 0, word.length() - 1) >= 0;
	}

	public CharNode head(char ch) {
		return dict.get(ch);
	}

	/**
	 * sen[offset] 后 tailLen 长的词是否存在.
	 * @see CharNode#indexOf(char[], int, int)
	 */
	public int search(CharNode node, char[] sen, int offset, int tailLen) {
		if (node != null) {
			return node.indexOf(sen, offset, tailLen);
		}

		return -1;
	}

	public int maxMatch(char[] sen, int offset) {
		CharNode node = dict.get(sen[offset]);

		return maxMatch(node, sen, offset);
	}

	public int maxMatch(CharNode node, char[] sen, int offset) {
		if (node != null) {
			return node.maxMatch(sen, offset + 1);
		}

		return 0;
	}

	public ArrayList<Integer> maxMatch(CharNode node, ArrayList<Integer> tailLens, char[] sen, int offset) {
		tailLens.clear();
		tailLens.add(0);
		if (node != null) {
			return node.maxMatch(tailLens, sen, offset + 1);
		}

		return tailLens;
	}

	public boolean isUnit(Character ch) {
		return unit.containsKey(ch);
	}

	/**
	 * 当 words.dic 是从 jar 里加载时, 可能 defalut 不存在
	 */
	public static File getDefalutPath() {
		if (defalutPath == null) {
			String defPath = System.getProperty("mmseg.dic.path");
			logger.info("look up in mmseg.dic.path=" + defPath);
			if (defPath == null) {
				URL url = Dictionary.class.getClassLoader().getResource("data");
				if (url != null) {
					defPath = url.getFile();
					logger.info("look up in classpath=" + defPath);
				} else {
					defPath = System.getProperty("user.dir") + "/data";
					logger.info("look up in user.dir=" + defPath);
				}

			}

			defalutPath = new File(defPath);
			if (!defalutPath.exists()) {
				logger.error("defalut dic path={} not exist", defalutPath);
			}
		}

		return defalutPath;
	}

	/**
	 * 仅仅用来观察词库
	 */
	public Map<Character, CharNode> getDict() {
		return dict;
	}

	/**
	 * 注意：当 words.dic 是从 jar 里加载时，此时 File 可能是不存在的。
	 */
	public File getDicPath() {
		return dicPath;
	}

	/** 最后加载词库的时间 */
	public long getLastLoadTime() {
		return lastLoadTime;
	}

}
