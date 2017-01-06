#include "CPPByteArrBean.h"
	std::mutex CPPByteArrBean::mtx;
CPPByteArrBean::CPPByteArrBean(jclass replaceMeClassName, jobject replaceMeObjectName, JNIEnv* env){

	jniJavaClassRef = replaceMeObjectName;
	jfieldID j_arr = env->GetFieldID(replaceMeClassName, "arr", "[B");
	jobject arr_obj = env->GetObjectField(replaceMeObjectName, j_arr);

	arrArr = reinterpret_cast<jbyteArray>(arr_obj);
	arr_length = env->GetArrayLength(arrArr);
	arr = (jbyte*)env->GetPrimitiveArrayCritical(arrArr, NULL);
	this->env = env;
	jniCreated = 1;
}
	int CPPByteArrBean::getarr_length(){
		return arr_length;
	}

	jbyte* CPPByteArrBean::getarr(){
		return arr;
	}

	jobject CPPByteArrBean::getJavaObject(){
		return jniJavaClassRef;
	}
CPPByteArrBean::CPPByteArrBean(jbyte* arrarg, int arr_lengtharg, jclass jClass, JNIEnv* jniEnv){
	arr_length = arr_lengtharg;
	arr = arrarg;
	env = jniEnv;
	if(jClass == NULL){
		printf("Provided java class object is null..!");
		return;
	}
	jmethodID constructor = env->GetMethodID(jClass, "<init>", "([B)V");
	if(constructor == NULL){
		printf("Constructor object method is null");
		return;
	}
	jbyteArray arrArr = env->NewByteArray(arr_lengtharg);
	env->SetByteArrayRegion(arrArr, 0, arr_lengtharg, arr);
	jniJavaClassRef = env->NewObject(jClass, constructor, arrArr);
}
CPPByteArrBean::CPPByteArrBean(){
	jniJavaClassRef = NULL;
}
CPPByteArrBean::~CPPByteArrBean() {
	if(jniCreated != 0){
	env->ReleasePrimitiveArrayCritical(arrArr, arr, 0);
	delete arr;
	jniCreated = 0;
}
}
