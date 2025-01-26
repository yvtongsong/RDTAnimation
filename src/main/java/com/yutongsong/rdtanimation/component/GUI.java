package com.yutongsong.rdtanimation.app;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.concurrent.TimeUnit;

import com.yutongsong.rdtanimation.component.*;

import static com.yutongsong.rdtanimation.util.Constant.*;
import static com.yutongsong.rdtanimation.util.ChannelUtil.*;

public class GUI { 
	public final NotificationBoard notificationBoard;
	public final JFrame frame;
	public final JPanel channelPanel;
	public final JButton sendButton;
	public final JButton loseButton;
	public final JButton corruptButton;
	public final JButton pauseButton;

	public GUI() {
		frame = new JFrame();
		sendButton = new JButton("Send");
		corruptButton = new JButton("Corrupt");
		loseButton = new JButton("Lose");
		pauseButton = new JButton("Pause");
		notificationBoard = new NotificationBoard();
		channelPanel = new JPanel();

		initFrame();
		initChannelPanel();
		initControlPanel();
	}

	public void initFrame() {
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	public void initChannelPanel() {
		frame.add(BorderLayout.CENTER, channelPanel);
		frame.pack();
	}

	public void initControlPanel() {
		JPanel controlPanel = new JPanel(new BorderLayout());
		controlPanel.add(BorderLayout.CENTER, notificationBoard);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(sendButton);
		buttonPanel.add(corruptButton);
		buttonPanel.add(loseButton);
		buttonPanel.add(pauseButton);

		controlPanel.add(BorderLayout.NORTH, buttonPanel);

		frame.add(BorderLayout.EAST, controlPanel);
		frame.pack();
	}
}
