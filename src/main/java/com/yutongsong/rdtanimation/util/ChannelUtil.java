package com.yutongsong.rdtanimation.util;

import com.yutongsong.rdtanimation.component.Channel;

public class ChannelUtil {
	public static Channel channelChosen;
	public static Object sendLock = new Object();
	public static Object sendLockGBN = new Object();
	public static Object sendLockSR = new Object();

	public static int windowSize = 5;
	public static int senderWindowStartChannelIndex = 0;
	public static int receiverWindowStartChannelIndex = 0;
	public static int maxSenderIndex = windowSize - 1;
	public static int numberOfBufferElements = 0;
}
