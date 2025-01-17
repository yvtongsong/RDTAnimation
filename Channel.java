import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Channel extends JPanel {
	public class Message {
		private Direction direction;
		private Color color = Color.red;
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

		public void corrupt() {
			color = Color.black;
		}

		public void lose() {
			color = Color.white;
		}

		public boolean isValid() {
			return (y >= 0 && y <= Utils.DRAW_PANEL_HEIGHT - Utils.STATIC_RECTANGLE_HEIGHT);
		}
	}

	private List<Message> messages;

	public Channel() {
		setPreferredSize(new Dimension(Utils.DRAW_PANEL_WIDTH, Utils.DRAW_PANEL_HEIGHT));
		messages = new ArrayList<>();
	}

	public void addMessage(Direction d) {
		Message message = new Message(d);
		messages.add(message);

		new Thread(() -> {
			while(message.isValid()) {
				message.move();
				repaint();
				try {
					TimeUnit.MILLISECONDS.sleep(50);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			messages.remove(message);
			repaint();
		}).start();
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
		for (Message message : messages) {
			g.setColor(message.color);
			g.fillRect(
					Utils.DRAW_PANEL_WIDTH / 2 - Utils.STATIC_RECTANGLE_WIDTH / 2,
					message.getY(),
					Utils.STATIC_RECTANGLE_WIDTH,
					Utils.STATIC_RECTANGLE_HEIGHT);
		}
	}
}
