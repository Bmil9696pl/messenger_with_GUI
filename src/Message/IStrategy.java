package Message;

import java.io.IOException;

public interface IStrategy {
    byte[] encode() throws IOException;

    String getType();
}
