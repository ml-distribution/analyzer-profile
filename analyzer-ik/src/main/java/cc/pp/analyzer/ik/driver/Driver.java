package cc.pp.analyzer.ik.driver;

import cc.pp.analyzer.ik.demo.IKSegmenterDemo;

public class Driver {

	/**
	 * 主函数
	 */
	public static void main(String[] args) {

		if (args.length == 0) {
			System.err.println("Usage: <ClassName> <ClassParams ...>");
			System.exit(-1);
		}

		switch (args[0]) {
		case "IKSegmenterDemo":
			IKSegmenterDemo.main(getLeftParams(args));
			break;
		default:
			return;
		}
	}

	public static String[] getLeftParams(String[] args) {
		String[] leftArgs = new String[args.length - 1];
		for (int i = 1; i < args.length; i++) {
			leftArgs[i - 1] = args[i];
		}
		return leftArgs;
	}

}
