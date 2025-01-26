package com.yutongsong.rdtanimation.util;

import com.yutongsong.rdtanimation.component.Channel;

public class ChannelUtil {
	public static Channel channelChosen;
	public static Object sendLock = new Object();

	public static int windowSize = 5;
	public static int senderWindowStartChannelIndex = 0;
	public static int receiverWindowStartChannelIndex = 0;
}
