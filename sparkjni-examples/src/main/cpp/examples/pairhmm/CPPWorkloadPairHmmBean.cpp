#include "CPPWorkloadPairHmmBean.h"
	std::mutex CPPWorkloadPairHmmBean::mtx;
CPPWorkloadPairHmmBean::CPPWorkloadPairHmmBean(jclass replaceMeClassName, jobject replaceMeObjectName, JNIEnv* env){

	jniJavaClassRef = replaceMeObjectName;
	jfieldID j_pairs = env->GetFieldID(replaceMeClassName, "pairs", "I");
	pairs = env->GetIntField(replaceMeObjectName, j_pairs);
	jfieldID j_batches = env->GetFieldID(replaceMeClassName, "batches", "I");
	batches = env->GetIntField(replaceMeObjectName, j_batches);
	jfieldID j_hapl = env->GetFieldID(replaceMeClassName, "hapl", "[I");
	jobject hapl_obj = env->GetObjectField(replaceMeObjectName, j_hapl);
	jfieldID j_read = env->GetFieldID(replaceMeClassName, "read", "[I");
	jobject read_obj = env->GetObjectField(replaceMeObjectName, j_read);
	jfieldID j_bx = env->GetFieldID(replaceMeClassName, "bx", "[I");
	jobject bx_obj = env->GetObjectField(replaceMeObjectName, j_bx);
	jfieldID j_by = env->GetFieldID(replaceMeClassName, "by", "[I");
	jobject by_obj = env->GetObjectField(replaceMeObjectName, j_by);
	jfieldID j_bbytes = env->GetFieldID(replaceMeClassName, "bbytes", "[I");
	jobject bbytes_obj = env->GetObjectField(replaceMeObjectName, j_bbytes);
	jfieldID j_bytes = env->GetFieldID(replaceMeClassName, "bytes", "I");
	bytes = env->GetIntField(replaceMeObjectName, j_bytes);

	haplArr = reinterpret_cast<jintArray>(hapl_obj);
	hapl_length = env->GetArrayLength(haplArr);
	hapl = (int*)env->GetPrimitiveArrayCritical(haplArr, NULL);
	readArr = reinterpret_cast<jintArray>(read_obj);
	read_length = env->GetArrayLength(readArr);
	read = (int*)env->GetPrimitiveArrayCritical(readArr, NULL);
	bxArr = reinterpret_cast<jintArray>(bx_obj);
	bx_length = env->GetArrayLength(bxArr);
	bx = (int*)env->GetPrimitiveArrayCritical(bxArr, NULL);
	byArr = reinterpret_cast<jintArray>(by_obj);
	by_length = env->GetArrayLength(byArr);
	by = (int*)env->GetPrimitiveArrayCritical(byArr, NULL);
	bbytesArr = reinterpret_cast<jintArray>(bbytes_obj);
	bbytes_length = env->GetArrayLength(bbytesArr);
	bbytes = (int*)env->GetPrimitiveArrayCritical(bbytesArr, NULL);
	this->env = env;
	jniCreated = 1;
}
	int CPPWorkloadPairHmmBean::getbytes(){
		return bytes;
	}

	int CPPWorkloadPairHmmBean::getpairs(){
		return pairs;
	}

	int* CPPWorkloadPairHmmBean::getbbytes(){
		return bbytes;
	}

	int* CPPWorkloadPairHmmBean::gethapl(){
		return hapl;
	}

	int CPPWorkloadPairHmmBean::getbbytes_length(){
		return bbytes_length;
	}

	int CPPWorkloadPairHmmBean::getby_length(){
		return by_length;
	}

	int* CPPWorkloadPairHmmBean::getby(){
		return by;
	}

	int CPPWorkloadPairHmmBean::getbatches(){
		return batches;
	}

	int CPPWorkloadPairHmmBean::getread_length(){
		return read_length;
	}

	int* CPPWorkloadPairHmmBean::getread(){
		return read;
	}

	int* CPPWorkloadPairHmmBean::getbx(){
		return bx;
	}

	int CPPWorkloadPairHmmBean::getbx_length(){
		return bx_length;
	}

	int CPPWorkloadPairHmmBean::gethapl_length(){
		return hapl_length;
	}

	jobject CPPWorkloadPairHmmBean::getJavaObject(){
		return jniJavaClassRef;
	}
CPPWorkloadPairHmmBean::CPPWorkloadPairHmmBean(int pairsarg, int batchesarg, int* haplarg, int hapl_lengtharg, int* readarg, int read_lengtharg, int* bxarg, int bx_lengtharg, int* byarg, int by_lengtharg, int* bbytesarg, int bbytes_lengtharg, int bytesarg, jclass jClass, JNIEnv* jniEnv){
	bytes = bytesarg;
	pairs = pairsarg;
	bbytes = bbytesarg;
	hapl = haplarg;
	bbytes_length = bbytes_lengtharg;
	by_length = by_lengtharg;
	by = byarg;
	batches = batchesarg;
	read_length = read_lengtharg;
	read = readarg;
	bx = bxarg;
	bx_length = bx_lengtharg;
	hapl_length = hapl_lengtharg;
	env = jniEnv;
	if(jClass == NULL){
		printf("Provided java class object is null..!");
		return;
	}
	jmethodID constructor = env->GetMethodID(jClass, "<init>", "(II[I[I[I[I[II)V");
	if(constructor == NULL){
		printf("Constructor object method is null");
		return;
	}
	jintArray haplArr = env->NewIntArray(hapl_lengtharg);
	env->SetIntArrayRegion(haplArr, 0, hapl_lengtharg, hapl);
	jintArray readArr = env->NewIntArray(read_lengtharg);
	env->SetIntArrayRegion(readArr, 0, read_lengtharg, read);
	jintArray bxArr = env->NewIntArray(bx_lengtharg);
	env->SetIntArrayRegion(bxArr, 0, bx_lengtharg, bx);
	jintArray byArr = env->NewIntArray(by_lengtharg);
	env->SetIntArrayRegion(byArr, 0, by_lengtharg, by);
	jintArray bbytesArr = env->NewIntArray(bbytes_lengtharg);
	env->SetIntArrayRegion(bbytesArr, 0, bbytes_lengtharg, bbytes);
	jniJavaClassRef = env->NewObject(jClass, constructor, pairs, batches, haplArr, readArr, bxArr, byArr, bbytesArr, bytes);
}
CPPWorkloadPairHmmBean::CPPWorkloadPairHmmBean(){
	jniJavaClassRef = NULL;
}
CPPWorkloadPairHmmBean::~CPPWorkloadPairHmmBean() {
	if(jniCreated != 0){
	env->ReleasePrimitiveArrayCritical(haplArr, hapl, 0);
	env->ReleasePrimitiveArrayCritical(readArr, read, 0);
	env->ReleasePrimitiveArrayCritical(bxArr, bx, 0);
	env->ReleasePrimitiveArrayCritical(byArr, by, 0);
	env->ReleasePrimitiveArrayCritical(bbytesArr, bbytes, 0);
	jniCreated = 0;
}
}
