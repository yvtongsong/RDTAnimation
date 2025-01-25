package com.yutongsong.rdtanimation.app;

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

	public void append(int channelIndex, String s) {
		textArea.append(channelIndex + ": " + s + "\n");
	}

	public void reset() {
		textArea.setText("");
	}
}
