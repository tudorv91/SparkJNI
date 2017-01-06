#ifndef CPPSIZESBEAN
#define CPPSIZESBEAN
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include <stdint.h>
#include <iostream>
#include <mutex>
#include <memory>

class CPPSizesBean {
private:
	JNIEnv* env;
	jobject jniJavaClassRef;
	int jniCreated = 0;
	int* col1;
	jintArray col1Arr;
	int col1_length;
	int* col2;
	jintArray col2Arr;
	int col2_length;
	static std::mutex mtx;

public:
		int* getcol1();
		int getcol1_length();
		int getcol2_length();
		int* getcol2();
		jobject getJavaObject();
	CPPSizesBean(jclass replaceMeClassName, jobject replaceMeObjectName, JNIEnv* env);
	CPPSizesBean(int* col1arg, int col1_lengtharg, int* col2arg, int col2_lengtharg, jclass jClass, JNIEnv* jniEnv);
	CPPSizesBean();
	~CPPSizesBean();

};
#endif