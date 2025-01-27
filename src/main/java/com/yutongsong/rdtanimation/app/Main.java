package com.yutongsong.rdtanimation.app;

import static com.yutongsong.rdtanimation.util.ChannelUtil.*;
import com.yutongsong.rdtanimation.util.ChannelUtil;

public class Main {
	public static void main(String[] args) {
		while (true) {
			Thread thread = new Thread(() -> new SRAnimation());
			thread.start();
			synchronized (resetLock) {
				try {
					resetLock.wait();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			thread.interrupt();
			ChannelUtil.reset();
		}
	}
}
