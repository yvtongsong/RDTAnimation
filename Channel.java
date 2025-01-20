import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

public class Channel extends JPanel {

	public class Message {
		private final Direction direction;
		private Color color = Color.red;
		private State state = State.NORMAL;
		private int y;

		public Message(Direction d) {
			direction = d;
			switch(direction) {
				case UP:
					y = Utils.DRAW_PANEL_HEIGHT - Utils.STATIC_RECTANGLE_HEIGHT;
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

		public boolean done() {
			return !(y >= 0 && y <= Utils.DRAW_PANEL_HEIGHT - Utils.STATIC_RECTANGLE_HEIGHT);
		}
	}

	public class ChannelMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			chooseMessage(e.getY());
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


	private final CopyOnWriteArraySet<Message> allMessages;
	private final CopyOnWriteArraySet<Message> highlightedMessages;
	private final NotificationBoard notificationBoard;
	private Message messageChosen = null;
	private AtomicBoolean isPausing;
	private final Object pausingLock = new Object();
	private final Object fsmLock = new Object();
	private final int channelIndex;

	public Channel(int channelIndex, NotificationBoard notificationBoard) {
		setPreferredSize(new Dimension(Utils.DRAW_PANEL_WIDTH, Utils.DRAW_PANEL_HEIGHT));
		addMouseListener(new ChannelMouseListener());
		this.notificationBoard = notificationBoard;
		this.channelIndex = channelIndex;
		isPausing = new AtomicBoolean(false);
		allMessages = new CopyOnWriteArraySet<>();
		highlightedMessages = new CopyOnWriteArraySet<>();

		setUpFSM();
	}

	public void go() {
		addMessage(Direction.UP);
	}

	public Message addMessage(Direction d) {
		Message message = new Message(d);
		allMessages.add(message);
		highlightedMessages.add(message);

		new Thread(() -> {
			while(!message.done()) {
				if (!isPausing.get()) {
					message.move();
					repaint();
					try {
						TimeUnit.MILLISECONDS.sleep(16);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					synchronized (pausingLock)  {
						try {
							pausingLock.wait();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			synchronized (fsmLock) {
				fsmLock.notify();
			}
			highlightedMessages.remove(message);
			repaint();
		}).start();

		return message;
	}

	public void chooseMessage(int clickY) {
		for (Message message : highlightedMessages) {
			if (clickY > message.getY() && clickY < message.getY() + Utils.STATIC_RECTANGLE_HEIGHT){
				messageChosen = message;
				Utils.channelChosen = this;
				notificationBoard.append(channelIndex, "You have chosen a Message");
				return;
			}
		}
		notificationBoard.append(channelIndex, "Please click again!");
	}

	public void loseMessage() {
		if (messageChosen != null && highlightedMessages.contains(messageChosen)) {
			messageChosen.lose();
			repaint();
			notificationBoard.append(channelIndex, "Lose!");
		} else {
			notificationBoard.append(channelIndex, "Invalid Operation! Please choose another message!");
		}
		highlightedMessages.remove(messageChosen);
		messageChosen = null;
	}

	public void corruptMessage() {
		if (
				messageChosen != null &&
				highlightedMessages.contains(messageChosen) &&
				messageChosen.getState() != State.CORRUPT) {
			messageChosen.corrupt();
			repaint();
			notificationBoard.append(channelIndex, "Corrupt!");
		} else {
			notificationBoard.append(channelIndex, "Invalid Operation! Please choose another message!");
		}
	}

	public void pause() {
		isPausing.set(true);
	}

	public void goOn() {
		synchronized (pausingLock) {
			isPausing.set(false);
			pausingLock.notify();
		}
	}

	public void reset() {
		allMessages.clear();
		highlightedMessages.clear();
		isPausing.set(false);
		notificationBoard.reset();
		goOn();
	}


	@Override
	public void paintComponent(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(
				0,
				0,
				Utils.DRAW_PANEL_WIDTH,
				Utils.DRAW_PANEL_HEIGHT);

		drawStaticRectangle(g);
		drawMovingRectangle(g);
	}

	private void drawStaticRectangle(Graphics g) {
		g.setColor(Color.black);
		g.drawRect(
				Utils.DRAW_PANEL_WIDTH / 2 - Utils.STATIC_RECTANGLE_WIDTH / 2,
				Utils.DRAW_PANEL_HEIGHT - Utils.STATIC_RECTANGLE_HEIGHT,
				Utils.STATIC_RECTANGLE_WIDTH,
				Utils.STATIC_RECTANGLE_HEIGHT);
		g.drawRect(
				Utils.DRAW_PANEL_WIDTH / 2 - Utils.STATIC_RECTANGLE_WIDTH / 2,
				0,
				Utils.STATIC_RECTANGLE_WIDTH,
				Utils.STATIC_RECTANGLE_HEIGHT);
		g.drawString(
				String.valueOf(channelIndex),
				Utils.DRAW_PANEL_WIDTH / 2 - Utils.STATIC_RECTANGLE_WIDTH / 2,
				Utils.DRAW_PANEL_HEIGHT - Utils.STATIC_RECTANGLE_HEIGHT / 2);

	}

	private void drawMovingRectangle(Graphics g) {
		for (Message message : highlightedMessages) {
			g.setColor(message.getColor());
			g.fillRect(
					Utils.DRAW_PANEL_WIDTH / 2 - Utils.STATIC_RECTANGLE_WIDTH / 2,
					message.getY(),
					Utils.STATIC_RECTANGLE_WIDTH,
					Utils.STATIC_RECTANGLE_HEIGHT);
		}
	}

	private void setUpFSM() {
		new Thread(() -> {
			while (true) {
				for (Message message : allMessages) {
					if (message.done()) {
						if (message.getState() == State.LOSE) {
							try {
								TimeUnit.SECONDS.sleep(5);
							} catch (Exception e) {
								e.printStackTrace();
							}
							addMessage(Direction.UP);
						} else {
							Message tmp;
							switch (message.getDirection()) {
								case UP:
									tmp = addMessage(Direction.DOWN);
									if (message.getState() == State.CORRUPT) {
										tmp.corrupt();
									}
									break;
								case DOWN:
									if (message.getState() == State.CORRUPT) {
										addMessage(Direction.UP);
									}
									break;
							}
						}
						allMessages.remove(message);
					} else {
						synchronized (fsmLock) {
							try {
								fsmLock.wait();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}).start();
	}
}
