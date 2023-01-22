package ChainOfResponsibility;

import Message.IStrategy;
import Message.Sound;

import java.io.FileNotFoundException;
import java.util.Arrays;

public class SoundChain extends BaseChain{
    @Override
    public IStrategy exec(byte[] data) throws FileNotFoundException {
        IStrategy result = null;
        if(data[0] == 1){
            byte[] temp = Arrays.copyOfRange(data, 5, data.length);
            result = Sound.decode(temp);
        } else {
            result = super.exec(data);
        }
        return result;
    }
}
