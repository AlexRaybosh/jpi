package jpi.utils;

import java.util.concurrent.atomic.AtomicInteger;

import jpi.utils.loader.JPiAPI;

public class SpinLock {
	AtomicInteger val=new AtomicInteger();
	public final void lock() {
		for (;;) {
			if (val.compareAndSet(0, 1)) return;
			Thread.yield();
		}
	}
	public final void unlockUnsafe() {
		val.lazySet(0);
	}
	public final void unlock() {
		//DipAPI.sfence();
		//val.lazySet(0);
		val.set(0);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
