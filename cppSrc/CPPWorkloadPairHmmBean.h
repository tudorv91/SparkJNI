#ifndef CPPWORKLOADPAIRHMMBEAN
#define CPPWORKLOADPAIRHMMBEAN
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <iostream>
#include <stdint.h>
#include <jni.h>
#include <mutex>


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
		int* getbbytes();
		int* getread();
		int* getby();
		int getbx_length();
		int gethapl_length();
		int getbbytes_length();
		int* getbx();
		int getread_length();
		int getbatches();
		int* gethapl();
		int getpairs();
		int getbytes();
		int getby_length();
		jobject getJavaObject();
	CPPWorkloadPairHmmBean(jclass replaceMeClassName, jobject replaceMeObjectName, JNIEnv* env);
	CPPWorkloadPairHmmBean(int pairsarg, int batchesarg, int* haplarg, int hapl_lengtharg, int* readarg, int read_lengtharg, int* bxarg, int bx_lengtharg, int* byarg, int by_lengtharg, int* bbytesarg, int bbytes_lengtharg, int bytesarg, jclass jClass, JNIEnv* jniEnv);
	CPPWorkloadPairHmmBean();
	~CPPWorkloadPairHmmBean();

};
#endif