#ifndef CPPPAIRHMMBEAN
#define CPPPAIRHMMBEAN
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include <stdint.h>
#include <iostream>
#include <mutex>
#include <memory>
#include "CPPWorkloadPairHmmBean.h"
#include "CPPByteArrBean.h"

class CPPPairHmmBean {
private:
	JNIEnv* env;
	jobject jniJavaClassRef;
	int jniCreated = 0;
	double memcopyTime;
	CPPWorkloadPairHmmBean* workload;
	CPPByteArrBean* byteArrBean;
	static std::mutex mtx;

public:
		CPPByteArrBean* getbyteArrBean();
		CPPWorkloadPairHmmBean* getworkload();
		double getmemcopyTime();
		jobject getJavaObject();
	CPPPairHmmBean(jclass replaceMeClassName, jobject replaceMeObjectName, JNIEnv* env);
	CPPPairHmmBean(double memcopyTimearg, CPPWorkloadPairHmmBean* workloadarg, CPPByteArrBean* byteArrBeanarg, jclass jClass, JNIEnv* jniEnv);
	CPPPairHmmBean();
	~CPPPairHmmBean();

};
#endif