package com.yutongsong.rdtanimation.app;

import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.concurrent.TimeUnit;

import static com.yutongsong.rdtanimation.util.Constant.*;
import static com.yutongsong.rdtanimation.util.Lock.*;
import static com.yutongsong.rdtanimation.util.ChannelUtil.*;

public class Channel extends JPanel {
	public class Message {
		private final Direction direction;
		private Color color;
		private State state;
		private int y;

		public Message(Direction direction, State state) {
			this.direction = direction;
			this.state = state;
			switch (state) {
				case NORMAL:
					color = Color.red;
					break;
				case CORRUPT:
					color = Color.black;
					break;
				case LOSE:
					color = Color.white;
					break;
			}
			switch(this.direction) {
				case UP:
					y = DRAW_PANEL_HEIGHT - STATIC_RECTANGLE_HEIGHT;
					break;
				case DOWN:
					y = 0;
					break;
			}
		}

		public void move() {
			switch(direction) {
				case UP:
					y--;
					break;
				case DOWN:
					y++;
					break;
			}
		}

		public int getY() {
			return y;
		}

		public State getState() {
			return state;
		}

		public Direction getDirection() {
			return direction;
		}

		public Color getColor() {
			return color;
		}

		public void corrupt() {
			color = Color.black;
			state = State.CORRUPT;
		}

		public void lose() {
			color = Color.white;
			state = State.LOSE;
		}

		public boolean isDone() {
			return !(y >= 0 && y <= DRAW_PANEL_HEIGHT - STATIC_RECTANGLE_HEIGHT);
		}
	}

	public class ChannelMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (isBusy() && e.getY() > message.getY() && e.getY() < message.getY() + STATIC_RECTANGLE_HEIGHT){
				isChosen = true;
				channelChosen = Channel.this;
				notificationBoard.append(channelIndex, "You have chosen a Message");
				return;
			}
			notificationBoard.append(channelIndex, "Please click again!");
		}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}
	}


	private Message message;
	private final NotificationBoard notificationBoard;
	private boolean isChosen = false;
	private final int channelIndex;
	private Object pauseLock = new Object();
	private boolean isPausing = false;

	public Channel(int channelIndex, NotificationBoard notificationBoard) {
		setPreferredSize(new Dimension(DRAW_PANEL_WIDTH, DRAW_PANEL_HEIGHT));
		addMouseListener(new ChannelMouseListener());
		this.notificationBoard = notificationBoard;
		this.channelIndex = channelIndex;
	}

	public void addMessage(Direction direction, State state) {
		message = new Message(direction, state);

		while(!message.isDone()) {
			if (isPausing) {
				synchronized (pauseLock) {
					try {
						pauseLock.wait();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			message.move();
			repaint();
			try {
				TimeUnit.MILLISECONDS.sleep(16);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		repaint();
		synchronized (sendLock) {
			sendLock.notify();
		}
	}


	public boolean isChosen() {
		return isChosen;
	}

	public boolean isBusy() {
		return message != null && !message.isDone();
	}

	public void loseMessage() {
		if (isBusy() && isChosen() && message.getState() != State.LOSE) {
			message.lose();
			repaint();
			notificationBoard.append(channelIndex, "Lose!");
		} else {
			notificationBoard.append(channelIndex, "Invalid Operation! Please choose another message!");
		}
	}

	public void corruptMessage() {
		if (isBusy() && isChosen() && message.getState() == State.NORMAL) {
			message.corrupt();
			repaint();
			notificationBoard.append(channelIndex, "Corrupt!");
		} else {
			notificationBoard.append(channelIndex, "Invalid Operation! Please choose another message!");
		}
	}

	public State getMessageState() {
		return message.getState();
	}

	public Direction getMessageDirection() {
		return message.getDirection();
	}

	public void pause() {
		isPausing = true;
	}

	public void goOn() {
		isPausing = false;
		synchronized (pauseLock) {
			pauseLock.notify();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(
				0,
				0,
				DRAW_PANEL_WIDTH,
				DRAW_PANEL_HEIGHT);

		drawStaticRectangle(g);
		drawMovingRectangle(g);
	}

	public void drawStaticRectangle(Graphics g) {
		g.setColor(Color.black);
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
				String.valueOf(channelIndex),
				(DRAW_PANEL_WIDTH - STATIC_RECTANGLE_WIDTH) / 2,
				DRAW_PANEL_HEIGHT - STATIC_RECTANGLE_HEIGHT / 2);

	}

	public void drawMovingRectangle(Graphics g) {
		if (!isBusy() || message.getState() == State.LOSE) {
			return;
		}
		g.setColor(message.getColor());
		g.fillRect(
				(DRAW_PANEL_WIDTH - STATIC_RECTANGLE_WIDTH) / 2,
				message.getY(),
				STATIC_RECTANGLE_WIDTH,
				STATIC_RECTANGLE_HEIGHT);
	}
}
