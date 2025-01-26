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

public class RDTAnimation extends GUI implements ButtonActionListener { 
	private final List<Channel> channels;

	public RDTAnimation() {
		channels = new ArrayList<>();
		for (int i = 0; i < NUMBER_OF_CHANNELS; i++) {
			Channel channel = new Channel(i, notificationBoard);
			channels.add(channel);
			channelPanel.add(channel);
		}
		frame.pack();

		sendButtonActionListener();
		corruptButtonActionListener();
		loseButtonActionListener();
		pauseButtonActionListener();
	}

	@Override
	public void sendButtonActionListener() {
		sendButton.addActionListener((event) -> {
			if (sendButton.getText().equals("Send")) {
				sendButton.setText("Reset");
				frame.pack();
				sendAction();
			} else {
				sendButton.setText("Send");
				frame.pack();
				resetAction();
			}
		});
	}

	private void sendAction() {
		new Thread(() -> {
			for (Channel channel : channels) {
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
					Direction direction = channel.getMessageDirection();
					State state = channel.getMessageState();
					switch (direction) {
						case UP:
							if (state == State.NORMAL) {
								channel.addMessage(Direction.DOWN, State.NORMAL);
							} else if (state == State.CORRUPT) {
								channel.addMessage(Direction.DOWN, State.CORRUPT);
							} else {
								channel.addMessage(Direction.DOWN, State.LOSE);
							}
							break;
						case DOWN:
							if (state == State.NORMAL) {
								loopFlag = false;
							} else if (state == State.CORRUPT) {
								channel.addMessage(Direction.UP, State.NORMAL);
							} else {
								channel.addMessage(Direction.UP, state.NORMAL);
							}
							break;
					}
				}
			}
		}).start();
	}

	private void resetAction() {

	}

	@Override
	public void corruptButtonActionListener() {
		corruptButton.addActionListener((event) -> {
			if (channelChosen != null) {
				channelChosen.corruptMessage();
			}
		});
	}

	@Override
	public void loseButtonActionListener() {
		loseButton.addActionListener((event) -> {
			if (channelChosen != null) {
				channelChosen.loseMessage();
			}
		});
	}

	@Override
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
