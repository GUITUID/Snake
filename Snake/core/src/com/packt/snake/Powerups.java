package com.packt.snake;

import com.badlogic.gdx.math.MathUtils;
import com.packt.snake.GameScreen.BodyPart;

public class Powerups {

	String powerupType;
	Boolean powerupAvailable;
	float powerupX, powerupY;
	
	private enum powerupType {
		Apple, Superapple, Booster, Slime
	}
	
	private void setpowerupType(String powerupType){
		powerupType=powerupType;
	}
	
	
	private void checkAndPlacePowerup(Snake snake){
		if(!powerupAvailable){
			do{
				powerupX=MathUtils.random((int)(viewport.getWorldWidth()/snake.getSNAKE_MOVEMENT())-1)*snake.getSNAKE_MOVEMENT();
				powerupY=MathUtils.random((int) (viewport.getWorldHeight())/snake.getSNAKE_MOVEMENT()-1)*snake.getSNAKE_MOVEMENT();
				powerupAvailable=true;		
			} while (powerupX==snake.getsnakeX() && powerupY == snake.getsnakeY());
		}
	}
	
	private void checkPowerupCollision(Snake snake){
		if(powerupAvailable && powerupX == snake.getsnakeX() && powerupY ==snake.getsnakeY()){
			BodyPart bodyPart = new BodyPart(snakeBody);
			bodyPart.updateBodyPosition(snakeX, snakeY);
			bodyParts.insert(0,bodyPart);
			addToScore();
			powerupAvailable=false;
		}
	}
	
	// an Apple can pop when no one is available and gives 10 points
	
	// a Superapple can pop every 30 seconds and desappear after 40 seconds and gives 30 points
	
	// a Booster powerup multiplies the snake speed by 2
	
	// a Slime powerup divide the speed of the opponent snake by 2
	
	
	
}
