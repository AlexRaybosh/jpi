package jpi.utils.loader;

import sun.misc.Unsafe;

public class NativeSpinLock {
	final static Unsafe unsafe=JPiAPI.getUnsafe();
	long addr;
	
	public NativeSpinLock() {
		addr=unsafe.allocateMemory(4);
		unsafe.putInt(addr, 0);
	}
	@Override
	protected void finalize() throws Throwable {
		if (addr!=0) {
			unsafe.freeMemory(addr);
			addr=0;
		}
	}
	public final void lock() {
		JPiAPI.lock_cas(addr);
	}
	public final void unlock() {
		JPiAPI.unlock_sfence(addr);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NativeSpinLock l=new NativeSpinLock();
		while (true) {
			l.lock();
			l.unlock();
		}
	}

}
