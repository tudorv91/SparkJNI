#include "CPPSumArray.h"
	std::mutex CPPSumArray::mtx;
CPPSumArray::CPPSumArray(jclass replaceMeClassName, jobject replaceMeObjectName, JNIEnv* env){

	jniJavaClassRef = replaceMeObjectName;
	jfieldID j_sum = env->GetFieldID(replaceMeClassName, "sum", "[I");
	jobject sum_obj = env->GetObjectField(replaceMeObjectName, j_sum);

	mtx.lock();
	sumArr = reinterpret_cast<jintArray>(sum_obj);
	sum_length = env->GetArrayLength(sumArr);
	sum = (int*)env->GetIntArrayElements(sumArr, NULL);
	mtx.unlock();
	this->env = env;
	jniCreated = 1;
}
	int* CPPSumArray::getsum(){
		return sum;
	}

	int CPPSumArray::getsum_length(){
		return sum_length;
	}

	jobject CPPSumArray::getJavaObject(){
		return jniJavaClassRef;
	}
CPPSumArray::CPPSumArray(int* sumarg, int sum_lengtharg, jclass jClass, JNIEnv* jniEnv){
	sum = sumarg;
	sum_length = sum_lengtharg;
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
	jintArray sumArr = env->NewIntArray(sum_lengtharg);
	env->SetIntArrayRegion(sumArr, 0, sum_lengtharg, sum);
	jniJavaClassRef = env->NewObject(jClass, constructor, sumArr);
}
CPPSumArray::CPPSumArray(){
	jniJavaClassRef = NULL;
}
CPPSumArray::~CPPSumArray() {
	if(jniCreated != 0){
env->ReleaseIntArrayElements(sumArr, sum, 0);	}
}
