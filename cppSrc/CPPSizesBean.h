#ifndef CPPSIZESBEAN
#define CPPSIZESBEAN
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <iostream>
#include <stdint.h>
#include <jni.h>
#include <mutex>


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
		int* getcol2();
		int* getcol1();
		int getcol1_length();
		int getcol2_length();
		jobject getJavaObject();
	CPPSizesBean(jclass replaceMeClassName, jobject replaceMeObjectName, JNIEnv* env);
	CPPSizesBean(int* col1arg, int col1_lengtharg, int* col2arg, int col2_lengtharg, jclass jClass, JNIEnv* jniEnv);
	CPPSizesBean();
	~CPPSizesBean();

};
#endif