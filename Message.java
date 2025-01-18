import java.awt.Color;

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

