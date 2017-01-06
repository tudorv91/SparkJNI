#ifndef CPPWORKLOADPAIRHMMBEAN
#define CPPWORKLOADPAIRHMMBEAN
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <jni.h>
#include <stdint.h>
#include <iostream>
#include <mutex>
#include <memory>

class CPPWorkloadPairHmmBean {
private:
	JNIEnv* env;
	jobject jniJavaClassRef;
	int jniCreated = 0;
	int pairs;
	int batches;
	int* hapl;
	jintArray haplArr;
	int hapl_length;
	int* read;
	jintArray readArr;
	int read_length;
	int* bx;
	jintArray bxArr;
	int bx_length;
	int* by;
	jintArray byArr;
	int by_length;
	int* bbytes;
	jintArray bbytesArr;
	int bbytes_length;
	int bytes;
	static std::mutex mtx;

public:
		int getbytes();
		int getpairs();
		int* getbbytes();
		int* gethapl();
		int getbbytes_length();
		int getby_length();
		int* getby();
		int getbatches();
		int getread_length();
		int* getread();
		int* getbx();
		int getbx_length();
		int gethapl_length();
		jobject getJavaObject();
	CPPWorkloadPairHmmBean(jclass replaceMeClassName, jobject replaceMeObjectName, JNIEnv* env);
	CPPWorkloadPairHmmBean(int pairsarg, int batchesarg, int* haplarg, int hapl_lengtharg, int* readarg, int read_lengtharg, int* bxarg, int bx_lengtharg, int* byarg, int by_lengtharg, int* bbytesarg, int bbytes_lengtharg, int bytesarg, jclass jClass, JNIEnv* jniEnv);
	CPPWorkloadPairHmmBean();
	~CPPWorkloadPairHmmBean();

};
#endif