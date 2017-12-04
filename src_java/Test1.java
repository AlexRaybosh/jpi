import java.nio.charset.Charset;

import jpi.utils.loader.NativeUtils;


public class Test1 {


	
	public static void main(String[] args) {
		long s=System.currentTimeMillis();
		long fl=0;
		String str="Exchange";
		int N=500000000;
		for (int i=0;i<N;++i) {
			byte[] b=NativeUtils.myUtf8(str);//.getBytes(UTF8);
			fl+=b.length;
			//char[] cc=new char[str.length()];
			//str.getChars(0, str.length(), cc, 0);
			//fl+=cc.length;
		}
		long e=System.currentTimeMillis();
		double perf=N/((e-s)/1000d);
		System.out.printf("Perf %d - %.2f\n",(int)(fl/N),perf);
	}


}
