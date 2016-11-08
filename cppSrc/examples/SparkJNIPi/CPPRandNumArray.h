#ifndef CPPRANDNUMARRAY
#define CPPRANDNUMARRAY
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <iostream>
#include <stdint.h>
#include <jni.h>
#include <mutex>


class CPPRandNumArray {
private:
	JNIEnv* env;
	jobject jniJavaClassRef;
	int jniCreated = 0;
	float* randNumArray;
	jfloatArray randNumArrayArr;
	int randNumArray_length;
	static std::mutex mtx;

public:
		int getrandNumArray_length();
		float* getrandNumArray();
		jobject getJavaObject();
	CPPRandNumArray(jclass replaceMeClassName, jobject replaceMeObjectName, JNIEnv* env);
	CPPRandNumArray(float* randNumArrayarg, int randNumArray_lengtharg, jclass jClass, JNIEnv* jniEnv);
	CPPRandNumArray();
	~CPPRandNumArray();

};
#endif