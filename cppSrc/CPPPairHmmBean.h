#ifndef CPPPAIRHMMBEAN
#define CPPPAIRHMMBEAN
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <iostream>
#include <stdint.h>
#include <jni.h>
#include <mutex>

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
		double getmemcopyTime();
		CPPByteArrBean* getbyteArrBean();
		CPPWorkloadPairHmmBean* getworkload();
		jobject getJavaObject();
	CPPPairHmmBean(jclass replaceMeClassName, jobject replaceMeObjectName, JNIEnv* env);
	CPPPairHmmBean(double memcopyTimearg, CPPWorkloadPairHmmBean* workloadarg, CPPByteArrBean* byteArrBeanarg, jclass jClass, JNIEnv* jniEnv);
	CPPPairHmmBean();
	~CPPPairHmmBean();

};
#endif