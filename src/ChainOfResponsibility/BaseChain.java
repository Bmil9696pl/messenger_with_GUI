package ChainOfResponsibility;

import Message.IStrategy;

import java.io.FileNotFoundException;

public class BaseChain implements IChain{
    IChain next;


    @Override
    public void setNext(IChain next) {
        this.next = next;
    }

    @Override
    public IStrategy exec(byte[] data) throws FileNotFoundException {
        IStrategy result = null;
        if(next != null){
            result = next.exec(data);
        } else {
            return null;
        }
        return result;
    }
}
