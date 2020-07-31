package trong.lixco.com.thai.bean;

import java.io.UnsupportedEncodingException;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class EncodeDecode {
	// Mã hóa một đoạn text
	   // Encode
	   public static String encodeString(String text)
	           throws UnsupportedEncodingException {
	       byte[] bytes = text.getBytes("UTF-8");
	       String encodeString = Base64.encode(bytes);
	       return encodeString;
	   }
	 
	   // Giải mã hóa một đoạn text (Đã mã hóa trước đó).
	   // Decode
	   public static String decodeString(String encodeText)
	           throws UnsupportedEncodingException {
	       byte[] decodeBytes = Base64.decode(encodeText);
	       String str = new String(decodeBytes, "UTF-8");
	       return str;
	   }
}
