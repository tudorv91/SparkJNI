#ifndef CPPDOUBLEARRAY
#define CPPDOUBLEARRAY
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include <stdint.h>
#include <iostream>
#include <mutex>
#include <memory>

class CPPDoubleArray {
private:
	JNIEnv* env;
	jobject jniJavaClassRef;
	int jniCreated = 0;
	double* dblArr;
	jdoubleArray dblArrArr;
	int dblArr_length;
	static std::mutex mtx;

public:
		double* getdblArr();
		int getdblArr_length();
		jobject getJavaObject();
	CPPDoubleArray(jclass replaceMeClassName, jobject replaceMeObjectName, JNIEnv* env);
	CPPDoubleArray(double* dblArrarg, int dblArr_lengtharg, jclass jClass, JNIEnv* jniEnv);
	CPPDoubleArray();
	~CPPDoubleArray();

};
#endif