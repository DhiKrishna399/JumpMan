package com.dhivak.jumpman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class JumpMan extends ApplicationAdapter {
	SpriteBatch batch;
	int spriteState = 0;
	int pause = 0;
	float velocity = 0;
	float gravity = 0.2f;
	int yPos = 0;
	int score = 0;
	Rectangle  jumpManRect;
	//Bitmapfont lets you display text on the screen
    BitmapFont font;
    int gameState = 0; //Represents the state of the game, start, finished, playing etc


	//Anytime you want to add an image use texture
    Texture background;
    //Use an array for the sprite bc he has multiple forms (running)
    Texture[] jumpMan;
    Texture jumpManDead;


	ArrayList<Integer> coinXPos = new ArrayList<Integer>();
	ArrayList<Integer> coinYPos = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRect = new ArrayList<Rectangle>();
	Texture coin;
	int coinCount;
	Random random;

    ArrayList<Integer> bombXPos = new ArrayList<Integer>();
    ArrayList<Integer> bombYPos = new ArrayList<Integer>();
    ArrayList<Rectangle> bombRect = new ArrayList<Rectangle>();
    Texture bomb;
    int bombCount;



	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");

		jumpMan = new Texture[4];
		jumpMan[0] = new Texture("frame-1.png");
		jumpMan[1] = new Texture("frame-2.png");
		jumpMan[2] = new Texture("frame-3.png");
		jumpMan[3] = new Texture("frame-4.png");
		jumpManDead = new Texture("dizzy-1.png");

		yPos = Gdx.graphics.getHeight()/2;

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		random = new Random();

		font = new BitmapFont();
		font.setColor(Color.SALMON);
		font.getData().setScale(10);


	}

	public void makeCoin() {
	    float height = random.nextFloat() * Gdx.graphics.getHeight();
	    coinYPos.add((int)height);
	    coinXPos.add(Gdx.graphics.getWidth());
    }

    public void makeBomb(){
        float height = random.nextFloat() * Gdx.graphics.getHeight();
        bombYPos.add((int)height);
        bombXPos.add(Gdx.graphics.getWidth());

    }

	//Render gets called over and over again through the program
	@Override
	public void render () {
	    batch.begin(); //Begin and end start adding everything
        batch.draw(background, 0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if(gameState == 1){
            //Game is playing

            //If statement to control jumps
            if(Gdx.input.justTouched()){
                velocity = -10;
            }


            //Create coins
            if(coinCount < 100){
                coinCount++;
            } else {
                coinCount = 0;
                makeCoin();
            }

            coinRect.clear();
            for(int i = 0; i < coinXPos.size(); i++){
                batch.draw(coin, coinXPos.get(i), coinYPos.get(i));
                coinXPos.set(i, coinXPos.get(i) - 6);
                //Make a rectangle around each coin
                coinRect.add(new Rectangle(coinXPos.get(i), coinYPos.get(i), coin.getWidth(), coin.getHeight()));
            }

            //Create bombs
            if(bombCount < 300){
                bombCount++;
            } else {
                bombCount = 0;
                makeBomb();
            }

            bombRect.clear();
            for(int i = 0; i < bombXPos.size(); i++){
                batch.draw(bomb, bombXPos.get(i), bombYPos.get(i));
                bombXPos.set(i, bombXPos.get(i) - 10);
                bombRect.add(new Rectangle(bombXPos.get(i), bombYPos.get(i), bomb.getWidth(), bomb.getHeight()));
            }

            //If statements fpr gravity and changing sprite graphics
            if(pause < 8){
                pause++;
            } else {
                pause = 0;

                if (spriteState < 3) {
                    spriteState++;
                } else {
                    spriteState = 0;
                }
            }

            velocity = velocity + gravity;
            yPos -= velocity;

            if(yPos <= 0){
                yPos = 0;
            }

        } else if(gameState == 0){
            //Waiting to start
            if(Gdx.input.justTouched()){
                gameState = 1;
            }

        } else if(gameState == 2){
            //GameEnds
            if(Gdx.input.justTouched()){
                gameState = 1;
                yPos = Gdx.graphics.getHeight() / 2;
                score = 0;
                velocity = 0;
                coinXPos.clear();
                coinYPos.clear();
                coinRect.clear();
                coinCount = 0;
                bombXPos.clear();
                bombYPos.clear();
                bombRect.clear();
                bombCount = 0;
            }

        }


        //Sprite will be draw according to position, meaning the differnt graphics are based on his position
        batch.draw(jumpMan[spriteState], (Gdx.graphics.getWidth()/2 - (jumpMan[spriteState].getWidth())), yPos);


		//Check for collisions
        jumpManRect = new Rectangle(Gdx.graphics.getWidth()/2 - jumpMan[spriteState].getWidth() / 2, yPos, jumpMan[spriteState].getWidth(), jumpMan[spriteState].getHeight());

        for(int i = 0; i < coinRect.size(); i++){
            if(Intersector.overlaps(jumpManRect, coinRect.get(i))){
                //Gdx.app.log("Test", "Collision.");
                score++;
                coinRect.remove(i);
                coinXPos.remove(i);
                coinYPos.remove(i);
                break;
            }
        }

        for(int i = 0; i < bombRect.size(); i++){
            if(Intersector.overlaps(jumpManRect, bombRect.get(i))){
                //Gdx.app.log("Test", "Bomb.");
                batch.draw(jumpManDead, (Gdx.graphics.getWidth()/2 - (jumpMan[spriteState].getWidth())), yPos);
                gameState = 2;
            }
        }

        font.draw(batch, String.valueOf(score), 100, 200);
	    batch.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();

	}
}
