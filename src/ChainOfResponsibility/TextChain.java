package ChainOfResponsibility;

import Message.IStrategy;
import Message.Text;

import java.io.FileNotFoundException;
import java.util.Arrays;

public class TextChain extends BaseChain{
    @Override
    public IStrategy exec(byte[] data) throws FileNotFoundException {
        IStrategy result = null;
        if(data[0] == 0){
            byte[] temp = Arrays.copyOfRange(data, 5, data.length);
            result = Text.decode(temp);
        } else {
            result = super.exec(data);
        }
        return result;
    }
}
