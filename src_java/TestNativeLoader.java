import jpi.utils.loader.JPiAPI;



public class TestNativeLoader {


	public static void main(String[] args) throws Throwable {
		JPiAPI.testNative();
		String str="Huck";
		JPiAPI.f(str);
		System.out.println(str);
	}

}
