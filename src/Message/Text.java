package Message;

import java.nio.charset.StandardCharsets;

public class Text extends BaseStrategy{
    private String message;

    public Text(String message){
        this.message = message;
    }

    public String print(){
        return message;
    }

    @Override
    public byte[] encode() {
        byte[] mask = {0};
        byte[] encoded = this.message.getBytes(StandardCharsets.UTF_8);
        return getBytes(mask, encoded);
    }

    @Override
    public String getType() {
        return "Text";
    }

    static public IStrategy decode(byte[] message) {
        return new Text(new String(message, StandardCharsets.UTF_8));
    }
}
