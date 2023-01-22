package ChainOfResponsibility;

import Message.IStrategy;

import java.io.FileNotFoundException;

public interface IChain {
    void setNext(IChain next);
    IStrategy exec(byte[] data) throws FileNotFoundException;
}
