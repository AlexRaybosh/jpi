import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jpi.utils.SpinLock;
import jpi.utils.loader.NativeSpinLock;


public class TestLock {

	/**
	 * @param args
	 */
	public static void mttest(String name, int tn, Runnable testable, int n) throws Exception {
		Thread[] threads=new Thread[tn];
		for (int i=0;i<tn;++i) {
			threads[i]=new Thread(testable);
		}
		for (Thread t : threads) t.start();
		
		long start=System.currentTimeMillis();
		for (Thread t : threads) t.join();
		long end=System.currentTimeMillis();
		double ns=1000000d*(end-start);
		double tns=ns/n;
		System.out.printf("%s, time %.1fns\n",name,(float)tns);
		
	}
	public static void main(String[] args) throws Exception {
		final SpinLock lock=new SpinLock();
		final int N=100000000;
		final int[] cnt=new int[1];
		Runnable luTest=new Runnable() {
			
			@Override
			public void run() {
				for (int i=0;i<N;++i) {
					lock.lock();
					++cnt[0];
					//lock.unlockUnsafe();
					lock.unlock();
				}
				
			}
		};
		mttest("Single spin lock unlock with an increment",1,luTest,N);
		System.out.println("Result : "+cnt[0]);
		cnt[0]=0;
		mttest("Dual spin lock unlock with an increment",2,luTest,2*N);
		System.out.println("Result : "+cnt[0]);
		
		cnt[0]=0;
		mttest("Quad spin lock unlock with an increment",4,luTest,4*N);
		System.out.println("Result : "+cnt[0]);
		
		final Lock rLock=new ReentrantLock();
		
		Runnable rlTest=new Runnable() {
			
			@Override
			public void run() {
				for (int i=0;i<N;++i) {
					rLock.lock();
					++cnt[0];
					rLock.unlock();
				}
				
			}
		};
		cnt[0]=0;
		mttest("Single rlock unlock with an increment",1,rlTest,N);
		System.out.println("Result : "+cnt[0]);
		/*cnt[0]=0;
		mttest("Dual rlock rlock with an increment",2,rlTest,2*N);
		System.out.println("Result : "+cnt[0]);
		*/
		
		final NativeSpinLock nLock=new NativeSpinLock();
		
		Runnable nTest=new Runnable() {
			
			@Override
			public void run() {
				for (int i=0;i<N;++i) {
					nLock.lock();
					++cnt[0];
					nLock.unlock();
				}
			}
		};
		cnt[0]=0;
		mttest("Single nlock unlock with an increment",1,nTest,N);
		System.out.println("Result : "+cnt[0]);
		cnt[0]=0;
		mttest("Dual nlock rlock with an increment",2,nTest,2*N);
		System.out.println("Result : "+cnt[0]);

	}

}
