#include "CPPWorkloadPairHmmBean.h"
#include "CPPByteArrBean.h"
#include "CPPPairHmmBean.h"
	std::mutex CPPPairHmmBean::mtx;
CPPPairHmmBean::CPPPairHmmBean(jclass replaceMeClassName, jobject replaceMeObjectName, JNIEnv* env){

	jniJavaClassRef = replaceMeObjectName;
	jfieldID j_memcopyTime = env->GetFieldID(replaceMeClassName, "memcopyTime", "D");
	memcopyTime = env->GetDoubleField(replaceMeObjectName, j_memcopyTime);
	jfieldID j_workload = env->GetFieldID(replaceMeClassName, "workload", "LpairHMM/WorkloadPairHmmBean;");
		if(j_workload == NULL){
		printf("FieldID object j_workload is null..");
		return;
	}
jobject workload_obj = env->GetObjectField(replaceMeObjectName, j_workload);
	jclass workloadClass = env->GetObjectClass(workload_obj);
	workload = new CPPWorkloadPairHmmBean(workloadClass, workload_obj, env);
	jfieldID j_byteArrBean = env->GetFieldID(replaceMeClassName, "byteArrBean", "LpairHMM/ByteArrBean;");
		if(j_byteArrBean == NULL){
		printf("FieldID object j_byteArrBean is null..");
		return;
	}
jobject byteArrBean_obj = env->GetObjectField(replaceMeObjectName, j_byteArrBean);
	jclass byteArrBeanClass = env->GetObjectClass(byteArrBean_obj);
	byteArrBean = new CPPByteArrBean(byteArrBeanClass, byteArrBean_obj, env);

	mtx.lock();
	mtx.unlock();
	this->env = env;
	jniCreated = 1;
}
	double CPPPairHmmBean::getmemcopyTime(){
		return memcopyTime;
	}

	CPPByteArrBean* CPPPairHmmBean::getbyteArrBean(){
		return byteArrBean;
	}

	CPPWorkloadPairHmmBean* CPPPairHmmBean::getworkload(){
		return workload;
	}

	jobject CPPPairHmmBean::getJavaObject(){
		return jniJavaClassRef;
	}
CPPPairHmmBean::CPPPairHmmBean(double memcopyTimearg, CPPWorkloadPairHmmBean* workloadarg, CPPByteArrBean* byteArrBeanarg, jclass jClass, JNIEnv* jniEnv){
	memcopyTime = memcopyTimearg;
	byteArrBean = byteArrBeanarg;
	workload = workloadarg;
	env = jniEnv;
	if(jClass == NULL){
		printf("Provided java class object is null..!");
		return;
	}
	jmethodID constructor = env->GetMethodID(jClass, "<init>", "(LpairHMM/WorkloadPairHmmBean;LpairHMM/ByteArrBean;D)V");
	if(constructor == NULL){
		printf("Constructor object method is null");
		return;
	}
	jniJavaClassRef = env->NewObject(jClass, constructor, workload->getJavaObject(), byteArrBean->getJavaObject(), memcopyTime);
}
CPPPairHmmBean::CPPPairHmmBean(){
}
CPPPairHmmBean::~CPPPairHmmBean() {
	if(jniCreated != 0){
	}
}
