#include "/home/tudor/dev/SparkJNI/cppSrc/examples/SparkJNIPi/SparkJNIPi.h"
#include <CL/cl.h>
#include "clUtils.h"
#include <math.h>

#define MAX_SOURCE_SIZE (0x100000)
//#define DEBUG

const char piSource[] = "__kernel void run(__global float *randNumArray, __global int *sum){\n\
	int gid = get_global_id(0);\n\
	int baseIndex = gid * 2;\n\
	float x = (randNumArray[baseIndex] * 2.0f) - 1.0f;\n\
	float y = (randNumArray[(baseIndex + 1)] * 2.0f) - 1.0f;\n\
	sum[gid]  = (((x * x) + (y * y))<1.0f)?1:0;\n\
	return;\n\
}\n";

void openCLMap(float* randNumArray, int* sum, int sumArrLength);
void debug(cl_int errorCode, const char* funcName);

CPPSumArray* randNativePiMap(CPPRandNumArray& cppRandNumArray, JNIEnv* env){
	jclass sumClass = env->FindClass("org/heterojni/examples/sparkJNIPi/SumArray");
	int rnaLength = cppRandNumArray.getrandNumArray_length();
	float* randNumArray = cppRandNumArray.getrandNumArray();
	int sumLength = rnaLength/2;
	int *sum = new int[sumLength];
	for(int i = 0; i < sumLength; i++){
		float x = randNumArray[2*i] * 2 - 1, y = randNumArray[2*i + 1] * 2 - 1;
		sum[i] = (x*x + y*y) < 1 ? 1 : 0;
	}
	CPPSumArray *returnedSum = new CPPSumArray(sum, sumLength, sumClass, env);
	return returnedSum;
}

CPPSumArray* randOpenclPiMap(CPPRandNumArray& cppRandNumArray, JNIEnv* env){
	jclass sumClass = env->FindClass("org/heterojni/examples/sparkJNIPi/SumArray");
	int rnaLength = cppRandNumArray.getrandNumArray_length();
	int sumLength = rnaLength/2;
	int *sum = new int[sumLength];
	openCLMap(cppRandNumArray.getrandNumArray(), sum, sumLength);
	CPPSumArray *returnedSum = new CPPSumArray(sum, sumLength, sumClass, env);
	return returnedSum;
}

CPPSumArray* randOpenclPiMapCritical(CPPRandNumArray& cppRandNumArray, JNIEnv* env){
	jclass sumClass = env->FindClass("org/heterojni/examples/sparkJNIPi/SumArray");
	int rnaLength = cppRandNumArray.getrandNumArray_length();
	int sumLength = rnaLength/2;
	int *sum = new int[sumLength];
	openCLMap(cppRandNumArray.getrandNumArray(), sum, sumLength);
	CPPSumArray *returnedSum = new CPPSumArray(sum, sumLength, sumClass, env);
	return returnedSum;
}

void openCLMap(float* randNumArray, int* sum, int sumArrLength){
//	FILE *fp;
//	char fileName[] = "/home/tudor/dev/SparkJNI/cppSrc/examples/SparkJNIPi/Pi.cl";
//	char *source_str;
	size_t source_size;

	/* Load the source code containing the kernel*/
//	fp = fopen(fileName, "r");
//	if (!fp) {
//		fprintf(stderr, "Failed to load kernel.\n");
//		exit(1);
//	}
//	source_str = (char*)malloc(MAX_SOURCE_SIZE);
//	source_size = fread(source_str, 1, MAX_SOURCE_SIZE, fp);
//	fclose(fp);
	source_size = sizeof(piSource);
	// Device input buffers
	cl_mem sum_dev, randNumArray_dev;

    cl_platform_id cpPlatform;        // OpenCL platform
    cl_device_id device_id;           // device ID
    cl_context context;               // context
    cl_command_queue queue;           // command queue
    cl_program program;               // program
    cl_kernel kernel;                 // kernel

    size_t rnaLength = sumArrLength * 2, globalSize = sumArrLength;
    size_t sumSizeMem = sumArrLength * sizeof(int), rnaSizeMem = rnaLength * sizeof(float);

    cl_uint numPlatforms;
    cl_int err = clGetPlatformIDs(1, &cpPlatform, &numPlatforms);
    debug(err, "clGetPlatformIDs");
    printf("platforms:%d\n", numPlatforms);

    err = clGetDeviceIDs(cpPlatform, CL_DEVICE_TYPE_GPU, 1, &device_id, NULL);
    debug(err, "clGetDeviceIDs");

    context = clCreateContext(NULL, 1, &device_id, NULL, NULL, &err);
    debug(err, "clCreateContext");
    queue = clCreateCommandQueue(context, device_id, 0, &err);
    debug(err, "clCreateCommandQueue");
    program = clCreateProgramWithSource(context, 1, (const char **)&piSource, (const size_t *)&source_size, &err);
    debug(err, "clCreateProgramWithSource");

    clBuildProgram(program, 1, &device_id, NULL, NULL, NULL);

    kernel = clCreateKernel(program, "run", &err);
    debug(err, "clCreateKernel");

    randNumArray_dev = clCreateBuffer(context, CL_MEM_READ_ONLY, rnaSizeMem, NULL, NULL);
    sum_dev = clCreateBuffer(context, CL_MEM_WRITE_ONLY, sumSizeMem, NULL, NULL);

    err = clEnqueueWriteBuffer(queue, randNumArray_dev, CL_TRUE, 0, rnaSizeMem, randNumArray, 0, NULL, NULL);
    debug(err, "clEnqueueWriteBuffer");

    err = clSetKernelArg(kernel, 0, sizeof(cl_mem), &randNumArray_dev);
    debug(err, "clSetKernelArg");
    err = clSetKernelArg(kernel, 1, sizeof(cl_mem), &sum_dev);
    debug(err, "clSetKernelArg");

    err = clEnqueueNDRangeKernel(queue, kernel, 1, NULL, &globalSize, NULL, 0, NULL, NULL);
    debug(err, "clEnqueueNDRangeKernel");

    clFinish(queue);
    err = clEnqueueReadBuffer(queue, sum_dev, CL_TRUE, 0, sumSizeMem, sum, 0, NULL, NULL);
    debug(err, "clEnqueueReadBuffer");

#ifdef DEBUG
    for(int i = 0; i < sumArrLength; i++)
    	printf("%d\n", sum[i]);
#endif
    clReleaseMemObject(sum_dev);
    clReleaseMemObject(randNumArray_dev);
    clReleaseProgram(program);
    clReleaseKernel(kernel);
    clReleaseCommandQueue(queue);
    clReleaseContext(context);
}

void debug(cl_int errorCode, const char* funcName){
	printf("\t(%d)%s on %s\n", (int)errorCode, getErrorString(errorCode), funcName);
}




