package com.packt.snake;

public class Constants {

	public static final int POINTS_PER_APPLE=20;
	
	public static final float WORLD_WIDTH=640;
	public static final float WORLD_HEIGHT=480;
	
	public enum STATE {
		PLAYING, GAME_OVER, SPLASHSCREEN
	}
	
	public static final int LEFT=1;
	public static final int UP=2;
	public static final int DOWN=3;
	public static final int RIGHT=4;
	
	public static final float MOVE_TIME= 1F;
	
	public static final int SNAKE_MOVEMENT=32;
	
}
