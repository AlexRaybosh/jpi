#include "NativeDataNode.h"
#include <stdlib.h>
#include "dip_jni_utils.h"

extern "C" JNIEXPORT jobject JNICALL Java_dip_util_guts_NativeDataNode_getNativeValue
	(JNIEnv * env, jclass cz, jlong proxy, jint num)
{
	return NULL;
}

extern "C" JNIEXPORT void JNICALL Java_dip_util_guts_NativeDataNode_setNativeValueNull
	(JNIEnv * env, jclass cz, jlong p, jint n)
{

}

extern "C" JNIEXPORT void JNICALL Java_dip_util_guts_NativeDataNode_setNativeValueInt
	(JNIEnv *, jclass, jlong, jint, jint)
{
}

extern "C" JNIEXPORT void JNICALL Java_dip_util_guts_NativeDataNode_setNativeValueString
  (JNIEnv *, jclass, jlong, jint, jbyteArray)
{
}

extern "C" JNIEXPORT jint JNICALL Java_dip_util_guts_NativeDataNode_getNativeNumberOfAttributes
  (JNIEnv *, jclass, jlong, jint)
{
}

extern "C" JNIEXPORT jboolean JNICALL Java_dip_util_guts_NativeDataNode_hasNextAttrName
  (JNIEnv *, jclass, jlong, jint, jint)
{
}

extern "C" JNIEXPORT jbyteArray JNICALL Java_dip_util_guts_NativeDataNode_nextAttrName
  (JNIEnv *, jclass, jlong, jint, jint)
{
}

extern "C" JNIEXPORT void JNICALL Java_dip_util_guts_NativeDataNode_removeAttrName
  (JNIEnv *, jclass, jlong, jint, jint)
{
}

extern "C" JNIEXPORT jint JNICALL Java_dip_util_guts_NativeDataNode_getNodesNum
  (JNIEnv *, jclass, jlong, jint, jbyteArray)
{
}

