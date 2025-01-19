import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CopyOnWriteArraySet;

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

	private final CopyOnWriteArraySet<Message> allMessages;
	private final CopyOnWriteArraySet<Message> highlightedMessages;
	private Message messageChosen = null;

	public Channel() {
		setPreferredSize(new Dimension(Utils.DRAW_PANEL_WIDTH, Utils.DRAW_PANEL_HEIGHT));
		allMessages = new CopyOnWriteArraySet<>();
		highlightedMessages = new CopyOnWriteArraySet<>();

		setUpFSM();
	}

	public boolean go() {
		if (allMessages.isEmpty()) {
			addMessage(Direction.UP);
			return true;
		}
		return false;
	}

	public Message addMessage(Direction d) {
		Message message = new Message(d);
		allMessages.add(message);
		highlightedMessages.add(message);

		new Thread(() -> {
			while(!message.done()) {
				message.move();
				repaint();
				try {
					TimeUnit.MILLISECONDS.sleep(16);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			highlightedMessages.remove(message);
			repaint();
		}).start();

		return message;
	}

	public boolean chooseMessage(int clickY) {
		for (Message message : highlightedMessages) {
			if (clickY > message.getY() && clickY < message.getY() + Utils.STATIC_RECTANGLE_HEIGHT){
				messageChosen = message;
				return true;
			}
		}
		return false;
	}

	public boolean loseMessage() {
		boolean result = false;
		if (messageChosen != null && highlightedMessages.contains(messageChosen)) {
			messageChosen.lose();
			result = true;
		}
		highlightedMessages.remove(messageChosen);
		messageChosen = null;
		return result;
	}

	public boolean corruptMessage() {
		if (messageChosen != null && highlightedMessages.contains(messageChosen)) {
			messageChosen.corrupt();
			return true;
		}
		return false;
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
					}
				}
			}
		}).start();
	}
}
