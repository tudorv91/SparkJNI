#ifndef GENERATORTEST_KERNELWRAPPER
#define GENERATORTEST_KERNELWRAPPER
#include "/home/tudor/dev/SparkJNI/integration-tests/generator-test/src/test/resources/generatorTester/src/main/resources/generatortest/test_MapCopyFunc.h"
#include "CPPDoubleArray.h"

std::shared_ptr<CPPDoubleArray> arrayCopy(std::shared_ptr<CPPDoubleArray>& cppdoublearray0,  jclass cppdoublearray_jClass, JNIEnv* jniEnv);
JNIEXPORT jobject JNICALL Java_test_MapCopyFunc_arrayCopy(JNIEnv *jniEnv, jobject callerObject, jobject cppdoublearray_jObject0){
	jclass cppdoublearray_jClass = jniEnv->GetObjectClass(cppdoublearray_jObject0);
	std::shared_ptr<CPPDoubleArray> cppdoublearray0 = std::make_shared<CPPDoubleArray>(cppdoublearray_jClass, cppdoublearray_jObject0, jniEnv);
	std::shared_ptr<CPPDoubleArray> CPPDoubleArray_ret = arrayCopy(cppdoublearray0, cppdoublearray_jClass, jniEnv);
	jobject CPPDoubleArray_retJavaObj = CPPDoubleArray_ret->getJavaObject();
	return CPPDoubleArray_retJavaObj;
}
#endif