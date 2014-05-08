package org.ictclas4j.demo;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.ictclas4j.bean.SegResult;
import org.ictclas4j.utility.GFString;

public class FrmMain {

	private static FrmMain frmMain;

	private JFrame jFrame = null;

	private JPanel jContentPane = null;

	private JTabbedPane jTabbedPane = null;

	private JPanel jpSplit = null;

	private JLabel jlSrcMsg = null;

	private JLabel jlSplitMsg = null;

	private JLabel jlSegPathCount = null;

	private JTextArea jtSegPathCount = null;

	private JTextArea jtSrcMsg = null;

	private JTextArea jtSplitMsg = null;

	private JTextArea jtFor=null;

	private JButton jbSplitOK = null;

	private JButton jbTest = null;

	private FrmMain() {

	}

	public static FrmMain getInstance() {
		if (frmMain == null)
			frmMain = new FrmMain();

		return frmMain;
	}

	private JPanel getJpSplit() {

		if (jpSplit == null) {
			jpSplit = new JPanel();
			jpSplit.add(getJpSplit0(), BorderLayout.CENTER);
			jpSplit.add(getJbSplitOK(), BorderLayout.SOUTH);
			jpSplit.add(getJbTest(), BorderLayout.SOUTH);
		}
		return jpSplit;
	}

	private JPanel getJpSplit0() {

		JPanel jpSplit0 = new JPanel();
		jpSplit0.add(getJpSegPathCount(), null);
		jpSplit0.add(getJpSrcMsg(), null);
		jpSplit0.add(getJpSplitMsg(), null);

		jpSplit0.setSize(400, 200);
		jpSplit0.setLayout(new GridLayout(3, 1));

		return jpSplit0;
	}

	private JPanel getJpSplitMsg() {

		jlSplitMsg = new JLabel();
		jlSplitMsg.setText("分词结果：");

		JPanel jpSplitMsg = new JPanel();
		jpSplitMsg.add(jlSplitMsg);
		jpSplitMsg.add(getJtSplitMsg());
		JScrollPane jsp = new JScrollPane(jtSplitMsg);
		jpSplitMsg.add(jsp, jsp.getName());
		return jpSplitMsg;
	}


	private JPanel getJpSegPathCount() {

		jlSegPathCount = new JLabel();
		jlSegPathCount.setText("分词路径：");
		JLabel jtf=new JLabel();
		jtf.setText("测试分词循环次数：");

		JPanel jpSegPath = new JPanel();
		jpSegPath.add(jlSegPathCount);
		jpSegPath.add(getJtSegPathCount());

		JPanel jpFor=new JPanel();
		jpFor.add(jtf);
		jpFor.add(getJtForCount());

		JPanel jpanel=new JPanel();
		jpanel.add(jpSegPath);
		jpanel.add(jpFor);
		return jpanel;
	}

	private JTextArea getJtSegPathCount() {
		jtSegPathCount = new JTextArea(1, 4);
		jtSegPathCount.setText("1");
		return jtSegPathCount;
	}

	private JTextArea getJtForCount() {
		jtFor = new JTextArea(1, 4);
		jtFor.setText("1000");
		return jtFor;
	}

	private JTextArea getJtSrcMsg() {
		jtSrcMsg = new JTextArea(6, 40);
		jtSrcMsg.setText("张华平说八点二分开会");
		jtSrcMsg.setLineWrap(true);
		return jtSrcMsg;
	}

	private JTextArea getJtSplitMsg() {
		jtSplitMsg = new JTextArea(6, 40);
		jtSplitMsg.setLineWrap(true);

		return jtSplitMsg;
	}

	private JPanel getJpSrcMsg() {
		jlSrcMsg = new JLabel();
		jlSrcMsg.setText("源字符串：");

		JPanel jpSrcMsg = new JPanel();
		jpSrcMsg.add(jlSrcMsg);
		jpSrcMsg.add(getJtSrcMsg());
		JScrollPane jsp = new JScrollPane(jtSrcMsg);
		jpSrcMsg.add(jsp, jsp.getName());
		return jpSrcMsg;
	}

	/**
	 * This method initializes jbSendOK
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJbSplitOK() {

		if (jbSplitOK == null) {
			jbSplitOK = new JButton();
			jbSplitOK.setText("分词");
			jbSplitOK.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					int segPathCount = Integer.parseInt(jtSegPathCount.getText());
					SegMain.seg.setSegPathCount(segPathCount);
					SegResult sr = SegMain.seg.split(jtSrcMsg.getText());
					jtSplitMsg.setText("[Time:" + sr.getSpendTime() + "ms]:\n" + sr.getFinalResult());
				}
			});
		}
		return jbSplitOK;
	}

	private JButton getJbTest() {

		if (jbTest == null) {
			jbTest = new JButton();
			jbTest.setText("分词测试");
			jbTest.addActionListener(new java.awt.event.ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						int count=0;
						long times=0;
						long bytes=0;
						int segPathCount = Integer.parseInt(jtSegPathCount.getText());
						SegMain.seg.setSegPathCount(segPathCount);
						int forCount=Integer.parseInt(jtFor.getText());
						ArrayList<String> testCases = GFString.readTxtFile2("test/case1.txt");
						for (int i = 0; i < forCount; i++) {
							for (String src : testCases) {
								SegResult sr = SegMain.seg.split(src);
								count++;
								bytes+=src.getBytes().length;
								times+=sr.getSpendTime();
								jtSrcMsg.setText("total_count:"+count+"\ntotal_time:"+times+"\ntotal_bytes:"+bytes
										+"\navg_time:"+(times/count)+"\navg_bytes:"+(bytes/times)+"b/ms");
								jtSplitMsg.setText("[time:" + sr.getSpendTime() + "ms]:\n" + sr.getFinalResult());
							}
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
		}
		return jbTest;
	}

	/**
	 * This method initializes jFrame
	 *
	 * @return javax.swing.JFrame
	 */
	public JFrame getJFrame() {

		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setSize(new java.awt.Dimension(624, 215));
			jFrame.setTitle("ICTCLAS4J演示系统");
			jFrame.setContentPane(getJContentPane());
			jFrame.addWindowListener(new java.awt.event.WindowAdapter() {
				@Override
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				}
			});

		}
		return jFrame;
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJTabbedPane(), java.awt.BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTabbedPane
	 *
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.addTab("分词", null, getJpSplit(), null);
		}
		return jTabbedPane;
	}

}
