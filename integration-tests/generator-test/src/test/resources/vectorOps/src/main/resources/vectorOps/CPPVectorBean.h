#ifndef CPPVECTORBEAN
#define CPPVECTORBEAN
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include <stdint.h>
#include <iostream>
#include <mutex>
#include <memory>

class CPPVectorBean {
private:
	JNIEnv* env;
	jobject jniJavaClassRef;
	int jniCreated = 0;
	int* data;
	jintArray dataArr;
	int data_length;
	static std::mutex mtx;

public:
		int* getdata();
		int getdata_length();
		jobject getJavaObject();
	CPPVectorBean(jclass replaceMeClassName, jobject replaceMeObjectName, JNIEnv* env);
	CPPVectorBean(int* dataarg, int data_lengtharg, jclass jClass, JNIEnv* jniEnv);
	CPPVectorBean();
	~CPPVectorBean();

};
#endif