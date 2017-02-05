#include "VectorOpsIntegrationTest.h"
std::shared_ptr<CPPVectorBean> reduceVectorAdd(std::shared_ptr<CPPVectorBean>& cppvectorbean0, std::shared_ptr<CPPVectorBean>& cppvectorbean1,  jclass cppvectorbean_jClass, JNIEnv* jniEnv) {
	std::shared_ptr<CPPVectorBean> returnedCppvectorbean0 = std::make_shared<CPPVectorBean>(new int[3]{1,2,3}, 3, cppvectorbean_jClass, jniEnv);
	return returnedCppvectorbean0;
}

std::shared_ptr<CPPVectorBean> mapVectorMul(std::shared_ptr<CPPVectorBean>& cppvectorbean0,  jclass cppvectorbean_jClass, JNIEnv* jniEnv) {
	std::shared_ptr<CPPVectorBean> returnedCppvectorbean0 = std::make_shared<CPPVectorBean>(new int[3]{1,2,3}, 3, cppvectorbean_jClass, jniEnv);
	return returnedCppvectorbean0;
}

