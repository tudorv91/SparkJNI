#include "/home/tudor/Desktop/Thesis/projects/PairHMM_TACC/pairhmm.h"
std::shared_ptr<CPPWorkloadPairHmmBean> loadSizes(std::shared_ptr<CPPSizesBean>& cppsizesbean0,  jclass cppworkloadpairhmmbean_jClass, JNIEnv* jniEnv) {
	std::shared_ptr<CPPWorkloadPairHmmBean> returnedCppworkloadpairhmmbean0 = std::make_shared<CPPWorkloadPairHmmBean>(3, 3, new int[3]{1,2,3}, 3, new int[3]{1,2,3}, 3, new int[3]{1,2,3}, 3, new int[3]{1,2,3}, 3, new int[3]{1,2,3}, 3, 3, cppworkloadpairhmmbean_jClass, jniEnv);
	return returnedCppworkloadpairhmmbean0;
}

std::shared_ptr<CPPByteArrBean> callDataLoader(std::shared_ptr<CPPWorkloadPairHmmBean>& cppworkloadpairhmmbean0,  jclass cppbytearrbean_jClass, JNIEnv* jniEnv) {
	std::shared_ptr<CPPByteArrBean> returnedCppbytearrbean0 = std::make_shared<CPPByteArrBean>(ERROR: Unimplemented default initialization type, 3, cppbytearrbean_jClass, jniEnv);
	return returnedCppbytearrbean0;
}

std::shared_ptr<CPPPairHmmBean> calculateSoftware(std::shared_ptr<CPPPairHmmBean>& cpppairhmmbean0,  jclass cpppairhmmbean_jClass, JNIEnv* jniEnv) {
	std::shared_ptr<CPPPairHmmBean> returnedCpppairhmmbean0 = std::make_shared<CPPPairHmmBean>(ERROR: Unimplemented default initialization type, cpppairhmmbean_jClass, jniEnv);
	return returnedCpppairhmmbean0;
}

std::shared_ptr<CPPPairHmmBean> calculateHardware(std::shared_ptr<CPPPairHmmBean>& cpppairhmmbean0,  jclass cpppairhmmbean_jClass, JNIEnv* jniEnv) {
	std::shared_ptr<CPPPairHmmBean> returnedCpppairhmmbean0 = std::make_shared<CPPPairHmmBean>(ERROR: Unimplemented default initialization type, cpppairhmmbean_jClass, jniEnv);
	return returnedCpppairhmmbean0;
}

