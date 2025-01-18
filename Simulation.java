import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.TimeUnit;

public class Simulation {
	public Channel channel;

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

		frame.pack();

		sendButton.addActionListener((event) -> channel.addMessage(Direction.UP));
		corruptButton.addActionListener((event) -> channel.corruptMessage());
		loseButton.addActionListener((event) -> channel.loseMessage());

		channel.addMouseListener(new ChannelMouseListener());
	}

	public class ChannelMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			channel.chooseMessage(e.getY());
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
