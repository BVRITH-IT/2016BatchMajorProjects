package CT;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.*;
public class SHA_256
{  
public static String hashCode(String rno)
        {byte[] hashedBytes=null;
 
    try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
       hashedBytes = digest.digest(rno.getBytes("UTF-8"));
 
       
    } catch (Exception ex) {
       System.out.println(ex);
    }
	 return convertByteArrayToHexString(hashedBytes);
}
 static String convertByteArrayToHexString(byte[] arrayBytes) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < arrayBytes.length; i++) {
            stringBuffer.append(Integer.toString((arrayBytes[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return stringBuffer.toString();
  }

  public static void main(String a[]){
  
  //System.out.println(SHA.SHA_1("hi","h"));
  }
}
