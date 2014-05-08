package org.ictclas4j.demo;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.ictclas4j.bean.SegResult;
import org.ictclas4j.segment.Segment;
import org.ictclas4j.utility.GFString;

/**
 * Copyright 2007.6.1 张新波（sinboy）
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */

public class SegMain {

	public static Segment seg;

	static Logger logger = Logger.getLogger(SegMain.class);

	public SegMain() {
		PropertyConfigurator.configure(Config.LOG4J_CONF);

	}

	public static void main(String[] args) {

		SegMain sm = new SegMain();
		seg = new Segment(1);
		sm.initFrm();
		String[] source = { "2006年德国世界杯八分之一决赛马上要开始", "广州：网络警察“巡逻”BBS和博客", "18时42分的球赛就要开始了", "张新波住在雅仕苑", "张新波在杭州",
				"红眼病很多", "中国人民从此站了起来", "三星ＳＨＭ－１００型电视获得了工业设计大奖" };
		for (int i = 0; i < source.length; i++) {
			SegResult sr = seg.split(source[i]);
			System.out.println("time:" + sr.getSpendTime() + " " + sr.getFinalResult());
		}

		try {
			int count = 0;
			long times = 0;
			long bytes = 0;
			int segPathCount = 1;
			int forCount = 1;
			seg.setSegPathCount(segPathCount);
			ArrayList<String> testCases = GFString.readTxtFile2("test/case1.txt");
			for (int i = 0; i < forCount; i++) {
				for (String src : testCases) {
					SegResult sr = SegMain.seg.split(src);
					count++;
					bytes += src.getBytes().length;
					times += sr.getSpendTime();
					logger.info("[time:" + sr.getSpendTime() + "ms]:\n" + sr.getFinalResult());
				}
				logger.info("i:" + i + "\ntotal_count:" + count + "\ntotal_time:" + times + "\ntotal_bytes:" + bytes
						+ "\navg_time:" + (times / count) + "\navg_bytes:" + (bytes / times) + "b/ms");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initFrm() {
		FrmMain frm = FrmMain.getInstance();
		JFrame jf = frm.getJFrame();
		jf.setSize(800, 600);
		jf.setVisible(true);
	}

}
