#ifndef NATIVEFUNCTIONWRAPPERTEST_KERNELWRAPPER
#define NATIVEFUNCTIONWRAPPERTEST_KERNELWRAPPER
#include "/home/tudor/Desktop/SparkJNI/SparkJNI/resources/NativeFunctionWrapperTest_TEST/org_heterojni_examples_vectorOps_VectorMulJni.h"
#include "/home/tudor/Desktop/SparkJNI/SparkJNI/resources/NativeFunctionWrapperTest_TEST/org_heterojni_examples_vectorOps_VectorAddJni.h"
#include "CPPVectorBean.h"

	CPPVectorBean mapVectorMul(CPPVectorBean cppvectorbean0,  jclass cppvectorbean_jClass, JNIEnv* jniEnv);
	CPPVectorBean reduceVectorAdd(CPPVectorBean cppvectorbean0, CPPVectorBean cppvectorbean1,  jclass cppvectorbean_jClass, JNIEnv* jniEnv);
JNIEXPORT jobject JNICALL Java_org_heterojni_examples_vectorOps_VectorMulJni_mapVectorMul(JNIEnv *jniEnv, jobject callerObject, jobject cppvectorbean_jObject0){
	jclass cppvectorbean_jClass = jniEnv->GetObjectClass(cppvectorbean_jObject0);
	CPPVectorBean cppvectorbean0(cppvectorbean_jClass, cppvectorbean_jObject0, jniEnv);
	CPPVectorBean returnedCppvectorbean0;
	return returnedCppvectorbean0.getJavaObject();
}

JNIEXPORT jobject JNICALL Java_org_heterojni_examples_vectorOps_VectorAddJni_reduceVectorAdd(JNIEnv *jniEnv, jobject callerObject, jobject cppvectorbean_jObject0, jobject cppvectorbean_jObject1){
	jclass cppvectorbean_jClass = jniEnv->GetObjectClass(cppvectorbean_jObject0);
	CPPVectorBean cppvectorbean0(cppvectorbean_jClass, cppvectorbean_jObject0, jniEnv);
	CPPVectorBean cppvectorbean1(cppvectorbean_jClass, cppvectorbean_jObject1, jniEnv);
	CPPVectorBean returnedCppvectorbean0;
	return returnedCppvectorbean0.getJavaObject();
}


#endif