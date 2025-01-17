import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Simulation {
	public void go() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);

		JPanel controlPanel = new JPanel();
		frame.add(BorderLayout.EAST, controlPanel);

		Channel channel = new Channel();
		frame.add(BorderLayout.CENTER, channel);

		JButton upButton = new JButton("UP");
		controlPanel.add(upButton);

		JButton downButton = new JButton("DOWN");
		controlPanel.add(downButton);

		frame.pack();

		upButton.addActionListener((event) -> channel.addMessage(Direction.UP));
		downButton.addActionListener((event) -> channel.addMessage(Direction.DOWN));
	}
}
