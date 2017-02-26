#include "generatortest.h"
std::shared_ptr<CPPDoubleArray> arrayCopy(std::shared_ptr<CPPDoubleArray>& cppdoublearray0,  jclass cppdoublearray_jClass, JNIEnv* jniEnv) {
	std::shared_ptr<CPPDoubleArray> returnedCppdoublearray0 = std::make_shared<CPPDoubleArray>(new double[3]{1.0,2.0,3.0}, 3, cppdoublearray_jClass, jniEnv);
	return returnedCppdoublearray0;
}

