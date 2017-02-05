#include "CPPDoubleArray.h"
	std::mutex CPPDoubleArray::mtx;
CPPDoubleArray::CPPDoubleArray(jclass replaceMeClassName, jobject replaceMeObjectName, JNIEnv* env){

	jniJavaClassRef = replaceMeObjectName;
	jfieldID j_dblArr = env->GetFieldID(replaceMeClassName, "dblArr", "[D");
		if (env->ExceptionCheck()) {
		return;
	}
jobject dblArr_obj = env->GetObjectField(replaceMeObjectName, j_dblArr);
	if (env->ExceptionCheck()) {
		return;
	}

	mtx.lock();
	dblArrArr = reinterpret_cast<jdoubleArray>(dblArr_obj);
	dblArr_length = env->GetArrayLength(dblArrArr);
	if (env->ExceptionCheck()) {
		return;
	}
	dblArr = (double*)env->GetPrimitiveArrayCritical(dblArrArr, NULL);
	if (env->ExceptionCheck()) {
		return;
	}
	mtx.unlock();
	this->env = env;
	jniCreated = 1;
}
	double* CPPDoubleArray::getdblArr(){
		return dblArr;
	}

	int CPPDoubleArray::getdblArr_length(){
		return dblArr_length;
	}

	jobject CPPDoubleArray::getJavaObject(){
		return jniJavaClassRef;
	}
CPPDoubleArray::CPPDoubleArray(double* dblArrarg, int dblArr_lengtharg, jclass jClass, JNIEnv* jniEnv){
	dblArr = dblArrarg;
	dblArr_length = dblArr_lengtharg;
	env = jniEnv;
	if(jClass == NULL){
		printf("Provided java class object is null..!");
		return;
	}
	jmethodID constructor = env->GetMethodID(jClass, "<init>", "([D)V");
	if (env->ExceptionCheck()) {
		return;
	}
	if(constructor == NULL){
		printf("Constructor object method is null");
		return;
	}
	jdoubleArray dblArrArr = env->NewDoubleArray(dblArr_lengtharg);
	if (env->ExceptionCheck()) {
		return;
	}
	env->SetDoubleArrayRegion(dblArrArr, 0, dblArr_lengtharg, dblArr);
	jniJavaClassRef = env->NewObject(jClass, constructor, dblArrArr);
	if (env->ExceptionCheck()) {
		return;
	}
}
CPPDoubleArray::CPPDoubleArray(){
	jniJavaClassRef = NULL;
}
CPPDoubleArray::~CPPDoubleArray() {
	if(jniCreated != 0){
	env->ReleasePrimitiveArrayCritical(dblArrArr, dblArr, 0);
	if (env->ExceptionCheck()) {
		return;
	}
	
	jniCreated = 0;
}
}
