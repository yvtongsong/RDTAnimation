import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.concurrent.TimeUnit;

public class Simulation {
	public final List<Channel> channels;
	private int channelIndex = 0;

	public Simulation() {
		channels = new ArrayList<>();
	}

	public void go() {
		setUpGUI();
	}

	public void setUpGUI() {
		JFrame frame = new JFrame();
		frame.setTitle("Reliable Data Transfer");
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);

		JPanel channelPanel = new JPanel();
		JPanel controlPanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel();
		JButton sendButton = new JButton("Send");
		JButton corruptButton = new JButton("Corrupt");
		JButton loseButton = new JButton("Lose");
		JButton pauseButton = new JButton("Pause");
		JButton resetButton = new JButton("Reset");
		NotificationBoard notificationBoard = new NotificationBoard();

		frame.add(BorderLayout.CENTER, channelPanel);
		frame.add(BorderLayout.EAST, controlPanel);

		for (int i = 0; i < Utils.NUMBER_OF_CHANNELS; i++) {
			Channel channel = new Channel(notificationBoard);
			channels.add(channel);
			channelPanel.add(BorderLayout.CENTER, channel);
		}

		buttonPanel.add(sendButton);
		buttonPanel.add(corruptButton);
		buttonPanel.add(loseButton);
		buttonPanel.add(pauseButton);
		buttonPanel.add(resetButton);
		controlPanel.add(BorderLayout.CENTER, notificationBoard);
		controlPanel.add(BorderLayout.NORTH, buttonPanel);

		frame.pack();

		sendButton.addActionListener((event) -> {
			if (channelIndex >= Utils.NUMBER_OF_CHANNELS) {
				notificationBoard.append("Channels are busy");
				return;
			}
			channels.get(channelIndex).go();
			channelIndex++;
			notificationBoard.append("Send a Message!");
		});
		corruptButton.addActionListener((event) -> {
			if (Utils.channelChosen != null) {
				Utils.channelChosen.corruptMessage();
			}
		});
		loseButton.addActionListener((event) -> {
			if (Utils.channelChosen != null) {
				Utils.channelChosen.loseMessage();
			}
		});
		pauseButton.addActionListener((event) -> {
			if (pauseButton.getText().equals("Pause")) {
				pauseButton.setText("Continue");
				for (Channel channel : channels) {
					channel.pause();
				}
			} else {
				pauseButton.setText("Pause");
				for (Channel channel : channels) {
					channel.goOn();
				}
			}
		});
		resetButton.addActionListener((event) -> {
			for (Channel channel : channels) {
				channel.reset();
			}
			channelIndex = 0;
			Utils.channelChosen = null;
			notificationBoard.reset();
		});
	}
}
