package com.yutongsong.rdtanimation.component;

import java.awt.*;

import static com.yutongsong.rdtanimation.util.Constant.*;
import static com.yutongsong.rdtanimation.util.ChannelUtil.*;

public class GBNChannel extends Channel{
	public GBNChannel(int channelIndex, NotificationBoard notificationBoard) {
		super(channelIndex, notificationBoard);
	}

	@Override
	public void addMessage(Direction direction, State state) {
		new Thread(() -> {
			super.addMessage(direction, state);
			switch (getMessageState()) {
				case NORMAL:
					if (receiverWindowStartChannelIndex == getChannelIndex()) {
						receiverWindowStartChannelIndex++;
					}
					super.addMessage(Direction.DOWN, State.NORMAL);
					break;
				case CORRUPT:
					super.addMessage(Direction.DOWN, State.CORRUPT);
					break;
				case LOSE:
					super.addMessage(Direction.DOWN, State.LOSE);
					break;
			}
			if (getMessageState() == State.NORMAL) {
				if (senderWindowStartChannelIndex == getChannelIndex()) {
					senderWindowStartChannelIndex++;
				}
			}
			if (getChannelIndex() == maxSenderIndex) {
				synchronized (sendLockGBN) {
					sendLockGBN.notify();
				}
			}
		}).start();
	}

	@Override
	public void drawStaticRectangle(Graphics g) {
		if (isInWindow()) {
			g.setColor(Color.red);
		} else {
			g.setColor(Color.black);
		}
		g.drawRect(
				(DRAW_PANEL_WIDTH - STATIC_RECTANGLE_WIDTH) / 2,
				DRAW_PANEL_HEIGHT - STATIC_RECTANGLE_HEIGHT,
				STATIC_RECTANGLE_WIDTH,
				STATIC_RECTANGLE_HEIGHT);
		g.drawRect(
				(DRAW_PANEL_WIDTH - STATIC_RECTANGLE_WIDTH) / 2,
				0,
				STATIC_RECTANGLE_WIDTH,
				STATIC_RECTANGLE_HEIGHT);
		g.drawString(
				String.valueOf(getChannelIndex()),
				(DRAW_PANEL_WIDTH - STATIC_RECTANGLE_WIDTH) / 2,
				DRAW_PANEL_HEIGHT - STATIC_RECTANGLE_HEIGHT / 2);
	}

	public boolean isInWindow() {
		int index = getChannelIndex();
		return index >= senderWindowStartChannelIndex && 
					 index < senderWindowStartChannelIndex + windowSize;
	}
}
