#ifndef VECTOROPSMAINTEST_KERNELWRAPPER
#define VECTOROPSMAINTEST_KERNELWRAPPER
#include "/home/tudor/dev/SparkJNI/resources/VectorOpsMainTest_TEST/org_heterojni_examples_vectorOps_VectorAddJni.h"
#include "/home/tudor/dev/SparkJNI/resources/VectorOpsMainTest_TEST/org_heterojni_examples_vectorOps_VectorMulJni.h"
#include "CPPVectorBean.h"

JNIEXPORT jobject JNICALL Java_org_heterojni_examples_vectorOps_VectorAddJni_reduceVectorAdd(JNIEnv *jniEnv, jobject callerObject, jobject cppvectorbean_jObject0, jobject cppvectorbean_jObject1){
	jclass cppvectorbean_jClass = jniEnv->GetObjectClass(cppvectorbean_jObject0);
	CPPVectorBean cppvectorbean0(cppvectorbean_jClass, cppvectorbean_jObject0, jniEnv);
	CPPVectorBean cppvectorbean1(cppvectorbean_jClass, cppvectorbean_jObject1, jniEnv);
	CPPVectorBean returnedCppvectorbean0;
	return returnedCppvectorbean0.getJavaObject();
}

JNIEXPORT jobject JNICALL Java_org_heterojni_examples_vectorOps_VectorMulJni_mapVectorMul(JNIEnv *jniEnv, jobject callerObject, jobject cppvectorbean_jObject0){
	jclass cppvectorbean_jClass = jniEnv->GetObjectClass(cppvectorbean_jObject0);
	CPPVectorBean cppvectorbean0(cppvectorbean_jClass, cppvectorbean_jObject0, jniEnv);
	CPPVectorBean returnedCppvectorbean0;
	return returnedCppvectorbean0.getJavaObject();
}


#endif