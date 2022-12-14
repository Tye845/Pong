import com.badlogic.gdx.ApplicationAdapter; 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer; 
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle; 
import com.badlogic.gdx.math.Circle; 
import com.badlogic.gdx.Input.Keys; 
import com.badlogic.gdx.math.Vector2; 
import com.badlogic.gdx.math.MathUtils; 
import com.badlogic.gdx.math.Intersector; 
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class Pong extends ApplicationAdapter
{
    /*
     * Starts the game in "easy" mode for the right side player (the one using the arrow keys) or the left side player, (the one using WASD). 
     * In easy mode the paddle size is increased allowing for easier use.
     */
    
    private OrthographicCamera camera; //the world camera 
    private Viewport viewport; //maintains the ratios of the world
    private ShapeRenderer renderer; //used to draw textures and fonts 
    private BitmapFont font; //used to draw fonts (text)
    private SpriteBatch batch; //also needed to draw fonts (text)

    private Rectangle leftPaddle;//Rectangle object to represent the left paddle
    private Rectangle rightPaddle; //Rectangle object to represent the right paddle
    private Circle ball; //Circle object to represent the ball 
    private float ballAngle; //holds the angle the ball is traveling
    private boolean started = false; //has the game started yet
    private int player1Score = 0; //scores
    private int player2Score = 0; 

    public static final float WORLD_WIDTH = 800; 
    public static final float WORLD_HEIGHT = 480;

    public static final float PADDLE_WIDTH = 20; 
    public static final float PADDLE_HEIGHT = 80;
    public static final float RADIUS = 15;
    public static final float PADDLE_SPEED = 10;
    public static final float BALL_SPEED = 10;

    @Override
    public void create(){
        camera = new OrthographicCamera(); //non-moving camera
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera); //maintains world units from screen units
        renderer = new ShapeRenderer(); 
        font = new BitmapFont(); 
        batch = new SpriteBatch(); 

        
        leftPaddle = new Rectangle(0, 0, PADDLE_WIDTH, PADDLE_HEIGHT); 
        rightPaddle = new Rectangle(WORLD_WIDTH - PADDLE_WIDTH, WORLD_HEIGHT / 2 - PADDLE_HEIGHT / 2,
            PADDLE_WIDTH, PADDLE_HEIGHT);
        ball = new Circle(WORLD_WIDTH / 2 - RADIUS, WORLD_HEIGHT / 2 - RADIUS, RADIUS);
        ballAngle = 0; 
    }

    @Override//called 60 times a second, all the drawing is in here, or in helper
    //methods that are called from here
    public void render(){
        viewport.apply(); 
        
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //if the game has started adjust the position
        //of the ball based on the ball angle
        if(started)
        {
            ball.x += BALL_SPEED * MathUtils.cosDeg(ballAngle);//cosine gets the change in x distance
            ball.y += BALL_SPEED * MathUtils.sinDeg(ballAngle); //sine gets the change in y distance
            //

        }

        //check for input
        if(Gdx.input.isKeyPressed(Keys.UP))
        {
            rightPaddle.y += PADDLE_SPEED;           
        }
        if(Gdx.input.isKeyPressed(Keys.DOWN))
        {
            rightPaddle.y -= PADDLE_SPEED;           
        }

        if(Gdx.input.isKeyPressed(Keys.W))
        {
            leftPaddle.y += PADDLE_SPEED;           
        }
        if(Gdx.input.isKeyPressed(Keys.S))
        {
            leftPaddle.y -= PADDLE_SPEED; 
        }
        //start the game normally
        if(Gdx.input.isKeyPressed(Keys.SPACE))
        {
            started = true;
            leftPaddle = new Rectangle(0, 0, PADDLE_WIDTH, PADDLE_HEIGHT); 
            rightPaddle = new Rectangle(WORLD_WIDTH - PADDLE_WIDTH, WORLD_HEIGHT / 2 - PADDLE_HEIGHT / 2, PADDLE_WIDTH, PADDLE_HEIGHT);
        }
        //Starts the game in "easy" mode for the right side player (the one using the arrow keys). In easy mode the paddle size is increased allowing for easier use.
        if(Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT))
        {
            started = true;  
            rightPaddle = new Rectangle(WORLD_WIDTH - PADDLE_WIDTH, WORLD_HEIGHT / 2 - PADDLE_HEIGHT / 2, PADDLE_WIDTH, PADDLE_HEIGHT + 60);
            leftPaddle = new Rectangle(0, 0, PADDLE_WIDTH, PADDLE_HEIGHT); 
        }
        //Starts the game in "easy" mode for the left side player (the one using "WASD" controls). In easy mode the paddle is larger and easier to use.
        if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT))
        {
            started = true;  
            leftPaddle = new Rectangle(0, 0, PADDLE_WIDTH, PADDLE_HEIGHT + 60); 
            rightPaddle = new Rectangle(WORLD_WIDTH - PADDLE_WIDTH, WORLD_HEIGHT / 2 - PADDLE_HEIGHT / 2, PADDLE_WIDTH, PADDLE_HEIGHT);
        }

        if(leftPaddle.y > WORLD_HEIGHT)
            leftPaddle.y = WORLD_HEIGHT - PADDLE_HEIGHT;
        if(leftPaddle.y < 0)
            leftPaddle.y = PADDLE_HEIGHT;

        if(rightPaddle.y > WORLD_HEIGHT)
            rightPaddle.y = WORLD_HEIGHT - PADDLE_HEIGHT;
        if(rightPaddle.y < 0)
            rightPaddle.y = PADDLE_HEIGHT;

        if(ball.y + RADIUS > WORLD_HEIGHT)
            ballAngle *= -1; 
        if(ball.y - RADIUS < 0)
            ballAngle *= -1; 

        //check for collision
        if(Intersector.overlaps(ball, rightPaddle))
        {
            //determine what angle the ball will bounce off the paddle
            float percentOfPaddle = (ball.y - rightPaddle.y) / PADDLE_HEIGHT;
            ballAngle = 225 - (percentOfPaddle * 90); //place for constants possibly? 

        }

        if(Intersector.overlaps(ball, leftPaddle))
        {
            float percentOfPaddle = (ball.y - leftPaddle.y) / PADDLE_HEIGHT;

            ballAngle = -45 + (percentOfPaddle * 90); 

        }

        if(ball.x > WORLD_WIDTH)
        {
            ball.x += BALL_SPEED * MathUtils.cosDeg(ballAngle);
            ball.y += BALL_SPEED * MathUtils.sinDeg(ballAngle);
            ball.x = WORLD_WIDTH / 2 - RADIUS;
            ball.y = WORLD_HEIGHT / 2 - RADIUS;
            ballAngle = 0;
            started = false;
            player2Score++;            
        }
        if(ball.x < 0)
        {
            ball.x += BALL_SPEED * MathUtils.cosDeg(ballAngle);
            ball.y += BALL_SPEED * MathUtils.sinDeg(ballAngle);
            ball.x = WORLD_WIDTH / 2 - RADIUS;
            ball.y = WORLD_HEIGHT / 2 - RADIUS;
            ballAngle = 0;
            started = false;
            player1Score++;            
        }   

        //draw everything on the screen with our renderer
        //draw each object based on its attributes
        renderer.setProjectionMatrix(viewport.getCamera().combined);
        renderer.setColor(Color.WHITE); 
        renderer.begin(ShapeType.Filled);

        renderer.rect(leftPaddle.x, leftPaddle.y, leftPaddle.width, leftPaddle.height);
        renderer.rect(rightPaddle.x, rightPaddle.y, rightPaddle.width, rightPaddle.height);
        renderer.circle(ball.x, ball.y, ball.radius);

        renderer.end();

        GlyphLayout layout = new GlyphLayout(font, "Press the Space Bar for Regular Mode or Try the Control keys for Easy Mode for One of the Players!");
        batch.begin();
        if(!started)
        {

            font.draw(batch, layout, 
                WORLD_WIDTH / 2 - layout.width / 2, 
                WORLD_HEIGHT/2 + layout.height / 2 + 20);

        }
        font.draw(batch, player1Score + ":" + player2Score, WORLD_WIDTH / 2 - 20, 440); 
        batch.end(); 
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true); 
    }

    @Override
    public void dispose(){
        renderer.dispose(); 
        batch.dispose(); 
    }

}
