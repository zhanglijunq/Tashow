package com.showjoy.tashow.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


/**
 * 对象序列化.
 * @author yangxiao
 */
public class SerializeToFlatByte {
    
	/**
	 * 对象序列化.
	 * @param object
	 * @return
	 */
    public static byte[] serializeToByte(Object object){
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(object);
            oos.flush();
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }
    
    /**
     * 对象反序列化.
     * @param bs
     * @return
     */
    public static Object restoreObject(byte[] bs) {
    	Object object = null;
    	try {
    		ByteArrayInputStream bis = new ByteArrayInputStream(bs);
    		ObjectInputStream ois = new ObjectInputStream(bis);
    		object = (Object)ois.readObject();
    	} catch (Exception e) {
    		// TODO: handle exception
    		e.printStackTrace();
    	}
    	return object;
    }
}
