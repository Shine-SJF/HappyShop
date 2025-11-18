import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class AudioFeature extends JFrame {
    private Clip backgroundClip;
    private static AudioFeature instance;   // singleton

    private AudioFeature() { initClip(); }

    public static AudioFeature getInstance() {
        if (instance == null) instance = new AudioFeature();
        return instance;
    }

    /** Load and prepare the looping clip */
    private void initClip() {
        try {
            URL url = getClass().getResource("/resources/music/happyshop_loop.wav");
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(ais);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException |
                 IOException |
                 LineUnavailableException e) {
            e.printStackTrace(); // handle gracefully in production
        }
    }

    /** Call when the shop UI is shown */
    public void startMusic() {
        if (backgroundClip != null && !backgroundClip.isRunning())
            backgroundClip.start();
    }

    /** Call when leaving the shop */
    public void stopMusic() {
        if (backgroundClip != null && backgroundClip.isRunning())
            backgroundClip.stop();
    }

    /** Optional: expose volume control */
    public void setVolume(float value) { // value 0.0‑1.0
        if (backgroundClip != null) {
            FloatControl vol = (FloatControl) backgroundClip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log10(value) * 20);
            vol.setValue(dB);
        }
    }
}