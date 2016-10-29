#ifndef SPARKJNIPI_KERNELWRAPPER
#define SPARKJNIPI_KERNELWRAPPER
#include "/home/tudor/dev/SparkJNI/cppSrc/examples/SparkJNIPi/org_heterojni_examples_sparkJNIPi_RandOpenclPiMap.h"
#include "/home/tudor/dev/SparkJNI/cppSrc/examples/SparkJNIPi/SparkJNIPi.h"
#include "CPPRandNumArray.h"
#include "CPPSumArray.h"

const char* openclKernel = "__kernel void run(__global float *randNumArray, __global int *sum)"
		"{\n" \
"	int gid = get_global_id(0);" \
"	int baseIndex = gid * 2;" \
"	float x = (randNumArray[baseIndex] * 2.0f) - 1.0f;" \
"	float y = (randNumArray[(baseIndex + 1)] * 2.0f) - 1.0f;" \
"	sum[gid]  = (((x * x) + (y * y))<1.0f)?1:0;" \
"	return;\n" \
"}";

CPPSumArray* randNativePiMap(CPPRandNumArray& cppRandNumArray, JNIEnv* env);
CPPSumArray* randOpenclPiMap(CPPRandNumArray& cppRandNumArray, JNIEnv* env);

JNIEXPORT jobject JNICALL Java_org_heterojni_examples_sparkJNIPi_RandOpenclPiMap_randToSum(JNIEnv *jniEnv, jobject callerObject, jobject cpprandnumarray_jObject0){
	jclass cpprandnumarray_jClass = jniEnv->GetObjectClass(cpprandnumarray_jObject0);
	CPPRandNumArray cpprandnumarray0(cpprandnumarray_jClass, cpprandnumarray_jObject0, jniEnv);
	CPPSumArray* sumArray = randOpenclPiMap(cpprandnumarray0, jniEnv);
	jobject returnedJObj = sumArray->getJavaObject();
	delete sumArray;
	return returnedJObj;
}
#endif
