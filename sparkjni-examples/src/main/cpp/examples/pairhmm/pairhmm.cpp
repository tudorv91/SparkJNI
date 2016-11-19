#define _POSIX_C_SOURCE 200809L

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <unistd.h>
#include <string.h>
#include <time.h>
#include <sys/time.h>
#include <omp.h>
extern "C" {
#include "defines.h"
#include "batch.h"
#include "utils.h"
#include "libcxl.h"
#include "psl.h"
}

#include "jni.h"
#include "org_tudelft_ewi_ceng_examples_pairHMM_LoadSizesJniFunction.h"
#include "org_tudelft_ewi_ceng_examples_pairHMM_DataLoaderJniFunction.h"
#include "org_tudelft_ewi_ceng_examples_pairHMM_PairHmmJniFunction.h"

#include "CPPPairHmmBean.h"
#include "CPPSizesBean.h"
#include "CPPWorkloadPairHmmBean.h"

JNIEXPORT jobject JNICALL Java_org_tudelft_ewi_ceng_examples_pairHMM_PairHmmJniFunction_calculateSoftware(
		JNIEnv *env, jobject caller, jobject dataInObj) {
	jclass pairHmmBeanClass = env->GetObjectClass(dataInObj);
	CPPPairHmmBean pairHmmBean(pairHmmBeanClass, dataInObj, env);
	CPPWorkloadPairHmmBean* workloadBean = pairHmmBean.getworkload();
	CPPByteArrBean* byteArrBean = pairHmmBean.getbyteArrBean();

	void * batch = (void*) byteArrBean->getarr();

	int* bx = workloadBean->getbx();
	int* by = workloadBean->getby();
	int* bbytes = workloadBean->getbbytes();
	t_batch *batches = (t_batch*) malloc(
			sizeof(t_batch) * workloadBean->getbatches());
	void * batch_cur = batch;

	for (int q = 0; q < workloadBean->getbatches(); q++) {
		init_batch_address(&batches[q], batch_cur, bx[q], by[q]);
		DEBUG_PRINT("Batch %3d -Init    : %016lX\n", q,
				(uint64_t) (batches[q].init));
		DEBUG_PRINT("          -Read    : %016lX\n",
				(uint64_t) (batches[q].read));
		DEBUG_PRINT("          -Hapl    : %016lX\n",
				(uint64_t) (batches[q].hapl));
		DEBUG_PRINT("          -Prob    : %016lX\n",
				(uint64_t) (batches[q].prob));
//		fill_batch(&batches[q], bx[q], by[q], 1.0);
		batch_cur = (void*) ((uint64_t) batch_cur + (uint64_t) bbytes[q]);
	}

	t_result * result_sw = (t_result*) malloc(
			sizeof(t_result) * workloadBean->getbatches() * PIPE_DEPTH);
	memset(result_sw, 0xFF,
			sizeof(t_result) * workloadBean->getbatches() * PIPE_DEPTH);

	double start = omp_get_wtime();
	for (int q = 0; q < workloadBean->getbatches(); q++) {

		int x = bx[q];
		int y = by[q];

//		printf("\nbx:%d by:%d\n", x, y);
		int size = (y + 1) * (x + 1);

		float * M = new float[size];
		float * I = new float[size];
		float * D = new float[size];

		// Calculate results
		for (int p = 0; p < PIPE_DEPTH; p++) {
//			printf("\n\tCalculating mids of %d\n", p);
			calculate_mids(batches + q, p, x, y, M, I, D);

			result_sw[q * PIPE_DEPTH + p].values[0] = 0.0;
			for (int c = 0; c < y + 1; c++) {
				// WARNING: THIS IS BECAUSE FLOATING POINT ADDITION IS NOT ASSOCIATIVE
				result_sw[q * PIPE_DEPTH + p].values[0] += M[(y + 1) * x + c];
				result_sw[q * PIPE_DEPTH + p].values[0] += I[(y + 1) * x + c];
			}
		}

		delete (M);
		delete (I);
		delete (D);
	}

	double totalFPGA = omp_get_wtime() - start;

	// DEBUG_PRINT("%d %d\n", calculate_sw, show_results);

	jbyte* returnarr = (jbyte*)malloc(sizeof(t_result) * workloadBean->getbatches() * PIPE_DEPTH);
	memcpy(returnarr, result_sw, sizeof(t_result) * workloadBean->getbatches() * PIPE_DEPTH);

	jclass byteArrBeanClass = env->FindClass(
			"org/tudelft/ewi/ceng/examples/pairHMM/ByteArrBean");
	CPPByteArrBean* returnBean = new CPPByteArrBean((jbyte*) returnarr,
			sizeof(t_result) * workloadBean->getbatches() * PIPE_DEPTH,
			byteArrBeanClass, env);
	CPPPairHmmBean returnPairBean(totalFPGA, workloadBean, returnBean, pairHmmBeanClass, env);

	printf("\nProcessing SW done..\n");
	return returnPairBean.getJavaObject();
}

JNIEXPORT jobject JNICALL Java_org_tudelft_ewi_ceng_examples_pairHMM_PairHmmJniFunction_calculateHardware(
		JNIEnv *env, jobject caller, jobject dataInObj) {
	struct cxl_afu_h *afu;
	double start = omp_get_wtime();
	jclass pairHmmBeanClass = env->GetObjectClass(dataInObj);
	CPPPairHmmBean pairHmmBean(pairHmmBeanClass, dataInObj, env);
	CPPWorkloadPairHmmBean* workloadBean = pairHmmBean.getworkload();

	CPPByteArrBean* byteArrBean = pairHmmBean.getbyteArrBean();

	void * jBatch = (void*) byteArrBean->getarr();
	void* batch;
	struct wed *wed0 = NULL;
	t_result * result_hw;

	DEBUG_PRINT("Setting up Work Element Descriptor...\n");

	if (posix_memalign((void **) &(batch), CACHELINE_BYTES, workloadBean->getbytes())) {
		perror("posix_memalign");
		return NULL;
	}
	if (posix_memalign((void **) &(wed0), CACHELINE_BYTES, sizeof(struct wed))) {
		perror("posix_memalign");
		return NULL;
	}
	if (posix_memalign( (void **) &result_hw, CACHELINE_BYTES, sizeof(t_result) * workloadBean->getbatches() * PIPE_DEPTH)) {
		perror("Could not allocate memory to store hardware results.\n");
		// return NULL;
	}
	memset(result_hw, 0xFF, sizeof(t_result) * workloadBean->getbatches() * PIPE_DEPTH);
	memcpy(batch, jBatch, workloadBean->getbytes());

	wed0->status = 0;
	wed0->source = (__u64 *) batch;
	wed0->destination = (__u64 *) result_hw;
	wed0->batch_size = (__u32) 0;
	wed0->pair_size = (__u32) 0;
	wed0->padded_size = (__u32) 0;
	wed0->batches = (__u32) workloadBean->getbatches();

	afu = cxl_afu_open_dev ((char*) (DEVICE));
  	if (!afu) { perror ("cxl_afu_open_dev"); return NULL; }

	DEBUG_PRINT("Attaching to AFU and passing WED address...\n");
	if (cxl_afu_attach(afu, (__u64) wed0) < 0) {perror ("cxl_afu_attach"); return NULL;}

	DEBUG_PRINT("Mapping MMIO...\n");
	if ((cxl_mmio_map(afu, CXL_MMIO_BIG_ENDIAN)) < 0) {
		perror("cxl_mmio_map");
		return NULL;
	}

	DEBUG_PRINT("Waiting for last result...\n");

	volatile int temp;
	while ((!wed0->status)|| (result_hw[workloadBean->getbatches() * PIPE_DEPTH - 1].b[0] == 0xFFFFFFFF)) {
		temp++;
#ifdef DEBUG
		sleep(5);
		for (int i = 0; i < workloadBean->getbatches() * PIPE_DEPTH; i++)
		{
			DEBUG_PRINT("%4d: %X" , i,result_hw[i].b[0]);
			DEBUG_PRINT(" %X" , result_hw[i].b[1]);
			DEBUG_PRINT(" %X" , result_hw[i].b[2]);
			DEBUG_PRINT(" %X\n" , result_hw[i].b[3]);
		}
#endif
	}

	jclass byteArrBeanClass = env->FindClass(
			"org/tudelft/ewi/ceng/examples/pairHMM/ByteArrBean");

	CPPByteArrBean *returnBean = new CPPByteArrBean((jbyte*) result_hw,
			sizeof(t_result) * workloadBean->getbatches() * PIPE_DEPTH,
			byteArrBeanClass, env);
	double totalFPGA = omp_get_wtime() - start;
	CPPPairHmmBean returnPairBean(totalFPGA, workloadBean, returnBean, pairHmmBeanClass, env);
	printf("\nProcessing HW done..\n");
	printf("\nTEMP %d\n",temp);

	cxl_mmio_unmap (afu);
	cxl_afu_free(afu);
	return returnPairBean.getJavaObject();
}

JNIEXPORT jobject JNICALL Java_org_tudelft_ewi_ceng_examples_pairHMM_LoadSizesJniFunction_loadSizes(
		JNIEnv *env, jobject caller, jobject sizes) {
	jclass sizesBeanClass = env->GetObjectClass(sizes);
	CPPSizesBean cppSizesBean(sizesBeanClass, sizes, env);

	int lines = cppSizesBean.getcol1_length();
	int pairs = lines;
	int batches = lines / PIPE_DEPTH;
	int* hapl = cppSizesBean.getcol1();
	int* read = cppSizesBean.getcol2();
	int* bx = (int*) malloc(batches * sizeof(int));
	int* by = (int*) malloc(batches * sizeof(int));
	int* bbytes = (int*) malloc(batches * sizeof(int));

	int bytes = 0;

	for (int b = 0; b < pairs / PIPE_DEPTH; b++) {
		int xmax = 0;
		int ymax = 0;
		for (int p = 0; p < PIPE_DEPTH; p++) {
			if (read[b * PIPE_DEPTH + p] > xmax)
				xmax = read[b * PIPE_DEPTH + p];
			if (hapl[b * PIPE_DEPTH + p] > ymax)
				ymax = hapl[b * PIPE_DEPTH + p];
		}
		bx[b] = xmax;
		by[b] = ymax;
		bbytes[b] = calc_batch_size(xmax, ymax, PES);
		bytes += bbytes[b];
	}

	jclass workloadClass = env->FindClass(
			"org/tudelft/ewi/ceng/examples/pairHMM/WorkloadPairHmmBean");
	if (workloadClass == NULL) {
		printf("workloadClass is NULL..try again");
		return NULL;
	}

	printf("before return..");
	CPPWorkloadPairHmmBean ret(lines, batches, hapl, lines, read, lines, bx,
			batches, by, batches, bbytes, batches, bytes, workloadClass, env);
	return ret.getJavaObject();
}

JNIEXPORT jobject JNICALL Java_org_tudelft_ewi_ceng_examples_pairHMM_DataLoaderJniFunction_callDataLoader(
		JNIEnv *env, jobject caller, jobject workloadObj) {
	jclass workloadClass = env->GetObjectClass(workloadObj);
	CPPWorkloadPairHmmBean workloadBean(workloadClass, workloadObj, env);
	BENCH_PRINT("%d, ", workloadBean.getpairs());

	DEBUG_PRINT("Allocating memory for %d batches and %d results...\n",
			(unsigned int) workloadBean.getbatches(),
			(unsigned int) workloadBean.getpairs());

	t_batch * batch = (t_batch*) malloc(workloadBean.getbytes());

	DEBUG_PRINT("Clearing batch and host result memory ...\n");
	memset(batch, 0x00, workloadBean.getbytes());

	DEBUG_PRINT("Filling batches...\n");

	void * batch_cur = batch;

	t_batch *batches = (t_batch*) malloc(
			sizeof(t_batch) * workloadBean.getbatches());

	int* bx = workloadBean.getbx();
	int* by = workloadBean.getby();
	int* bbytes = workloadBean.getbbytes();

	for (int q = 0; q < workloadBean.getbatches(); q++) {
		init_batch_address(&batches[q], batch_cur, bx[q], by[q]);
		DEBUG_PRINT("Batch %3d -Init    : %016lX\n", q,
				(uint64_t) (batches[q].init));
		DEBUG_PRINT("          -Read    : %016lX\n",
				(uint64_t) (batches[q].read));
		DEBUG_PRINT("          -Hapl    : %016lX\n",
				(uint64_t) (batches[q].hapl));
		DEBUG_PRINT("          -Prob    : %016lX\n",
				(uint64_t) (batches[q].prob));
		fill_batch(&batches[q], bx[q], by[q], 1.0);
		batch_cur = (void*) ((uint64_t) batch_cur + (uint64_t) bbytes[q]);
	}

	jclass byteArrBeanClass = env->FindClass(
			"org/tudelft/ewi/ceng/examples/pairHMM/ByteArrBean");
	CPPByteArrBean byteArrayBean((jbyte*) batch, workloadBean.getbytes(),
			byteArrBeanClass, env);

	printf("\ndataIn done;\n");
	return byteArrayBean.getJavaObject();
}
