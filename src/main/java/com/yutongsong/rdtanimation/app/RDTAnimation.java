package com.yutongsong.rdtanimation.app;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.concurrent.TimeUnit;

import com.yutongsong.rdtanimation.component.*;

import static com.yutongsong.rdtanimation.util.Constant.*;
import static com.yutongsong.rdtanimation.util.ChannelUtil.*;

public class RDTAnimation {
	public final List<Channel> channels;
	public final NotificationBoard notificationBoard;
	public final JFrame frame;
	public final JButton sendButton;
	public final JButton loseButton;
	public final JButton corruptButton;
	public final JButton pauseButton;
	private int channelIndex = 0;

	public RDTAnimation() {
		channels = new ArrayList<>();
		frame = new JFrame();
		sendButton = new JButton("Send");
		corruptButton = new JButton("Corrupt");
		loseButton = new JButton("Lose");
		pauseButton = new JButton("Pause");
		notificationBoard = new NotificationBoard();

		initFrame();
		initChannelPanel();
		initControlPanel();

		sendButtonActionListener();
		corruptButtonActionListener();
		loseButtonActionListener();
		pauseButtonActionListener();

	}

	public void initFrame() {
		frame.setTitle("RDT");
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);
	}

	public void initChannelPanel() {
		JPanel channelPanel = new JPanel();
		for (int i = 0; i < NUMBER_OF_CHANNELS; i++) {
			Channel channel = new Channel(i + 1, notificationBoard);
			channels.add(channel);
			channelPanel.add(BorderLayout.CENTER, channel);
		}
		frame.add(BorderLayout.CENTER, channelPanel);
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

	public void sendButtonActionListener() {
		sendButton.addActionListener((event) -> {
			if (sendButton.getText().equals("Send")) {
				sendAction();
				sendButton.setText("Reset");
				frame.pack();
			} else {
				resetAction();
				sendButton.setText("Send");
				frame.pack();
			}
		});
	}

	public void sendAction() {
		new Thread(() -> {
			for (Channel channel : channels) {
				notificationBoard.append(channelIndex, "Send a Message!");
				channel.addMessage(Direction.UP, State.NORMAL);

				boolean loopFlag = true;
				while (loopFlag) {
					if (channel.isBusy()) {
						synchronized (sendLock) {
							try {
								sendLock.wait();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					State state = channel.getMessageState();
					Direction direction = channel.getMessageDirection();

					if (direction == Direction.UP) {
						switch (state) {
							case NORMAL:
								channel.addMessage(Direction.DOWN, State.NORMAL);
								break;
							case CORRUPT:
								channel.addMessage(Direction.DOWN, State.CORRUPT);
								break;
							case LOSE:
								channel.addMessage(Direction.DOWN, State.LOSE);
								break;
						}
					} else {
						switch (state) {
							case NORMAL:
								loopFlag = false;
								break;
							case CORRUPT:
								channel.addMessage(Direction.UP, State.NORMAL);
								break;
							case LOSE:
								channel.addMessage(Direction.UP, State.NORMAL);
								break;
						}
					}
				}
			}
		}).start();
	}

	public void resetAction() {
	}

	public void corruptButtonActionListener() {
		corruptButton.addActionListener((event) -> {
			if (channelChosen != null) {
				channelChosen.corruptMessage();
			}
		});
	}

	public void loseButtonActionListener() {
		loseButton.addActionListener((event) -> {
			if (channelChosen != null) {
				channelChosen.loseMessage();
			}
		});
	}

	public void pauseButtonActionListener() {
		pauseButton.addActionListener((event) -> {
			if (pauseButton.getText().equals("Pause")) {
				pauseButton.setText("Continue");
				frame.pack();
				for (Channel channel : channels) {
					channel.pause();
				}
			} else {
				pauseButton.setText("Pause");
				frame.pack();
				for (Channel channel : channels) {
					channel.goOn();
				}
			}
		});
	}
}
