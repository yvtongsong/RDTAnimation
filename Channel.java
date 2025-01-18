import javax.swing.*;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Channel extends JPanel {
	private final List<Message> messages;

	public Channel() {
		setPreferredSize(new Dimension(Utils.DRAW_PANEL_WIDTH, Utils.DRAW_PANEL_HEIGHT));
		messages = new ArrayList<>();
	}

	public Message addMessage(Direction d) {
		Message message = new Message(d);
		messages.add(message);

		new Thread(() -> {
			while(!message.done()) {
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

		return message;
	}

	public Message chooseMessage(int clickY) {
		for (Message message : messages) {
			if (clickY > message.getY() && clickY < message.getY() + Utils.STATIC_RECTANGLE_HEIGHT) {
				return message;
			}
		}
		return null;
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
			g.setColor(message.getColor());
			g.fillRect(
					Utils.DRAW_PANEL_WIDTH / 2 - Utils.STATIC_RECTANGLE_WIDTH / 2,
					message.getY(),
					Utils.STATIC_RECTANGLE_WIDTH,
					Utils.STATIC_RECTANGLE_HEIGHT);
		}
	}
}
