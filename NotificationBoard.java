import javax.swing.*;

public class NotificationBoard extends JScrollPane{
	private final JTextArea textArea;
	private int notificationIndex = 0;

	public NotificationBoard() {
		super(
				new JTextArea(10, 20),
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		textArea = (JTextArea) this.getViewport().getView();
	}

	public void append(String s) {
		notificationIndex++;
		textArea.append(notificationIndex + ": " + s + "\n");
	}

	public void reset() {
		textArea.setText("");
	}
}
