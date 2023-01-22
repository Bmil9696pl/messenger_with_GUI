package ChainOfResponsibility;

import Message.IStrategy;

import java.io.FileNotFoundException;

public class ChainHandler {
    public static IStrategy exec(byte[] data) throws FileNotFoundException {
        TextChain text = new TextChain();
        ImgChain img = new ImgChain();
        SoundChain sound = new SoundChain();

        text.setNext(img);
        img.setNext(sound);

        return text.exec(data);
    }
}
