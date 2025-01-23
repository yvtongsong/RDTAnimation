public class Utils {
	public static final int STATIC_RECTANGLE_WIDTH = 20;
	public static final int STATIC_RECTANGLE_HEIGHT = 30;

	public static final int DRAW_PANEL_WIDTH = 20;
	public static final int DRAW_PANEL_HEIGHT = 300;

	public static final int CONTROL_PANEL_WIDTH = 200;
	public static final int CONTROL_PANEL_HEIGHT = 300;

	public static final int FRAME_WIDTH = DRAW_PANEL_WIDTH + CONTROL_PANEL_WIDTH;
	public static final int FRAME_HEIGHT = DRAW_PANEL_HEIGHT;

	public static final int FRAME_X = 600;
	public static final int FRAME_Y = 100;

	public static final int NUMBER_OF_CHANNELS = 15;

	public static Channel channelChosen;
	public static final Object sendLock = new Object();
}
