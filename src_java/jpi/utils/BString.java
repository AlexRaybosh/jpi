package jpi.utils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import jpi.utils.loader.JPiAPI;



public class BString {

	public static final Charset UTF8 = Charset.forName("UTF-8");
	final byte[] bytes;
	int hash;
	public BString(String str) {
		if (str==null) str="";
		bytes=convert(str);
	}
	
	public BString(byte[] b) {
		if (b==null || b.length==0) b=new byte[1];
		bytes=new byte[b.length];
		System.arraycopy(b, 0, bytes, 0, b.length);
	}
	
	@Override
	public int hashCode() {
		if (hash>0) return hash;
		final int prime = 31;
		hash = 1;
		hash = prime * hash + Arrays.hashCode(bytes);
		return hash;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		BString other = (BString) obj;
		if (hashCode()!=other.hashCode()) return false;
		if (!Arrays.equals(bytes, other.bytes)) return false;
		return true;
	}
	
	public static byte[] convert(String str) {
		int len=str.length();
		byte[] res=new byte[len+1];
		for (int i=0;i<len;++i) {
			char c=str.charAt(i);
			if (c<'\u0080') res[i]=(byte)c;
			else {
				byte[] tmp=str.getBytes(UTF8);
				res=new byte[tmp.length+1];
				System.arraycopy(tmp, 0, res, 0, tmp.length);
				return res;
			}
		} 
		return res;
	}
}
