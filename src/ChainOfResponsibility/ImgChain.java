package ChainOfResponsibility;

import Message.IStrategy;
import Message.Img;

import java.io.FileNotFoundException;
import java.util.Arrays;

public class ImgChain extends BaseChain{
    @Override
    public IStrategy exec(byte[] data) throws FileNotFoundException {
        IStrategy result = null;
        if(data[0] == 2){
            byte[] temp = Arrays.copyOfRange(data, 5, data.length);
            result = Img.decode(temp);
        } else {
            result = super.exec(data);
        }
        return result;
    }
}
