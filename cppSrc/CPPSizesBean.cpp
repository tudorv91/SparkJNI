#include "CPPSizesBean.h"
	std::mutex CPPSizesBean::mtx;
CPPSizesBean::CPPSizesBean(jclass replaceMeClassName, jobject replaceMeObjectName, JNIEnv* env){

	jniJavaClassRef = replaceMeObjectName;
	jfieldID j_col1 = env->GetFieldID(replaceMeClassName, "col1", "[I");
	jobject col1_obj = env->GetObjectField(replaceMeObjectName, j_col1);
	jfieldID j_col2 = env->GetFieldID(replaceMeClassName, "col2", "[I");
	jobject col2_obj = env->GetObjectField(replaceMeObjectName, j_col2);

	mtx.lock();
	col1Arr = reinterpret_cast<jintArray>(col1_obj);
	col1_length = env->GetArrayLength(col1Arr);
	col1 = (int*)env->GetIntArrayElements(col1Arr, NULL);
	col2Arr = reinterpret_cast<jintArray>(col2_obj);
	col2_length = env->GetArrayLength(col2Arr);
	col2 = (int*)env->GetIntArrayElements(col2Arr, NULL);
	mtx.unlock();
	this->env = env;
	jniCreated = 1;
}
	int* CPPSizesBean::getcol2(){
		return col2;
	}

	int* CPPSizesBean::getcol1(){
		return col1;
	}

	int CPPSizesBean::getcol1_length(){
		return col1_length;
	}

	int CPPSizesBean::getcol2_length(){
		return col2_length;
	}

	jobject CPPSizesBean::getJavaObject(){
		return jniJavaClassRef;
	}
CPPSizesBean::CPPSizesBean(int* col1arg, int col1_lengtharg, int* col2arg, int col2_lengtharg, jclass jClass, JNIEnv* jniEnv){
	col2 = col2arg;
	col1 = col1arg;
	col1_length = col1_lengtharg;
	col2_length = col2_lengtharg;
	env = jniEnv;
	if(jClass == NULL){
		printf("Provided java class object is null..!");
		return;
	}
	jmethodID constructor = env->GetMethodID(jClass, "<init>", "([I[I)V");
	if(constructor == NULL){
		printf("Constructor object method is null");
		return;
	}
	jintArray col1Arr = env->NewIntArray(col1_lengtharg);
	env->SetIntArrayRegion(col1Arr, 0, col1_lengtharg, col1);
	jintArray col2Arr = env->NewIntArray(col2_lengtharg);
	env->SetIntArrayRegion(col2Arr, 0, col2_lengtharg, col2);
	jniJavaClassRef = env->NewObject(jClass, constructor, col1Arr, col2Arr);
}
CPPSizesBean::CPPSizesBean(){
}
CPPSizesBean::~CPPSizesBean() {
	if(jniCreated != 0){
env->ReleaseIntArrayElements(col1Arr, col1, 0);env->ReleaseIntArrayElements(col2Arr, col2, 0);	}
}
