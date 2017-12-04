package jpi.utils;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Dictionary {
	

	private static AtomicInteger intKeyCount=new AtomicInteger();

	private final HashMap<String,Entry> strMap=new HashMap<String, Entry>();
	private final HashMap<Integer,Entry> intMap=new HashMap<Integer, Entry>();
	
	public static class BinKey {
	
	}
	
	static class Entry {
		public Entry(String str) {
			stringKey=str;
//			binKey=new BinKey(str);
//			intKey=intKeyCount.getAndIncrement();
		}
		public Entry(byte[] bin) {
			//stringKey=str;
			//binKey=new BinKey(str);
			intKey=intKeyCount.getAndIncrement();
		}
		private String stringKey;
		private BinKey binKey;
		private AtomicInteger refCount=new AtomicInteger();
		private int intKey;
	}
	private Dictionary(Dictionary dic) {
		// TODO Auto-generated constructor stub
	}

	protected Dictionary() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	private final static AtomicReference<Dictionary> ref=new AtomicReference<Dictionary>(new Dictionary());

	private final static Lock lock=new ReentrantLock();
	
/*	public static int intValue(String str) {
		Dictionary dic=ref.get();
//		Integer v=dic.lookup(str);
		if (v==null) {
			lock.lock();
			try {
//				v=dic.lookup(str);
				if (v==null) {
					Dictionary ndic=new Dictionary(dic);
//					v=cnt++;
					ndic.set(str,v);
					ref.set(ndic);
					dic.clear();
				}
			} finally {
				lock.unlock();
			}
		}
		return v;
	}
*/
	private void set(String str, Integer v) {
//		strMap.put(str, v);
//		intMap.put(v, str);
		
	}

	private void clear() {
		
	}


//	private Integer lookup(String str) {
//		return strMap.get(str);
//	}
	
	
	
}
