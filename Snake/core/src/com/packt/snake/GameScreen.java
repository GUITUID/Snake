package com.packt.snake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.packt.snake.Constants.STATE;

public class GameScreen extends ScreenAdapter {
	private SpriteBatch batch;
	private Texture snakeHead;
	
	private float snakeX=0;
	private float snakeY=0;

	private int snakeDirection=Constants.RIGHT;
	
	private Texture apple;
	private boolean appleAvailable=false;
	private float appleX, appleY;
	
	private Texture snakeBody;
	private Array<BodyPart> bodyParts= new Array<BodyPart>();
	private float snakeXBeforeUpdate=0, snakeYBeforeUpdate=0;
	
	//private ShapeRenderer shapeRenderer;
	//private static final int GRID_CELL=32;
	
	private boolean directionSet=false;
	private boolean hasHit=false;
	
	public float timer = Constants.MOVE_TIME;
	
	private STATE state=STATE.PLAYING;
	
	private BitmapFont bitmapFont;
	private GlyphLayout layout = new GlyphLayout();	
	
	private static final String GAME_OVER_TEXT="Game Over... press space to restart!";
	
	private int score=0;
	
	
	
	private Viewport viewport;
	private Camera camera;
	
	//TODO add a background
	//TODO add graphics
	//TODO use Hero for graphics fonts
	//TODO add special apples that disappear after a certain amount of time
	//TODO add bonuses (bullet time, fast time, score x2...), add particles effects
	//TODO add 'in the dark mode' so i can learn lighting 
	//TODO add multiplayer mode part mario kart part tron
	//TODO save highscores
	
	@Override
	public void show(){
		//shapeRenderer=new ShapeRenderer();
		batch=new SpriteBatch();
		snakeHead=new Texture(Gdx.files.internal("snakeHead.png"));
		apple=new Texture(Gdx.files.internal("apple.png"));
		snakeBody=new Texture(Gdx.files.internal("snakeBody.png"));
		bitmapFont = new BitmapFont();
		camera=new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		camera.position.set(Constants.WORLD_WIDTH/2,Constants.WORLD_HEIGHT/2,0);
		camera.update();
		viewport=new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT,camera);
	}
	
	@Override
	public void render(float delta){
		switch(state){
		case PLAYING: {
			queryInput();
			updateSnake(delta);
			checkAppleCollision();
			checkAndPlaceApple();
		}
		break;
		case GAME_OVER:{
			checkForRestart();
		}
		break;
		default:
			break;
		}		
		clearScreen();
		//drawGrid();
		draw();
	}
	
	private void checkForOutOfBounds(){
		if(snakeX>=viewport.getWorldWidth()){
			snakeX=0;
		}
		if(snakeX<0){
			snakeX=viewport.getWorldWidth()-Constants.SNAKE_MOVEMENT;
		}
		if(snakeY>=viewport.getWorldHeight()){
			snakeY=0;
		}
		if(snakeY<0){
			snakeY=viewport.getWorldHeight()-Constants.SNAKE_MOVEMENT;
		}
	}
	
	private void queryInput() {
		boolean lPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
		boolean rPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
		boolean uPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
		boolean dPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);
		
		if (lPressed) updateDirection(Constants.LEFT);
		if (rPressed) updateDirection(Constants.RIGHT);
		if (uPressed) updateDirection(Constants.UP);
		if (dPressed) updateDirection(Constants.DOWN);
	}
	
	private void moveSnake(){
		snakeXBeforeUpdate=snakeX;
		snakeYBeforeUpdate=snakeY;
		switch(snakeDirection){
			case Constants.RIGHT: {
				snakeX+=Constants.SNAKE_MOVEMENT;
				return;
			}
			case Constants.LEFT: {
				snakeX-=Constants.SNAKE_MOVEMENT;
				return;
			}
			case Constants.UP: {
				snakeY+=Constants.SNAKE_MOVEMENT;
				return;
			}
			case Constants.DOWN: {
				snakeY-=Constants.SNAKE_MOVEMENT;
				return;
			}
		}
	}
	
	private void updateDirection(int newSnakeDirection) {
		if (!directionSet && snakeDirection != newSnakeDirection) {
			directionSet = true;
			switch (newSnakeDirection) {
			case Constants.LEFT: {
				updateIfNotOppositeDirection(newSnakeDirection, Constants.RIGHT);
			}
			break;
			case Constants.RIGHT: {
				updateIfNotOppositeDirection(newSnakeDirection, Constants.LEFT);
			}
			break;
			case Constants.UP: {
				updateIfNotOppositeDirection(newSnakeDirection, Constants.DOWN);
			}
			break;
			case Constants.DOWN: {
				updateIfNotOppositeDirection(newSnakeDirection, Constants.UP);
			}
			break;
			}
		}
	}
	
	private void updateBodyPartsPosition(){
		if(bodyParts.size>0){
			BodyPart bodyPart=bodyParts.removeIndex(0);
			bodyPart.updateBodyPosition(snakeXBeforeUpdate,snakeYBeforeUpdate);
			bodyParts.add(bodyPart);
		}
	}
	
	private void checkAndPlaceApple(){
		if(!appleAvailable){
			do{
				appleX=MathUtils.random((int)(viewport.getWorldWidth()/Constants.SNAKE_MOVEMENT)-1)*Constants.SNAKE_MOVEMENT;
				appleY=MathUtils.random((int) (viewport.getWorldHeight())/Constants.SNAKE_MOVEMENT-1)*Constants.SNAKE_MOVEMENT;
				appleAvailable=true;		
			} while (appleX==snakeX && appleY == snakeY);
		}
	}
	
	private void clearScreen(){
		Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
	private void draw(){
		batch.begin();
		batch.draw(snakeHead, snakeX, snakeY); 
		for(BodyPart bodyPart:bodyParts){
			bodyPart.draw(batch);
		}
		if (appleAvailable) {
		batch.draw(apple, appleX, appleY);
		}
		if (state == STATE.GAME_OVER) {
            layout.setText(bitmapFont, GAME_OVER_TEXT);
            bitmapFont.draw(batch, GAME_OVER_TEXT, viewport.getWorldWidth() / 2 - layout.width / 2, viewport.getWorldHeight() / 2 - layout.height / 2);
        }
		/*
		String text="This Snake Game is AWESOME!";
		layout.setText(bitmapFont, text);
		bitmapFont.draw(batch, text, 0, layout.height);
		*/
		drawScore();
		batch.end();
	}
	
	private void checkAppleCollision(){
		if(appleAvailable && appleX == snakeX && appleY ==snakeY){
			BodyPart bodyPart = new BodyPart(snakeBody);
			bodyPart.updateBodyPosition(snakeX, snakeY);
			bodyParts.insert(0,bodyPart);
			addToScore();
			appleAvailable=false;
		}
	}
	
	private class BodyPart {
		private float x,y;
		private Texture texture;
		
		public BodyPart(Texture texture){
			this.texture=texture;
		}
		
		public void updateBodyPosition(float snakeX, float snakeY){
			this.x=snakeX;
			this.y=snakeY;
		}
		
		public void draw(Batch batch){
			if(!(x==snakeX && y==snakeY))batch.draw(texture,x,y);
		}
	}
	
	/*
	private void drawGrid(){
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		for(int x=0;x<Gdx.graphics.getWidth();x+=GRID_CELL){
			for(int y=0;y<Gdx.graphics.getHeight();y+=GRID_CELL){
				shapeRenderer.rect(x, y, GRID_CELL, GRID_CELL);
			}
		}
		shapeRenderer.end();
	}
	*/
	
	private void updateIfNotOppositeDirection(int newSnakeDirection, int oppositeDirection) {
			if (snakeDirection != oppositeDirection || bodyParts.size==0) snakeDirection = newSnakeDirection;
	}
	
	private void checkSnakeBodyCollision(){
		for(BodyPart bodyPart:bodyParts){
			if(bodyPart.x == snakeX && bodyPart.y==snakeY) state=STATE.GAME_OVER;
		}
	}
	
	private void updateSnake(float delta){
		if(!hasHit){
			timer-=delta;
			if(timer<=0){
				timer=Constants.MOVE_TIME;
				moveSnake();
				checkForOutOfBounds();
				updateBodyPartsPosition();
				checkSnakeBodyCollision();
				directionSet=false;
			}
		}
	}
	
	private void checkForRestart(){
		if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) doRestart();
	}
	
	private void doRestart(){
		state=STATE.PLAYING;
		bodyParts.clear();
		snakeDirection=Constants.RIGHT;
		directionSet=false;
		timer=Constants.MOVE_TIME;
		snakeX=0;
		snakeY=0;
		snakeXBeforeUpdate=0;
		snakeYBeforeUpdate=0;
		appleAvailable=false;
		score=0;
	}
	
	private void addToScore(){
		score+=Constants.POINTS_PER_APPLE;
	}
	
	private void drawScore(){
		if(state==STATE.PLAYING){
			String scoreAsString=Integer.toString(score);
		    layout.setText(bitmapFont, scoreAsString);
            bitmapFont.draw(batch, scoreAsString, viewport.getWorldWidth() - layout.width, viewport.getWorldHeight() - layout.height / 2);
        
			//BitmapFont.Glyph scoreBounds=bitmapFont.getBounds(scoreAsString);
			//bitmapFont.draw(batch,scoreAsString,(Gdx.graphics.getWidth()-scoreBounds.width)/2,(4*Gdx.graphics.getHeight()/5)-scoreBounds.height/2);
		}
	}
	
	//TODO implement splashscreen method
	private void splashscreen(){
		// O'nun'kawa present
	}
}
