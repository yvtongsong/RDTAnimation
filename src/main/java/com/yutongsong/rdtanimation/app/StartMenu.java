package com.yutongsong.rdtanimation.app;

import javax.swing.*;
import java.awt.BorderLayout;

import static com.yutongsong.rdtanimation.util.ChannelUtil.*;
import com.yutongsong.rdtanimation.util.ChannelUtil;

public class StartMenu {
	public Thread thread;
	public JFrame frame;

	public StartMenu() {
		initFrame();

		while (true) {
			synchronized (resetLock) {
				try {
					resetLock.wait();
				} catch (Exception e) {
					e.printStackTrace();
				}
				thread.interrupt();
				ChannelUtil.reset();
				frame.setVisible(true);
			}
		}
	}

	public void initFrame() {
		frame = new JFrame();
		frame.setTitle("RDTAnimation");
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);

		JButton rdtButton = new JButton("RDTAnimation");
		JButton gbnButton = new JButton("GBNAnimation");
		JButton srButton = new JButton("SRAnimation");

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(rdtButton);
		panel.add(gbnButton);
		panel.add(srButton);
		frame.add(BorderLayout.CENTER, panel);
		frame.pack();

		JLabel label = new JLabel("Please choose an animation!");
		frame.add(BorderLayout.WEST, label);
		frame.pack();

		rdtButton.addActionListener((event) -> {
			frame.setVisible(false);
			thread = new Thread(() -> new RDTAnimation());
			thread.start();
		});
		gbnButton.addActionListener((event) -> {
			frame.setVisible(false);
			thread = new Thread(() -> new GBNAnimation());
			thread.start();
		});
		srButton.addActionListener((event) -> {
			frame.setVisible(false);
			thread = new Thread(() -> new SRAnimation());
			thread.start();
		});
	}
}
