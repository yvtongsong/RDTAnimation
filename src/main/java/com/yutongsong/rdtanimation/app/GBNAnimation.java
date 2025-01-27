package com.yutongsong.rdtanimation.app;

import java.util.List;
import java.util.ArrayList;
import java.awt.event.*;
import java.util.concurrent.TimeUnit;

import com.yutongsong.rdtanimation.component.*;

import static com.yutongsong.rdtanimation.util.Constant.*;
import static com.yutongsong.rdtanimation.util.ChannelUtil.*;

public class GBNAnimation extends GUI implements ButtonActionListener {
	private final List<GBNChannel> channels;

	public GBNAnimation() {
		channels = new ArrayList<>();
		for (int i = 0; i < NUMBER_OF_CHANNELS; i++) {
			GBNChannel channel = new GBNChannel(i, notificationBoard);
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
			while (true) {
				maxSenderIndex = windowSize + senderWindowStartChannelIndex - 1;
				for (GBNChannel channel : channels) {
					if (channel.isInWindow()) {
						channel.addMessage(Direction.UP, State.NORMAL);
						try {
							TimeUnit.MILLISECONDS.sleep(300);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				synchronized (sendLockGBN) {
					try {
						sendLockGBN.wait();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void resetAction() {
		frame.dispose();
		synchronized (resetLock) {
			resetLock.notify();
		}
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
				for (GBNChannel channel : channels) {
					channel.goOn();
				}
			}
		});
	}
}
