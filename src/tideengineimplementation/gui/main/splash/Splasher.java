package tideengineimplementation.gui.main.splash;

/**
 * Not used.
 */
public class Splasher
{
  public static void main(String[] args)
  {
    SplashWindow.splash(Splasher.class.getResource("paperboat.png"), null);
    SplashWindow.invokeMain("tideengineimplementation.gui.SwingUI", args);
    SplashWindow.disposeSplash();
  }  
}