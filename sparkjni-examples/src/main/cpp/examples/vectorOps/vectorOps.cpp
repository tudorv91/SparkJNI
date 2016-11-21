#include "/home/tudor/Development/SparkJNI/sparkjni-examples/src/main/cpp/examples/vectorOps/vectorOps.h"
std::shared_ptr<CPPVectorBean> reduceVectorAdd(std::shared_ptr<CPPVectorBean>& cppvectorbean0, std::shared_ptr<CPPVectorBean>& cppvectorbean1,  jclass cppvectorbean_jClass, JNIEnv* jniEnv) {
	for(int idx = 0; idx < cppvectorbean0->getdata_length(); idx++)
		cppvectorbean0->getdata()[idx] += cppvectorbean1->getdata()[idx];
	return cppvectorbean0;
}

std::shared_ptr<CPPVectorBean> mapVectorMul(std::shared_ptr<CPPVectorBean>& cppvectorbean0,  jclass cppvectorbean_jClass, JNIEnv* jniEnv) {
	for(int idx = 0; idx < cppvectorbean0->getdata_length(); idx++)
		cppvectorbean0->getdata()[idx] *= 2;
	return cppvectorbean0;
}
