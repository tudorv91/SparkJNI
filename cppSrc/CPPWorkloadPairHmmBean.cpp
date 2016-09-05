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

	mtx.lock();
	haplArr = reinterpret_cast<jintArray>(hapl_obj);
	hapl_length = env->GetArrayLength(haplArr);
	hapl = (int*)env->GetIntArrayElements(haplArr, NULL);
	readArr = reinterpret_cast<jintArray>(read_obj);
	read_length = env->GetArrayLength(readArr);
	read = (int*)env->GetIntArrayElements(readArr, NULL);
	bxArr = reinterpret_cast<jintArray>(bx_obj);
	bx_length = env->GetArrayLength(bxArr);
	bx = (int*)env->GetIntArrayElements(bxArr, NULL);
	byArr = reinterpret_cast<jintArray>(by_obj);
	by_length = env->GetArrayLength(byArr);
	by = (int*)env->GetIntArrayElements(byArr, NULL);
	bbytesArr = reinterpret_cast<jintArray>(bbytes_obj);
	bbytes_length = env->GetArrayLength(bbytesArr);
	bbytes = (int*)env->GetIntArrayElements(bbytesArr, NULL);
	mtx.unlock();
	this->env = env;
	jniCreated = 1;
}
	int* CPPWorkloadPairHmmBean::getbbytes(){
		return bbytes;
	}

	int* CPPWorkloadPairHmmBean::getread(){
		return read;
	}

	int* CPPWorkloadPairHmmBean::getby(){
		return by;
	}

	int CPPWorkloadPairHmmBean::getbx_length(){
		return bx_length;
	}

	int CPPWorkloadPairHmmBean::gethapl_length(){
		return hapl_length;
	}

	int CPPWorkloadPairHmmBean::getbbytes_length(){
		return bbytes_length;
	}

	int* CPPWorkloadPairHmmBean::getbx(){
		return bx;
	}

	int CPPWorkloadPairHmmBean::getread_length(){
		return read_length;
	}

	int CPPWorkloadPairHmmBean::getbatches(){
		return batches;
	}

	int* CPPWorkloadPairHmmBean::gethapl(){
		return hapl;
	}

	int CPPWorkloadPairHmmBean::getpairs(){
		return pairs;
	}

	int CPPWorkloadPairHmmBean::getbytes(){
		return bytes;
	}

	int CPPWorkloadPairHmmBean::getby_length(){
		return by_length;
	}

	jobject CPPWorkloadPairHmmBean::getJavaObject(){
		return jniJavaClassRef;
	}
CPPWorkloadPairHmmBean::CPPWorkloadPairHmmBean(int pairsarg, int batchesarg, int* haplarg, int hapl_lengtharg, int* readarg, int read_lengtharg, int* bxarg, int bx_lengtharg, int* byarg, int by_lengtharg, int* bbytesarg, int bbytes_lengtharg, int bytesarg, jclass jClass, JNIEnv* jniEnv){
	bbytes = bbytesarg;
	read = readarg;
	by = byarg;
	bx_length = bx_lengtharg;
	hapl_length = hapl_lengtharg;
	bbytes_length = bbytes_lengtharg;
	bx = bxarg;
	read_length = read_lengtharg;
	batches = batchesarg;
	hapl = haplarg;
	pairs = pairsarg;
	bytes = bytesarg;
	by_length = by_lengtharg;
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
}
CPPWorkloadPairHmmBean::~CPPWorkloadPairHmmBean() {
	if(jniCreated != 0){
env->ReleaseIntArrayElements(haplArr, hapl, 0);env->ReleaseIntArrayElements(readArr, read, 0);env->ReleaseIntArrayElements(bxArr, bx, 0);env->ReleaseIntArrayElements(byArr, by, 0);env->ReleaseIntArrayElements(bbytesArr, bbytes, 0);	}
}
