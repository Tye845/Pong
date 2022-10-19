import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
public class GameLauncher
{
    public static void main(String[] args)
    {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration(); 
        config.width = 800;//set the width of your screen window
        config.height = 480; //set the height of your screen window
       
        LwjglApplication launcher = new LwjglApplication(new Pong(), config);
    }
}
