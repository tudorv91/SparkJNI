#include "CPPDoubleArray.h"
	std::mutex CPPDoubleArray::mtx;
CPPDoubleArray::CPPDoubleArray(jclass replaceMeClassName, jobject replaceMeObjectName, JNIEnv* env){

	jniJavaClassRef = replaceMeObjectName;
	jfieldID j_dblArr = env->GetFieldID(replaceMeClassName, "dblArr", "[D");
	jobject dblArr_obj = env->GetObjectField(replaceMeObjectName, j_dblArr);

	mtx.lock();
	dblArrArr = reinterpret_cast<jdoubleArray>(dblArr_obj);
	dblArr_length = env->GetArrayLength(dblArrArr);
	dblArr = (double*)env->GetPrimitiveArrayCritical(dblArrArr, NULL);
	mtx.unlock();
	this->env = env;
	jniCreated = 1;
}
	int CPPDoubleArray::getdblArr_length(){
		return dblArr_length;
	}

	double* CPPDoubleArray::getdblArr(){
		return dblArr;
	}

	jobject CPPDoubleArray::getJavaObject(){
		return jniJavaClassRef;
	}
CPPDoubleArray::CPPDoubleArray(double* dblArrarg, int dblArr_lengtharg, jclass jClass, JNIEnv* jniEnv){
	dblArr_length = dblArr_lengtharg;
	dblArr = dblArrarg;
	env = jniEnv;
	if(jClass == NULL){
		printf("Provided java class object is null..!");
		return;
	}
	jmethodID constructor = env->GetMethodID(jClass, "<init>", "([D)V");
	if(constructor == NULL){
		printf("Constructor object method is null");
		return;
	}
	jdoubleArray dblArrArr = env->NewDoubleArray(dblArr_lengtharg);
	env->SetDoubleArrayRegion(dblArrArr, 0, dblArr_lengtharg, dblArr);
	jniJavaClassRef = env->NewObject(jClass, constructor, dblArrArr);
}
CPPDoubleArray::CPPDoubleArray(){
	jniJavaClassRef = NULL;
}
CPPDoubleArray::~CPPDoubleArray() {
	if(jniCreated != 0){
	env->ReleasePrimitiveArrayCritical(dblArrArr, dblArr, 0);
	
	jniCreated = 0;
}
}
