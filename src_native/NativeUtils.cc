#include "NativeUtils.h"
#include <stdlib.h>

/*
	 * index >= count)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return value[index + offset];
        rivate final char[] value;
  Signature: [C
private final int offset;
  Signature: I
private final int count;
  Signature: I
private int hash;
  Signature: I


jfieldID myFieldID;
  */

jfieldID __restrict__ stringOffsetFieldID;
jfieldID __restrict__ stringCountFieldID;
jfieldID __restrict__ stringValueFieldID;

/*
 * Class:     dip_util_guts_NativeUtils

 * Method:    initStringFieldIds
 * Signature: (Ljava/lang/Class;)V
 */
extern "C" JNIEXPORT void JNICALL Java_dip_util_guts_NativeUtils_initStringFieldIds(JNIEnv*   env,
		jclass  nuClass, jclass stringClass)
{
	//stringCountFieldID=env->GetFieldID( stringClass, "count", "I");
	//stringOffsetFieldID=env->GetFieldID( stringClass, "offset", "I");
	stringValueFieldID=env->GetFieldID( stringClass, "value", "[C");
}

jboolean isCopy=0;
/*
 * Class:     dip_util_guts_NativeUtils
 * Method:    testStringAccess
 * Signature: (Ljava/lang/String;)Ljava/lang/Object;
 */
extern "C" JNIEXPORT void JNICALL Java_dip_util_guts_NativeUtils_testStringAccess
  (JNIEnv * __restrict__ env, jclass __restrict__ nuClass, jstring __restrict__ jstr)
{

	  //jint strCount = env->GetIntField(jstr, stringCountFieldID);
	  //jint strOffset = env->GetIntField(jstr, stringOffsetFieldID);
	  //jcharArray __restrict__ arr=(jcharArray)env->GetObjectField(jstr,stringValueFieldID);
	  char* __restrict__  b=(char*)alloca(8+1);
	  char* __restrict__  res=b;
	  b[8]=0;
	  unsigned short* __restrict__ ptr=(unsigned short*)alloca(8);
	 /* env->GetCharArrayRegion(arr,0,8,ptr);
	  for (int i=0;i<8;++i) {
		  *b++=(char)*ptr++;
	  }
	  */
}

JNIEXPORT void JNICALL Java_dip_util_guts_NativeUtils_testStringAccessHack1
  (JNIEnv * env, jclass cz, jint len, jchar c1, jchar c2, jchar c3, jchar c4, jchar c5, jchar c6, jchar c7, jchar c8,
		  jchar c9, jchar c10, jchar c11, jchar c12, jchar c13, jchar c14, jchar c15, jchar c16)
{
	  char* __restrict__ b=(char*)alloca(len+1);

	  if (len>0) {
		  *b++=(char)c1;
		  if (len>1) {
			  *b++=(char)c2;
			  if (len>2) {
				  *b++=(char)c3;
				  if (len>3) {
					  *b++=(char)c4;
					  if (len>4) {
						  *b++=(char)c5;
						  if (len>5) {
							  *b++=(char)c6;
							  if (len>6) {
								  *b++=(char)c7;
								  if (len>7) {
									  *b++=(char)c8;
								  }
							  }
						  }
					  }
				  }
			  }
		  }
	  }
	//printf("str=%s\n",b-len);
}
