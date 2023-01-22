package Message;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;

public class Sound extends BaseStrategy{
    private File file;
    private AudioInputStream ais;

    public static final AudioFormat AUDIO_FORMAT = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED
            , (float) 44100.0
            , 16
            , 2
            , 4
            , (float) 44100.0
            , false
    );

    public Sound(File file) throws UnsupportedAudioFileException, IOException {
        this.file = file;
        this.ais = AudioSystem.getAudioInputStream(file);
    }

    public Sound(AudioInputStream ais){
        this.ais = ais;
    }

    public AudioInputStream print(){
        return this.ais;
    }

    @Override
    public byte[] encode(){
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            byte[] mask = {1};
            byte[] encoded = audioInputStream.readAllBytes();
            return getBytes(mask, encoded);
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getType() {
        return "Sound";
    }

    static public IStrategy decode(byte[] message) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(message);
        AudioInputStream audioInputStream = null;

        audioInputStream = new AudioInputStream(byteArrayInputStream,AUDIO_FORMAT, byteArrayInputStream.available());

        return new Sound(audioInputStream);
    }
}
