import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.concurrent.TimeUnit;

public class RDT {
	public final String title;
	public final List<Channel> channels;
	public final NotificationBoard notificationBoard;
	public final JFrame frame;
	public final JButton sendButton;
	public final JButton loseButton;
	public final JButton corruptButton;
	public final JButton pauseButton;
	public int channelIndex = 0;

	public RDT(String title) {
		this.title = title;
		channels = new ArrayList<>();
		frame = new JFrame();
		sendButton = new JButton("Send");
		corruptButton = new JButton("Corrupt");
		loseButton = new JButton("Lose");
		pauseButton = new JButton("Pause");
		notificationBoard = new NotificationBoard();
	}

	public void go() {
		setUpGUI();
	}

	public void setUpGUI() {
		frame.setTitle(title);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);

		JPanel channelPanel = new JPanel();
		JPanel controlPanel = new JPanel(new BorderLayout());
		JPanel buttonPanel = new JPanel();

		frame.add(BorderLayout.CENTER, channelPanel);
		frame.add(BorderLayout.EAST, controlPanel);

		for (int i = 0; i < Utils.NUMBER_OF_CHANNELS; i++) {
			Channel channel = new Channel(i + 1, notificationBoard);
			channels.add(channel);
			channelPanel.add(BorderLayout.CENTER, channel);
		}

		buttonPanel.add(sendButton);
		buttonPanel.add(corruptButton);
		buttonPanel.add(loseButton);
		buttonPanel.add(pauseButton);
		controlPanel.add(BorderLayout.CENTER, notificationBoard);
		controlPanel.add(BorderLayout.NORTH, buttonPanel);

		frame.pack();

		sendButtonActionListener();
		corruptButtonActionListener();
		loseButtonActionListener();
		pauseButtonActionListener();
	}

	public void sendButtonActionListener() {
		sendButton.addActionListener((event) -> {
			if (sendButton.getText().equals("Send")) {
				sendAction();
				sendButton.setText("Reset");
				frame.pack();
			} else {
				resetAction();
				sendButton.setText("Send");
				frame.pack();
			}
		});
	}

	public void sendAction() {
		new Thread(() -> {
			while (true) {
				if (channelIndex >= Utils.NUMBER_OF_CHANNELS ||
						(channelIndex != 0 && !channels.get(channelIndex - 1).isDone())) {
					synchronized (Utils.sendLock) {
						try {
							System.out.println("sleep!");
							Utils.sendLock.wait();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				System.out.println("Wakeup!");
				channels.get(channelIndex).go();
				channelIndex++;
				notificationBoard.append(channelIndex, "Send a Message!");
			}
		}).start();
	}

	public void resetAction() {
		for (Channel channel : channels) {
			channel.reset();
		}
		channelIndex = 0;
		Utils.channelChosen = null;
		notificationBoard.reset();
	}

	public void corruptButtonActionListener() {
		corruptButton.addActionListener((event) -> {
			if (Utils.channelChosen != null) {
				Utils.channelChosen.corruptMessage();
			}
		});
	}

	public void loseButtonActionListener() {
		loseButton.addActionListener((event) -> {
			if (Utils.channelChosen != null) {
				Utils.channelChosen.loseMessage();
			}
		});
	}

	public void pauseButtonActionListener() {
		pauseButton.addActionListener((event) -> {
			if (pauseButton.getText().equals("Pause")) {
				pauseButton.setText("Continue");
				frame.pack();
				for (Channel channel : channels) {
					channel.pause();
				}
			} else {
				pauseButton.setText("Pause");
				frame.pack();
				for (Channel channel : channels) {
					channel.goOn();
				}
			}
		});
	}
}
