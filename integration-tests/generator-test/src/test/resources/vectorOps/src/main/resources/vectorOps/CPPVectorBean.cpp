#include "CPPVectorBean.h"
	std::mutex CPPVectorBean::mtx;
CPPVectorBean::CPPVectorBean(jclass replaceMeClassName, jobject replaceMeObjectName, JNIEnv* env){

	jniJavaClassRef = replaceMeObjectName;
	jfieldID j_data = env->GetFieldID(replaceMeClassName, "data", "[I");
		if (env->ExceptionCheck()) {
		return;
	}
jobject data_obj = env->GetObjectField(replaceMeObjectName, j_data);
	if (env->ExceptionCheck()) {
		return;
	}

	mtx.lock();
	dataArr = reinterpret_cast<jintArray>(data_obj);
	data_length = env->GetArrayLength(dataArr);
	if (env->ExceptionCheck()) {
		return;
	}
	data = (int*)env->GetIntArrayElements(dataArr, NULL);
	if (env->ExceptionCheck()) {
		return;
	}
	mtx.unlock();
	this->env = env;
	jniCreated = 1;
}
	int* CPPVectorBean::getdata(){
		return data;
	}

	int CPPVectorBean::getdata_length(){
		return data_length;
	}

	jobject CPPVectorBean::getJavaObject(){
		return jniJavaClassRef;
	}
CPPVectorBean::CPPVectorBean(int* dataarg, int data_lengtharg, jclass jClass, JNIEnv* jniEnv){
	data = dataarg;
	data_length = data_lengtharg;
	env = jniEnv;
	if(jClass == NULL){
		printf("Provided java class object is null..!");
		return;
	}
	jmethodID constructor = env->GetMethodID(jClass, "<init>", "([I)V");
	if (env->ExceptionCheck()) {
		return;
	}
	if(constructor == NULL){
		printf("Constructor object method is null");
		return;
	}
	jintArray dataArr = env->NewIntArray(data_lengtharg);
	if (env->ExceptionCheck()) {
		return;
	}
	env->SetIntArrayRegion(dataArr, 0, data_lengtharg, data);
	jniJavaClassRef = env->NewObject(jClass, constructor, dataArr);
	if (env->ExceptionCheck()) {
		return;
	}
}
CPPVectorBean::CPPVectorBean(){
	jniJavaClassRef = NULL;
}
CPPVectorBean::~CPPVectorBean() {
	if(jniCreated != 0){
	env->ReleaseIntArrayElements(dataArr, data, 0);
	if (env->ExceptionCheck()) {
		return;
	}
	
	jniCreated = 0;
}
}
