package jpi.utils.loader;

public class Consts {

	public final static String NATIVE_API_CLASS_NAME="jpi.utils.loader.JPiNativeAPI";
	public final static String API_CLASS_NAME="jpi.utils.loader.JPiAPI";
	public final static String SO_LOADER_CLASS_NAME="jpi.utils.loader.SoLoader";	
	public final static String SO_LOADER_RESOURCE_BYTES_NAME=String.format("/%s.bytes", SO_LOADER_CLASS_NAME.replaceAll("\\.", "/"));
    public static final String SYSTEM_PROPERTIES_FILE   = "jpi.properties";
    public static final String KEY_JPI_LIB_PATH             = "jpi.lib.path";
    public static final String KEY_JPI_LIB_NAME             = "jpi.lib.name";
    public static final String KEY_JPI_TEMPDIR              = "jpi.tempdir";
    public static final String KEY_JPI_USE_SYSTEMLIB        = "jpi.use.systemlib";

	}
