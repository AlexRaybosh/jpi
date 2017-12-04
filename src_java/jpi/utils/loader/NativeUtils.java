package jpi.utils.loader;

import java.nio.charset.Charset;

public final class NativeUtils {
	public static void access() {
		System.out.println("NativeUtils.access");
	}
	static {
		//System.loadLibrary("dipjava");
		initStringFieldIds(String.class);
	}
	private static native void initStringFieldIds(Class<String> strclass);
	

	public static native void testStringAccess(String str);
	
	public static native void testStringAccessHack1(int len, char c0, char c1, char c2, char c3, char c4, char c5, char c6, char c7, char c8, char c9, char c10, char c11, char c12, char c13, char c14, char c15);
	
	
	public static void testStringAccess1(String str) {
		int len=str.length();

		if (len < 19) {
			if (len < 9) {
				if (len<5) {
					
				} else {
					switch (len) {
					case 5:
						testStringAccessHack1(5, str.charAt(0), str.charAt(1), str.charAt(2), str.charAt(3),str.charAt(4),(char)0,(char)0,(char)0, (char)0,(char)0,(char)0,(char)0,(char)0,(char)0,(char)0,(char)0);
						break;
					case 6:
						testStringAccessHack1(6, str.charAt(0), str.charAt(1), str.charAt(2), str.charAt(3),str.charAt(4),str.charAt(5),(char)0,(char)0, (char)0,(char)0,(char)0,(char)0,(char)0,(char)0,(char)0,(char)0);
						break;
					case 7:
						testStringAccessHack1(7, str.charAt(0), str.charAt(1), str.charAt(2), str.charAt(3),str.charAt(4),str.charAt(5),str.charAt(6),(char)0, (char)0,(char)0,(char)0,(char)0,(char)0,(char)0,(char)0,(char)0);
						break;
					case 8:
						testStringAccessHack1(8, str.charAt(0), str.charAt(1), str.charAt(2), str.charAt(3),str.charAt(4),str.charAt(5),str.charAt(6),str.charAt(7),(char)0,(char)0,(char)0,(char)0,(char)0,(char)0,(char)0,(char)0);
						break;
					}
					
				}
			}
		}
	}
	
	public static void main(String[] args) {
		System.out.println("Survived");
	}
	
	public static byte[] myUtf8(String str) {
		int len=str.length();
		byte[] res=new byte[len];
		for (int i=0;i<len;++i) {
			char c=str.charAt(i);
			if (c<'\u0080') res[i]=(byte)c;
			else 
				return str.getBytes(UTF8);
		} 
		return res;
	}
	
	private static final Charset UTF8 = Charset.forName("UTF-8");
	private static final Charset UTF16LE = Charset.forName("UTF-16LE");
	private static final Charset UTF16BE = Charset.forName("UTF-16BE");
	
}

