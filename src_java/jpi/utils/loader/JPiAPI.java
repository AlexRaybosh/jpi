package jpi.utils.loader;

import java.lang.reflect.Field;

import sun.misc.Unsafe;


@SuppressWarnings("static-access")
public class JPiAPI {

	private final static JPiNativeAPI nativeApi;
	static {
		nativeApi=APILoader.load();
	}
	
	final private static Unsafe unsafe;
	final public static int byteArrayBaseOffset;
	final public static int charArrayBaseOffset;
	final public static int byteArrayIndexScale;
	final public static int charArrayIndexScale;
	final private static Field strValueField;
	final private static long strValueFieldOffset;
	
	static {
		try {
		   Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
		   field.setAccessible(true);
		   unsafe = (sun.misc.Unsafe) field.get(null);
		   byteArrayBaseOffset=unsafe.arrayBaseOffset(byte[].class);
		   charArrayBaseOffset=unsafe.arrayBaseOffset(char[].class);
		   byteArrayIndexScale=unsafe.arrayIndexScale(byte[].class);
		   charArrayIndexScale=unsafe.arrayIndexScale(char[].class);
		   strValueField=String.class.getDeclaredField("value");
		   //unsafe.fieldOffset(strValueField);
		   strValueFieldOffset=unsafe.objectFieldOffset(strValueField);
		} catch (Exception e) {
		   throw new AssertionError(e);
		}
	}
	public static Unsafe getUnsafe() {
		return unsafe;
	}
	
	public static void f(String str) {
		Object strValue=unsafe.getObject(str, strValueFieldOffset);
		unsafe.putChar(strValue, (long)charArrayBaseOffset, 'F');
		
	}
	
	public static void  testArray(byte[] arr) {
		unsafe.arrayBaseOffset(byte[].class);
	}
	
	public static void testNative() {
		nativeApi.testNativeCall();
	}
	public static void lock_cas(long addr) {
		nativeApi.lock_cas(addr);
	}
	public static void lock_tas(long addr) {
		nativeApi.lock_tas(addr);
	}
	public static void unlock_sfence(long addr) {
		nativeApi.unlock_sfence(addr);
	}
	public static void unlock_dummy(long addr) {
		nativeApi.unlock_dummy(addr);
	}
	public static void unlock_lock_release(long addr) {
		nativeApi.unlock_lock_release(addr);
	}
	public static void sfence() {
		nativeApi.sfence();
	}
}
