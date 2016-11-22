#include "CPPVectorBean.h"
	std::mutex CPPVectorBean::mtx;
CPPVectorBean::CPPVectorBean(jclass replaceMeClassName, jobject replaceMeObjectName, JNIEnv* env){

	jniJavaClassRef = replaceMeObjectName;
	jfieldID j_data = env->GetFieldID(replaceMeClassName, "data", "[I");
	jobject data_obj = env->GetObjectField(replaceMeObjectName, j_data);

	mtx.lock();
	dataArr = reinterpret_cast<jintArray>(data_obj);
	data_length = env->GetArrayLength(dataArr);
	data = (int*)env->GetIntArrayElements(dataArr, NULL);
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
	if(constructor == NULL){
		printf("Constructor object method is null");
		return;
	}
	jintArray dataArr = env->NewIntArray(data_lengtharg);
	env->SetIntArrayRegion(dataArr, 0, data_lengtharg, data);
	jniJavaClassRef = env->NewObject(jClass, constructor, dataArr);
}
CPPVectorBean::CPPVectorBean(){
	jniJavaClassRef = NULL;
}
CPPVectorBean::~CPPVectorBean() {
	if(jniCreated != 0){
	env->ReleaseIntArrayElements(dataArr, data, 0);
	
	jniCreated = 0;
}
}
