#include "CPPVectorBean.h"
#include "org_tudelft_ceng_examples_vectorOps_VectorMulJni.h"
#include "org_tudelft_ceng_examples_vectorOps_VectorAddJni.h"
#include "CPPVectorBean.h"

JNIEXPORT jobject JNICALL Java_org_tudelft_ceng_examples_vectorOps_VectorMulJni_mapVectorMul(JNIEnv *jniEnv, jobject callerObject, jobject cppvectorbean_jObject0){
	jclass cppvectorbean_jClass = jniEnv->GetObjectClass(cppvectorbean_jObject0);
	CPPVectorBean cppvectorbean0(cppvectorbean_jClass, cppvectorbean_jObject0, jniEnv);
	// add RETURN OBJECT.getJavaObject()
}

JNIEXPORT jobject JNICALL Java_org_tudelft_ceng_examples_vectorOps_VectorAddJni_reduceVectorAdd(JNIEnv *jniEnv, jobject callerObject, jobject cppvectorbean_jObject0, jobject cppvectorbean_jObject1){
	CPPVectorBean cppvectorbean0(cppvectorbean_jClass, cppvectorbean_jObject0, jniEnv);
	CPPVectorBean cppvectorbean1(cppvectorbean_jClass, cppvectorbean_jObject1, jniEnv);
	// add RETURN OBJECT.getJavaObject()
}

