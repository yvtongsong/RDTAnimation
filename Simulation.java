import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.TimeUnit;

public class Simulation {
	public Channel channel;
	public Message messageChoosen;
	public void go() {
		JFrame frame = new JFrame();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);

		JPanel controlPanel = new JPanel();
		frame.add(BorderLayout.EAST, controlPanel);

		channel = new Channel();
		frame.add(BorderLayout.CENTER, channel);

		JButton upButton = new JButton("UP");
		controlPanel.add(upButton);

		JButton downButton = new JButton("DOWN");
		controlPanel.add(downButton);

		JButton corruptButton = new JButton("Corrupt");
		controlPanel.add(corruptButton);

		JButton loseButton = new JButton("Lose");
		controlPanel.add(loseButton);

		frame.pack();

		upButton.addActionListener((event) -> channel.addMessage(Direction.UP));
		downButton.addActionListener((event) -> channel.addMessage(Direction.DOWN));
		corruptButton.addActionListener((event) -> {
			if (messageChoosen != null) {
				messageChoosen.corrupt();
			}
		});
		loseButton.addActionListener((event) -> {
			if (messageChoosen != null) {
				channel.loseMessage(messageChoosen);
				messageChoosen = null;
			}
		});

		channel.addMouseListener(new ChannelMouseListener());
	}

	public class ChannelMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
			messageChoosen = channel.chooseMessage(e.getY());
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
