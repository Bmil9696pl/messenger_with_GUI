package Message;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class Img extends BaseStrategy{
    private File file;
    private BufferedImage bufferedImage;

    public Img(File file) throws IOException {
        this.file = file;
        bufferedImage = ImageIO.read(file);
    }

    public Img(BufferedImage bufferedImage){
        file = null;
        this.bufferedImage = bufferedImage;
    }

    public BufferedImage print(){
        return bufferedImage;
    }

    @Override
    public byte[] encode(){
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);

            byte[] mask = {2};
            byte[] encoded = byteArrayOutputStream.toByteArray();
            return getBytes(mask, encoded);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String getType() {
        return "Image";
    }

    static public IStrategy decode(byte[] message) {
        ByteArrayInputStream bis = new ByteArrayInputStream(message);
        BufferedImage temp;

        try {
            temp = ImageIO.read(bis);
        } catch (IOException e) {
            return null;
        }

        return new Img(temp);
    }
}
