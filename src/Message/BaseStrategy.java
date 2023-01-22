package Message;

import java.io.IOException;
import java.math.BigInteger;

public class BaseStrategy implements IStrategy{
    @Override
    public byte[] encode() throws IOException {
        return new byte[0];
    }

    @Override
    public String getType() {
        return null;
    }

    static byte[] getBytes(byte[] mask, byte[] encoded) {
        byte[] length = new byte[4];
        int n = 4;
        byte[] temp2 = BigInteger.valueOf(encoded.length).toByteArray();
        n-=temp2.length;
        try{
            for (byte temp :
                    temp2) {
                length[n] = temp;
                n++;
            }
        } catch (IndexOutOfBoundsException ex){
            throw ex;
        }
        byte[] ret = new byte[mask.length + length.length + encoded.length];
        System.arraycopy(mask, 0, ret,0, mask.length);
        System.arraycopy(length, 0, ret,mask.length, length.length);
        System.arraycopy(encoded, 0,ret,mask.length + length.length, encoded.length);
        return ret;
    }
}
