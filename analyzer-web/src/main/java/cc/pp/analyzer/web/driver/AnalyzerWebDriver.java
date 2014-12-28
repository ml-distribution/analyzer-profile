package cc.pp.analyzer.web.driver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.pp.analyzer.web.server.AnalyzerServer;

/**
 * 驱动类
 *
 */
public class AnalyzerWebDriver {

	private static Logger logger = LoggerFactory.getLogger(AnalyzerWebDriver.class);

	/**
	 * 主函数
	 */
	public static void main(String[] args) {

		if (args.length == 0) {
			System.err.println("Usage: Driver <class-name>");
			System.exit(-1);
		}
		String[] leftArgs = new String[args.length - 1];
		System.arraycopy(args, 1, leftArgs, 0, leftArgs.length);

		switch (args[0]) {
		case "analyzerServer":
			logger.info("中文分词服务");
			AnalyzerServer.main(leftArgs);
			break;
		default:
			return;
		}

		// 需要修改，启动接口后，又关闭了
		//		int exitCode = -1;
		//		ProgramDriver pgd = new ProgramDriver();
		//		try {
		//			pgd.addClass("analyzerServer", AnalyzerServer.class, "中文分词服务");
		//			pgd.driver(args);
		//			exitCode = 0;
		//		} catch (Throwable e) {
		//			throw new RuntimeException(e);
		//		}
		//		System.exit(exitCode);

	}

}
