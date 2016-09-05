#ifndef CPPBYTEARRBEAN
#define CPPBYTEARRBEAN
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <iostream>
#include <stdint.h>
#include <jni.h>
#include <mutex>


class CPPByteArrBean {
private:
	JNIEnv* env;
	jobject jniJavaClassRef;
	int jniCreated = 0;
	jbyte* arr;
	jbyteArray arrArr;
	int arr_length;
	static std::mutex mtx;

public:
		int getarr_length();
		jbyte* getarr();
		jobject getJavaObject();
	CPPByteArrBean(jclass replaceMeClassName, jobject replaceMeObjectName, JNIEnv* env);
	CPPByteArrBean(jbyte* arrarg, int arr_lengtharg, jclass jClass, JNIEnv* jniEnv);
	CPPByteArrBean();
	~CPPByteArrBean();

};
#endif