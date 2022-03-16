package elevator.util;

import java.io.*;

public class Serializer {
    public static <T extends Serializable> T fromByteArray(byte[] bytes, Class<T> type){
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        T obj = null;
        try {
            in = new ObjectInputStream(bis);
            obj = (T) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
            }
        }
        return obj;
    }

    public static byte[] toByteArray(Serializable object){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            System.err.println("Could not serialize " + object.getClass().getSimpleName() + ": " + object);
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }
}
