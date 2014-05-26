package com.gs.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CharsetUtils {

	//fuqu加入的代码，用于猜编码
    public static String guessCharset(File file) {   
        String charset = "GBK";   
        byte[] first3Bytes = new byte[3];
        BufferedInputStream bis = null;
        try {
            boolean checked = false;   
            bis = new BufferedInputStream(new FileInputStream(file));   
            bis.mark(0);   
            int read = bis.read(first3Bytes, 0, 3);   
            if (read == -1)
                return charset;
            
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {   
                charset = "UTF-16LE";   
                checked = true;   
            } else if (first3Bytes[0] == (byte) 0xFE  
                    && first3Bytes[1] == (byte) 0xFF) {   
                charset = "UTF-16BE";   
                checked = true;   
            } else if (first3Bytes[0] == (byte) 0xEF  
                    && first3Bytes[1] == (byte) 0xBB  
                    && first3Bytes[2] == (byte) 0xBF) {   
                charset = "UTF-8";   
                checked = true;   
            }   
            bis.reset();   
            if (!checked) {   
                // int len = 0;   
                //int loc = 0;   
  
                while ((read = bis.read()) != -1) {   
                    //loc++;   
                    if (read >= 0xF0)   
                        break;   
                    if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK   
                        break;   
                    if (0xC0 <= read && read <= 0xDF) {   
                        read = bis.read();   
                        if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)   
                                                            // (0x80   
                            // - 0xBF),也可能在GB编码内   
                            continue;   
                        else  
                            break;   
                    } else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小   
                       read = bis.read();   
                        if (0x80 <= read && read <= 0xBF) {   
                            read = bis.read();   
                            if (0x80 <= read && read <= 0xBF) {   
                                charset = "UTF-8";   
                                break;   
                            } else  
                                break;   
                        } else  
                            break;   
                    }   
                }   
                // System.out.println( loc + " " + Integer.toHexString( read )   
                // );   
            }   
        } catch (Exception e) {   
            return "UTF-8";   
        }finally{
        	 try {
				bis.close();
			} catch (IOException e) {
			} 
        }
  
        return charset;   
    } 
    
    public static InputStreamReader getInputStreamReader(InputStream inputIntream) throws IOException {   
        String charset = "GBK";   
        byte[] first3Bytes = new byte[3];
        BufferedInputStream bis = null;
        boolean checked = false;   
        bis = new BufferedInputStream(inputIntream);   
        bis.mark(0);   
        int read = bis.read(first3Bytes, 0, 3);   
        if (read == -1){
        	bis.reset(); 
            return new InputStreamReader(bis,charset);
        }
        
        if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {   
            charset = "UTF-16LE";   
            checked = true;   
        } else if (first3Bytes[0] == (byte) 0xFE  
                && first3Bytes[1] == (byte) 0xFF) {   
            charset = "UTF-16BE";   
            checked = true;   
        } else if (first3Bytes[0] == (byte) 0xEF  
                && first3Bytes[1] == (byte) 0xBB  
                && first3Bytes[2] == (byte) 0xBF) {   
            charset = "UTF-8";   
            checked = true;   
        }   
        bis.reset();   
        if (!checked) {
            // int len = 0;   
            //int loc = 0;   
  
                while ((read = bis.read()) != -1) {   
                    //loc++;   
                if (read >= 0xF0)   
                    break;   
                if (0x80 <= read && read <= 0xBF) // 单独出现BF以下的，也算是GBK   
                    break;   
                if (0xC0 <= read && read <= 0xDF) {   
                    read = bis.read();   
                    if (0x80 <= read && read <= 0xBF) // 双字节 (0xC0 - 0xDF)   
                                                        // (0x80   
                        // - 0xBF),也可能在GB编码内   
                        continue;   
                    else  
                        break;   
                } else if (0xE0 <= read && read <= 0xEF) {// 也有可能出错，但是几率较小   
                   read = bis.read();   
                    if (0x80 <= read && read <= 0xBF) {   
                        read = bis.read();   
                        if (0x80 <= read && read <= 0xBF) {   
                            charset = "UTF-8";   
                            break;   
                        } else  
                            break;   
                    } else  
                        break;   
                }   
            }
        }
  
        bis.reset(); 
        return new InputStreamReader(bis,charset);
    } 
}
