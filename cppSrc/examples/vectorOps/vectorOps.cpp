#include "CPPVectorBean.h"
#include "org_tudelft_ewi_ceng_examples_vectorOps_VectorAddJni.h"
#include "org_tudelft_ewi_ceng_examples_vectorOps_VectorMulJni.h"

JNIEXPORT jobject JNICALL Java_org_tudelft_ewi_ceng_examples_vectorOps_VectorAddJni_reduceVectorAdd(JNIEnv *env, jobject caller, jobject v1obj, jobject v2obj){
	jclass vectorClass = env->GetObjectClass(v1obj);
	CPPVectorBean v1(vectorClass, v1obj, env);
	CPPVectorBean v2(vectorClass, v2obj, env);
	int vectorLength = v1.getdata_length();
	for(int idx = 0; idx < vectorLength; idx++)
		v1.getdata()[idx] += v2.getdata()[idx];
	return v1.getJavaObject();
}

JNIEXPORT jobject JNICALL Java_org_tudelft_ewi_ceng_examples_vectorOps_VectorMulJni_mapVectorMul(JNIEnv *env, jobject caller, jobject vectObj){
	jclass vectorClass = env->GetObjectClass(vectObj);
	CPPVectorBean v1(vectorClass, vectObj, env);
	int vectorLength = v1.getdata_length();
	for(int idx = 0; idx < vectorLength; idx++)
		v1.getdata()[idx] *= 2;
	return v1.getJavaObject();
}
