/*--------------------------------------------------------------------------
 * Based on:
 *  Copyright 2008 Taro L. Saito
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

import java.util.HashMap;
import java.util.Map;

/**
 * Provides OS name and architecture name.
 * 
 * @author leo
 * 
 */
public class OSInfo {
    public static void main(String[] args) {
        if (args.length >= 1) {
            if ("--os".equals(args[0])) {
                System.out.print(getOSName());
                return;
            }
            else if ("--arch".equals(args[0])) {
                System.out.print(getArchName());
                return;
            }
        }

        System.out.println("Arch: "+getArchName());
        System.out.println("Arch: "+getOSName());
    }


    public static String getOSName() {
        return translateOSNameToFolderName(System.getProperty("os.name"));
    }

    public static String getArchName() {
    	String arch=System.getProperty("os.arch").replaceAll("\\W", "");
    	if (archMap.containsKey(arch))
    		arch=archMap.get(arch);
        return arch;
    }
    static Map<String,String> archMap=new HashMap<String,String>();
    static {
    	archMap.put("amd64", "x86_64");
    	archMap.put("i386", "x86");
    	archMap.put("i686", "x86");
    	archMap.put("arm", "armv6l");
    }
    

    public static String translateOSNameToFolderName(String osName) {
        if (osName.contains("Windows")) {
            return "Windows";
        }
        else if (osName.contains("Mac")) {
            return "Mac";
        }
        else if (osName.contains("Linux")) {
            return "Linux";
        }
        else {
            return osName.replaceAll("\\W", "");
        }
    }
}
