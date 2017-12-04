/*--------------------------------------------------------------------------
 * The below code is based on org.xerial Dip native api code.
 * 
 * The original copyright:
 *  Copyright 2011 Taro L. Saito
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *--------------------------------------------------------------------------*/
//--------------------------------------
package jpi.utils.loader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ProcessBuilder.Redirect;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.management.RuntimeErrorException;

/**
 * <b>Internal only - Do not use this class.</b> This class loads a native
 * library of dip-java (jpijava.dll, libjpijava.so, etc.) according to the
 * user platform (<i>os.name</i> and <i>os.arch</i>). The natively compiled
 * libraries bundled to dip-java contain the codes of the original dip and
 * JNI programs to access Dip.
 * 
 * In default, no configuration is required to use dip-java, but you can load
 * your own native library created by 'make native' command.
 * 
 * This LibLoader searches for native libraries (jpijava.dll,
 * libdip.so, etc.) in the following order:
 * <ol>
 * <li>If system property <i>dip.use.systemlib</i> is set to true,
 * lookup folders specified by <i>java.lib.path</i> system property (This is the
 * default path that JVM searches for native libraries)
 * <li>(System property: <i>dip.lib.path</i>)/(System property:
 * <i>dip.lib.name</i>)
 * <li>One of the libraries embedded in dip-java-(version).jar extracted into
 * (System property: <i>java.io.tempdir</i>). If
 * <i>dip.tempdir</i> is set, use this folder instead of
 * <i>java.io.tempdir</i>.
 * </ol>
 * 
 * <p>
 * If you do not want to use folder <i>java.io.tempdir</i>, set the System
 * property <i>dip.tempdir</i>. For example, to use
 * <i>/tmp/my</i> as a temporary folder to copy native libraries, use -D option
 * of JVM:
 * 
 * <pre>
 * <code>
 * java -Ddip.tempdir="/tmp/my" ...
 * </code>
 * </pre>
 * 
 * </p>
 * 
 * Original author:
 * @author leo
 * 
 */
public class APILoader {

    private static boolean     isLoaded                        = false;
    /**
     * load system properties when configuration file of the name
     * {@link #Consts.SYSTEM_PROPERTIES_FILE} is found
     */
    private static void loadJPiSystemProperties() {
    	InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(Consts.SYSTEM_PROPERTIES_FILE);
            if (is == null) return; // no configuration file is found 
            // Load property file
            Properties props = new Properties();
            props.load(is);
            is.close();
            Enumeration< ? > names = props.propertyNames();
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                if (name.startsWith("jpi.")) {
                    if (System.getProperty(name) == null) {
                        System.setProperty(name, props.getProperty(name));
                    }
                }
            }
        } catch (Throwable ex) {
            System.err.println("Failed to load '" + Consts.SYSTEM_PROPERTIES_FILE + "' from classpath: " + ex.toString());
        } finally {
        	if (is!=null) try {is.close();} catch (IOException e) {}
        }
    }

    static {
        loadJPiSystemProperties();
    }

    private static ClassLoader getRootClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        while (cl.getParent() != null) {
            cl = cl.getParent();
        }
        return cl;
    }

    public static byte[] getResourceBytes(String resourcePath) throws IOException {
        InputStream in = APILoader.class.getResourceAsStream(resourcePath);
        if (in == null) throw new IOException(resourcePath + " is not found");
        byte[] buf = new byte[1024];
        ByteArrayOutputStream byteCodeBuf = new ByteArrayOutputStream();
        for (int readLength; (readLength = in.read(buf)) != -1;) {
            byteCodeBuf.write(buf, 0, readLength);
        }
        in.close();
        return byteCodeBuf.toByteArray();
    }

    public static boolean isNativeLibraryLoaded() {
        return isLoaded;
    }

    private static boolean hasInjectedSoLoader() {
        try {
            Class< ? > c = Class.forName(Consts.SO_LOADER_CLASS_NAME);
            // If this native loader class is already defined, it means that another class loader already loaded the native library of dip
            return true;
        } catch (ClassNotFoundException e) {
            // do loading
            return false;
        }
    }

    /**
     * Load DipNative and its JNI native implementation using the root class
     * loader. This hack is for avoiding the JNI multi-loading issue when the
     * same JNI library is loaded by different class loaders.
     * 
     * In order to load native code in the root class loader, this method first
     * inject JPiSoLoader class into the root class loader, because
     * {@link System#load(String)} method uses the class loader of the caller
     * class when loading native libraries.
     * 
     * <pre>
     * (root class loader) -> [JPiSoLoader (load JNI code), DipNative (has native methods), DipNativeAPI, DipErrorCode]  (injected by this method)
     *    |
     *    |
     * (child class loader) -> Sees the above classes loaded by the root class loader.
     *   Then creates DipNativeAPI implementation by instantiating DipNaitive class.
     * </pre>
     * 
     * 
     * <pre>
     * (root class loader) -> [JPiSoLoader, DipNative ...]  -> native code is loaded by once in this class loader 
     *   |   \
     *   |    (child2 class loader)      
     * (child1 class loader)
     * 
     * child1 and child2 share the same DipNative code loaded by the root class loader.
     * </pre>
     * 
     * Note that Java's class loader first delegates the class lookup to its
     * parent class loader. So once JPiSoLoader is loaded by the root
     * class loader, no child class loader initialize JPiSoLoader again.
     * 
     * @return 
     */
    public static synchronized JPiNativeAPI load() {
    	try {
	        if (!hasInjectedSoLoader()) {
	            // Inject JPiSoLoader (src/main/resources/dip/JPiSoLoader.bytecode) to the root class loader  
	            Class< ? > soLoader = injectSoLoader();
	            // Load the JNI code using the injected loader
	            loadSharedLibrary(soLoader);
	            isLoaded = true;
	        }
	        return (JPiNativeAPI) Class.forName(Consts.NATIVE_API_CLASS_NAME).newInstance();
    	} catch (Exception e) {
    		throw new RuntimeException("Failed to init dip.util.guts.DipNativeAPI", e);
    		
    	}
    }

    /**
     * Inject JPiSoLoader class to the root class loader
     * @param loaderInjected 
     * @param clazz 
     * 
     */
    private static Class< ? > injectSoLoader() throws IOException, ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            ClassLoader rootClassLoader = getRootClassLoader();
            // Load a byte code 
            byte[] byteCode = null;
            byteCode=getResourceBytes(Consts.SO_LOADER_RESOURCE_BYTES_NAME);
            // In addition, we need to load the other dependent classes (e.g., DipNative and DipException) using the system class loader
// TODO
            String[] classesToPreload = new String[] { "jpi.utils.loader.JPiAPI", "jpi.utils.loader.JPiNativeAPI" };
            List<byte[]> preloadClassByteCode = new ArrayList<byte[]>(classesToPreload.length);
            for (String each : classesToPreload) {
                preloadClassByteCode.add(getResourceBytes(String.format("/%s.class", each.replaceAll("\\.", "/"))));
            }
            
            // Create JPiSoLoader class from a byte code
            Class< ? > classLoader = Class.forName("java.lang.ClassLoader");
            Method defineClass = classLoader.getDeclaredMethod("defineClass", new Class[] { String.class, byte[].class,
                    int.class, int.class, ProtectionDomain.class });

            ProtectionDomain pd = System.class.getProtectionDomain();

            // ClassLoader.defineClass is a protected method, so we have to make it accessible
            defineClass.setAccessible(true);
            try {
                // Create a new class using a ClassLoader#defineClass
                defineClass.invoke(rootClassLoader, Consts.SO_LOADER_CLASS_NAME, byteCode, 0, byteCode.length, pd);

                // And also define dependent classes in the root class loader
                for (int i = 0; i < classesToPreload.length; ++i) {
                    byte[] b = preloadClassByteCode.get(i);
                    defineClass.invoke(rootClassLoader, classesToPreload[i], b, 0, b.length, pd);
                }
                
            }
            finally {
                // Reset the accessibility to defineClass method
                defineClass.setAccessible(false);
            }

            // Load the JPiSoLoader class
            return rootClassLoader.loadClass(Consts.SO_LOADER_CLASS_NAME);
    }

    /**
     * Load dip-java's native code using load method of the
     * JPiSoLoader class injected to the root class loader.
     * @param loaderInjected 
     * 
     * @param loaderClass
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws IOException 
     * @throws NoSuchAlgorithmException 
     */
    private static void loadSharedLibrary(Class< ? > loaderClass) throws SecurityException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException, NoSuchAlgorithmException {
        if (loaderClass == null) throw new IllegalArgumentException("missing JPiSoLoader loader class");

        File nativeLib = findNativeLibrary();
        if (nativeLib != null) {
            // Load extracted or specified jpijava native library. 
            Method loadMethod = loaderClass.getDeclaredMethod("load", new Class[] { String.class });
            loadMethod.invoke(null, nativeLib.getAbsolutePath());
        }
        else {
            // Load preinstalled jpijava (in the path -Djava.library.path) 
            Method loadMethod = loaderClass.getDeclaredMethod("loadLibrary", new Class[] { String.class });
            loadMethod.invoke(null, "jpijava");
        }
    }

    /**
     * Computes the MD5 value of the input stream
     * 
     * @param input
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    static String md5sum(InputStream input) throws IOException {
        BufferedInputStream in = new BufferedInputStream(input);
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            DigestInputStream digestInputStream = new DigestInputStream(in, digest);
            for (; digestInputStream.read() >= 0;) {

            }
            ByteArrayOutputStream md5out = new ByteArrayOutputStream();
            md5out.write(digest.digest());
            return md5out.toString();
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm is not available: " + e);
        }
        finally {
            in.close();
        }
    }
    private static String md5sum(byte[] soContent) throws NoSuchAlgorithmException {
    	MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
    	return new String(digest.digest(soContent));
	}
    /**
     * Extract the specified library file to the target folder
     * @param soContent 
     * 
     * @param libFolderForCurrentOS
     * @param libraryFileName
     * @param targetFolder
     * @return
     * @throws IOException 
     * @throws NoSuchAlgorithmException 
     */
    private static File extractLibraryFile(byte[] soContent, String unmappedSoName, String mappedSoName, String targetFolder) throws IOException, NoSuchAlgorithmException {
        File extractedLibFile = new File(targetFolder, mappedSoName);
        if (extractedLibFile.exists()) {
            // test md5sum value
            String md5sum1 = md5sum(soContent);
            String md5sum2 = md5sum(Files.readAllBytes(extractedLibFile.toPath()));

            if (md5sum1.equals(md5sum2)) {
                return extractedLibFile;
            } else {
                // remove old native library file
                boolean deletionSucceeded = extractedLibFile.delete();
                if (!deletionSucceeded) {
                    throw new IOException("failed to remove existing native library file: "
                            + extractedLibFile.getAbsolutePath());
                }
            }
        }

        Files.write(extractedLibFile.toPath(), soContent, StandardOpenOption.WRITE,StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.CREATE );
        // Set executable (x) flag to enable Java to load the native library
        if (!System.getProperty("os.name").contains("Windows")) {
            try {
                Runtime.getRuntime().exec(new String[] { "chmod", "755", extractedLibFile.getAbsolutePath() }).waitFor();
            } catch (Throwable e) {}
        }
        return extractedLibFile;
    }



	static File findNativeLibrary() throws IOException, NoSuchAlgorithmException {

        boolean useSystemLib = Boolean.parseBoolean(System.getProperty(Consts.KEY_JPI_USE_SYSTEMLIB, "false"));
        if (useSystemLib)
            return null;


        // Try to load the library in dip.lib.path  */
        String jpiSoLibraryPath = System.getProperty(Consts.KEY_JPI_LIB_PATH);
        String jpiSoLibraryName = System.getProperty(Consts.KEY_JPI_LIB_NAME);

        String unmappedName=null;
        // Resolve the library file name with a suffix (e.g., .so, etc.) 
        if (jpiSoLibraryName == null) {
        	StringBuilder v=new StringBuilder("_");
        	for (byte b : getResourceBytes("/VERSION")) {
        		if (b!=0xA && b!=0xD) v.append((char)b);
        		else break;
        	}
        	String arch=getRealOsAndArch();
        	System.err.println("Arch name: "+arch);
        	unmappedName=v.toString()+"_"+arch;
        
            jpiSoLibraryName = System.mapLibraryName(unmappedName);
            System.err.println("SO name: "+jpiSoLibraryName);

        }
        if (jpiSoLibraryPath != null) {
            File nativeLib = new File(jpiSoLibraryPath, jpiSoLibraryName);
            if (nativeLib.exists())
                return nativeLib;
        }

        {
            String full="/so/" + jpiSoLibraryName;
            if (APILoader.class.getResource(full) != null) {
                byte[] soContent=getResourceBytes(full);
                // Temporary library folder. Use the value of dip.tempdir or java.io.tmpdir
                String tempFolder = new File(System.getProperty(Consts.KEY_JPI_TEMPDIR,
                        System.getProperty("java.io.tmpdir"))).getAbsolutePath();

                // Extract and load a native library inside the jar file
                return extractLibraryFile(soContent,unmappedName, jpiSoLibraryName, tempFolder);
            }
        }

        return null; // Use a pre-installed libjpijava
    }

	public static String getRealOsAndArch() {	
		StringBuilder sb=new StringBuilder();
		if (System.getProperty("os.name").contains("Windows")
			|| !output(sb,"sh", "-c", "echo -n `uname`-`arch`")) throw new RuntimeException("Unsupported OS: "+System.getProperty("os.name"));
		return sb.toString();
		
	}
	public static boolean output(StringBuilder sb, String ...args) {
        try {
        	ProcessBuilder pb=new ProcessBuilder(args);
        	pb.redirectError(Redirect.INHERIT);
        	pb.redirectOutput(Redirect.PIPE);
        	Process p=pb.start();
        	InputStream is = p.getInputStream();
        	int c=0;
        	while ( (c=is.read())!=-1) sb.append((char)c);
        	if (p.waitFor()==0) return true; 
        } catch (Throwable e) {e.printStackTrace();}
        return false;
	}
}
