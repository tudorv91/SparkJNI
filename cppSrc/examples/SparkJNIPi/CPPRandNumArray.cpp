#include "CPPRandNumArray.h"
std::mutex CPPRandNumArray::mtx;
CPPRandNumArray::CPPRandNumArray(jclass replaceMeClassName,
		jobject replaceMeObjectName, JNIEnv* env) {

	jniJavaClassRef = replaceMeObjectName;
	jfieldID j_randNumArray = env->GetFieldID(replaceMeClassName,
			"randNumArray", "[F");
	jobject randNumArray_obj = env->GetObjectField(replaceMeObjectName,
			j_randNumArray);

//	mtx.lock();
	randNumArrayArr = reinterpret_cast<jfloatArray>(randNumArray_obj);
	randNumArray_length = env->GetArrayLength(randNumArrayArr);
	randNumArray = (float*) env->GetPrimitiveArrayCritical(randNumArrayArr, NULL);
//	mtx.unlock();
	this->env = env;
	jniCreated = 1;
}
int CPPRandNumArray::getrandNumArray_length() {
	return randNumArray_length;
}

float* CPPRandNumArray::getrandNumArray() {
	return randNumArray;
}

jobject CPPRandNumArray::getJavaObject() {
	return jniJavaClassRef;
}
CPPRandNumArray::CPPRandNumArray(float* randNumArrayarg,
		int randNumArray_lengtharg, jclass jClass, JNIEnv* jniEnv) {
	randNumArray_length = randNumArray_lengtharg;
	randNumArray = randNumArrayarg;
	env = jniEnv;
	if (jClass == NULL) {
		printf("Provided java class object is null..!");
		return;
	}
	jmethodID constructor = env->GetMethodID(jClass, "<init>", "([F)V");
	if (constructor == NULL) {
		printf("Constructor object method is null");
		return;
	}
	jfloatArray randNumArrayArr = env->NewFloatArray(randNumArray_lengtharg);
	env->SetFloatArrayRegion(randNumArrayArr, 0, randNumArray_lengtharg,
			randNumArray);
	jniJavaClassRef = env->NewObject(jClass, constructor, randNumArrayArr);
}
CPPRandNumArray::CPPRandNumArray() {
	jniJavaClassRef = NULL;
}
CPPRandNumArray::~CPPRandNumArray() {
	if (jniCreated != 0) {
//		env->ReleaseFloatArrayElements(randNumArrayArr, randNumArray, 0);
		env->ReleasePrimitiveArrayCritical(randNumArrayArr, randNumArray, 0);
	}
}
