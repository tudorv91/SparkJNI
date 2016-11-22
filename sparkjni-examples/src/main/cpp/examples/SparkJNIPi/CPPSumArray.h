#ifndef CPPSUMARRAY
#define CPPSUMARRAY
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <iostream>
#include <stdint.h>
#include <jni.h>
#include <mutex>


class CPPSumArray {
private:
	JNIEnv* env;
	jobject jniJavaClassRef;
	int jniCreated = 0;
	int* sum;
	jintArray sumArr;
	int sum_length;
	static std::mutex mtx;

public:
		int* getsum();
		int getsum_length();
		jobject getJavaObject();
	CPPSumArray(jclass replaceMeClassName, jobject replaceMeObjectName, JNIEnv* env);
	CPPSumArray(int* sumarg, int sum_lengtharg, jclass jClass, JNIEnv* jniEnv);
	CPPSumArray();
	~CPPSumArray();

};
#endif