package cc.pp.analyzer.smallseg.core;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Splitter {

	private static final Pattern DEFAULT_PATTERN = Pattern.compile("\\s+");

	private final Pattern pattern;
	private final boolean keep_delimiters;

	public Splitter(Pattern pattern, boolean keep_delimiters) {
		this.pattern = pattern;
		this.keep_delimiters = keep_delimiters;
	}

	public Splitter(String pattern, boolean keep_delimiters) {
		this(Pattern.compile(pattern == null ? "" : pattern), keep_delimiters);
	}

	public Splitter(Pattern pattern) {
		this(pattern, true);
	}

	public Splitter(String pattern) {
		this(pattern, true);
	}

	public Splitter(boolean keep_delimiters) {
		this(DEFAULT_PATTERN, keep_delimiters);
	}

	public Splitter() {
		this(DEFAULT_PATTERN);
	}

	public String[] split(String text) {
		if (text == null) {
			text = "";
		}

		int last_match = 0;
		LinkedList<String> splitted = new LinkedList<String>();

		Matcher m = this.pattern.matcher(text);

		while (m.find()) {

			splitted.add(text.substring(last_match, m.start()));

			if (this.keep_delimiters) {
				splitted.add(m.group());
			}

			last_match = m.end();
		}

		splitted.add(text.substring(last_match));

		return splitted.toArray(new String[splitted.size()]);
	}

	public static void main(String[] argv) {
		if (argv.length != 2) {
			System.err.println("Syntax: java Splitter <pattern> <text>");
			return;
		}

		Pattern pattern = null;
		try {
			pattern = Pattern.compile(argv[0]);
		} catch (PatternSyntaxException e) {
			System.err.println(e);
			return;
		}

		Splitter splitter = new Splitter(pattern);

		String text = argv[1];
		int counter = 1;
		for (String part : splitter.split(text)) {
			System.out.printf("Part %d: \"%s\"\n", counter++, part);
		}
	}
}