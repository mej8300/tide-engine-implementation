package tideengineimplementation.gui.main.splash;

public class Splasher
{
  public static void main(String[] args)
  {
    SplashWindow.splash(Splasher.class.getResource("paperboat.png"));
    SplashWindow.invokeMain("tideengineimplementation.gui.SwingUI", args);
    SplashWindow.disposeSplash();
  }  
}