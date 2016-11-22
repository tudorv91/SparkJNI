#ifndef VECTOROPS_KERNELWRAPPER
#define VECTOROPS_KERNELWRAPPER
#include "/home/tudor/Development/SparkJNI/sparkjni-examples/src/main/cpp/examples/vectorOps/vectorOps_VectorAddJni.h"
#include "/home/tudor/Development/SparkJNI/sparkjni-examples/src/main/cpp/examples/vectorOps/vectorOps_VectorMulJni.h"
#include "CPPVectorBean.h"

std::shared_ptr<CPPVectorBean> reduceVectorAdd(std::shared_ptr<CPPVectorBean>& cppvectorbean0, std::shared_ptr<CPPVectorBean>& cppvectorbean1,  jclass cppvectorbean_jClass, JNIEnv* jniEnv);
std::shared_ptr<CPPVectorBean> mapVectorMul(std::shared_ptr<CPPVectorBean>& cppvectorbean0,  jclass cppvectorbean_jClass, JNIEnv* jniEnv);
JNIEXPORT jobject JNICALL Java_vectorOps_VectorAddJni_reduceVectorAdd(JNIEnv *jniEnv, jobject callerObject, jobject cppvectorbean_jObject0, jobject cppvectorbean_jObject1){
	jclass cppvectorbean_jClass = jniEnv->GetObjectClass(cppvectorbean_jObject0);
	std::shared_ptr<CPPVectorBean> cppvectorbean0 = std::make_shared<CPPVectorBean>(cppvectorbean_jClass, cppvectorbean_jObject0, jniEnv);
	std::shared_ptr<CPPVectorBean> cppvectorbean1 = std::make_shared<CPPVectorBean>(cppvectorbean_jClass, cppvectorbean_jObject1, jniEnv);
	std::shared_ptr<CPPVectorBean> CPPVectorBean_ret = reduceVectorAdd(cppvectorbean0, cppvectorbean1, cppvectorbean_jClass, jniEnv);
	jobject CPPVectorBean_retJavaObj = CPPVectorBean_ret->getJavaObject();
	return CPPVectorBean_retJavaObj;
}JNIEXPORT jobject JNICALL Java_vectorOps_VectorMulJni_mapVectorMul(JNIEnv *jniEnv, jobject callerObject, jobject cppvectorbean_jObject0){
	jclass cppvectorbean_jClass = jniEnv->GetObjectClass(cppvectorbean_jObject0);
	std::shared_ptr<CPPVectorBean> cppvectorbean0 = std::make_shared<CPPVectorBean>(cppvectorbean_jClass, cppvectorbean_jObject0, jniEnv);
	std::shared_ptr<CPPVectorBean> CPPVectorBean_ret = mapVectorMul(cppvectorbean0, cppvectorbean_jClass, jniEnv);
	jobject CPPVectorBean_retJavaObj = CPPVectorBean_ret->getJavaObject();
	return CPPVectorBean_retJavaObj;
}
#endif