import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.TimeUnit;

public class Simulation {
	public Channel channel;
	public JTextArea text;

	public void go() {
		setUpGUI();
	}

	public void setUpGUI() {
		JFrame frame = new JFrame();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);

		JPanel controlPanel = new JPanel();
		frame.add(BorderLayout.EAST, controlPanel);

		channel = new Channel();
		frame.add(BorderLayout.CENTER, channel);

		JButton sendButton = new JButton("Send");
		controlPanel.add(sendButton);

		JButton corruptButton = new JButton("Corrupt");
		controlPanel.add(corruptButton);

		JButton loseButton = new JButton("Lose");
		controlPanel.add(loseButton);

		text = new JTextArea(10, 30);
		JScrollPane scroll = new JScrollPane(
				text,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		controlPanel.add(scroll);

		frame.pack();

		sendButton.addActionListener((event) -> {
			if (channel.go()) {
				appendNotifications("Send a Message!");
			} else {
				appendNotifications("Invalid operation! This channel is busy!");
			}
		});
		corruptButton.addActionListener((event) -> {
			if (channel.corruptMessage()) {
				appendNotifications("Corrupt!");
			} else {
				appendNotifications("Invalid operation! Choose a new Message!");
			}
		});
		loseButton.addActionListener((event) -> {
			if (channel.loseMessage()) {
				appendNotifications("Lose!");
			} else {
				appendNotifications("Invalid operation! Choose a new Message!");
			}
		});

		channel.addMouseListener(new ChannelMouseListener());
	}

	private int notificationTimes = 0;

	private void appendNotifications(String s) {
		notificationTimes++;
		text.append(notificationTimes + ": " + s + "\n");
	}

	public class ChannelMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (channel.chooseMessage(e.getY())) {
				appendNotifications("You have chosen a Message!");
			} else {
				appendNotifications("Please click again!");
			}
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
}
