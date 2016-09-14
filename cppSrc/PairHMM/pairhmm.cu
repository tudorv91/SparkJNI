#define _POSIX_C_SOURCE 200809L

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <unistd.h>
#include <string.h>
#include <time.h>
#include <sys/time.h>
#include <cuda.h>
#include <omp.h>
#include "libcxl.h"

#include "defines.h"
#include "batch.h"
#include "utils.h"
#include "psl.h"
#include "batch.c"
#include "utils.c"


#define BILLION 1000000000L




//****************codes below added by shanshan*************//

typedef struct struct_NUM_ADD
  {
    short read_number;
    short haplotype_number;
    int address_array;
  } NUM_ADD;

typedef struct struct_parameters{
  float distm_simi[32];
  float distm_diff[32];
  float alpha[32];
  float beta[32];
  float delta[32];
  float upsilon[32];
  float eta[32];
  float zeta[32];
} t_parameters;



__global__ void  pairHMM( int size, char * data,NUM_ADD * num_add, float * result) // what is the maximum number of parameters?
  {
   int offset=blockIdx.x;

   while(offset<size)
   {  
    //as each time it will deal with 2 read&haplotype pairs
    // each block deal with one pairs of haplotype & read
    NUM_ADD number_address;
    number_address=num_add[offset];

    int read_number=number_address.read_number; 
    int haplotype_number=number_address.haplotype_number;
   
    char * read_base_array=(char *)(data+number_address.address_array); // to caculate the address of read_base_array. 
    char4 * haplotype_base_array=(char4 * )(read_base_array+(read_number+127)/128*128);
    int aa=(haplotype_number+3)/4;
    t_parameters *parameter_array=(t_parameters *) (read_base_array+(read_number+127)/128*128+ (aa*4+127)/128*128);
      

    __shared__ char haplotype_base_in_char[350];
    int hh=(haplotype_number+4-1)/4;
    int tt=(hh+blockDim.x-1)/blockDim.x;
    for(int ii=0;ii<tt;ii++)
    { 
      int aa=threadIdx.x+ii*blockDim.x;
      if(aa< hh)
      {
      char4 haplotype_base_in_thread;
      haplotype_base_in_thread=haplotype_base_array[aa]; //Is it right to get data from global memory
      haplotype_base_in_char[aa*4]=haplotype_base_in_thread.x;
      haplotype_base_in_char[aa*4+1]=haplotype_base_in_thread.y;
      haplotype_base_in_char[aa*4+2]=haplotype_base_in_thread.z;
      haplotype_base_in_char[aa*4+3]=haplotype_base_in_thread.w;
}
    }
    __syncthreads();

    float MM, DD,II;
    float Qm,Qm_1,alpha,beta,delta,epsion,xiksi,thet;
    float D_0=(ldexpf(1.f, 120))/(float)haplotype_number;
    
    __shared__ float MM_stored[270];// as long as the haplotype
    __shared__ float DD_stored[270];
    __shared__ float II_stored[270];
    float result_block=0;
    int round=(read_number+blockDim.x-1)/blockDim.x;
      
    int round_size;
    char read_base;
    for(int i=0;i<round;i++)
    {
      round_size=(read_number>blockDim.x)?blockDim.x: read_number;
      read_number=(read_number>blockDim.x)?read_number-blockDim.x:0; // read_num is the remaining length at this round
      
      if(threadIdx.x<round_size ) // tid is from 0 ~ round_size-1
      {
        read_base=read_base_array[threadIdx.x+blockDim.x*i];
       
        Qm_1=parameter_array[i].distm_simi[threadIdx.x];
        Qm=parameter_array[i].distm_diff[threadIdx.x];
        alpha=parameter_array[i].alpha[threadIdx.x];
        beta=parameter_array[i].beta[threadIdx.x];
        delta=parameter_array[i].delta[threadIdx.x];
        epsion=parameter_array[i].upsilon[threadIdx.x];
        xiksi=parameter_array[i].eta[threadIdx.x];
        thet=parameter_array[i].zeta[threadIdx.x];


 }
        
      float M=0; //now 
      float I=0; //now
      float D=0; //now
      float MMM=0;//up left
      float DDD=0;//up left
      float III=0;//up left
      if(threadIdx.x==0&&i==0) DDD=D_0; // Just in the first round, it need to be D_0
      
      int current_haplotype_id=0;
      for(int j=0;j<round_size+haplotype_number-1;j++)
      { 
        int aa=j-threadIdx.x; 
        if( aa>=0 && (current_haplotype_id<haplotype_number))
        {
          if(threadIdx.x==0) // if it is the second or more round
                                        {
                                                if(i>0)
                                                {
                                                MM=MM_stored[current_haplotype_id];
                                                II=II_stored[current_haplotype_id];
                                                DD=DD_stored[current_haplotype_id];
                                                }
                                                else
                                                {
                                                MM=0;
                                                II=0;
                                                DD=D_0;
                                                }

                                        }   
                                        float MID=__fadd_rn(III,DDD);
                                        DDD=DD;
                                        III=II;
                                        float DDM=__fmul_rn(M,xiksi);
                                        float IIMI=__fmul_rn(II,epsion);
                                        float MIIDD=__fmul_rn(beta,MID);
                                        char haplotype_base_each=haplotype_base_in_char[current_haplotype_id];
                                        float aa=(haplotype_base_each==read_base)? Qm_1:Qm;
                                        D=__fmaf_rn(D,thet,DDM);
                                        I=__fmaf_rn(MM,delta,IIMI);
                                        float MMID=__fmaf_rn(alpha,MMM,MIIDD);
                                        MMM=MM;
                                        current_haplotype_id++;
                                        M=__fmul_rn(aa,MMID);
                                        II=I;
                                        DD=D;
                                        MM=M;


                                  }
        
                                if(threadIdx.x==round_size-1 && i<round-1) // tid is the last thread but there are more round
                                {
                                        MM_stored[current_haplotype_id-1]=M;
                                        II_stored[current_haplotype_id-1]=I;
                                        DD_stored[current_haplotype_id-1]=D;
                                }
                                if(threadIdx.x==round_size-1 && i==round-1)
                                        result_block=__fadd_rn(result_block,__fadd_rn(M,I));

                                MM=__shfl_up(MM,1);
                                II=__shfl_up(II,1);
                                DD=__shfl_up(DD,1);
      }
    }
    if(threadIdx.x==round_size-1) 
    {
      result[offset]=result_block;
    } 
    offset+=gridDim.x;  
   }

}
//****************above codes added by shanshan*************//



int main (int argc, char *argv[]) {
  struct timespec hwstart, hwend;
  //struct cxl_afu_h *afu;
  void             *batch;
  t_result         *result_hw;
  t_result         *result_sw;
  t_workload       *workload;
  t_batch          *batches;  
  
  unsigned char    show_table = 0;
  unsigned char		 show_results = 0;
  unsigned char 	 calculate_sw = 0;
  double					 clock_sw;
  double					 clock_hw;
  
  uint64_t   			 threads = 1;

  DEBUG_PRINT("Parsing input arguments...\n");
  if (argc < 5) {
    fprintf(stderr, "ERROR: Correct usage is: %s <-f = file, -m = manual> ... \n-m: <pairs> <X> <Y> ... \n-f: <input file>\n... <number of threads*> <sw solve?*> <show results?*> <show MID table?*> (* is optional)\n", APP_NAME);
    return -1;
  } 
  else {
		if (strncmp(argv[1],"-f",2)==0) {
			if ((workload = load_workload(argv[2])) == NULL) {
				fprintf(stderr, "ERROR: %s cannot be opened.\n", argv[2]);
				return -1;
			}
			if (argc >= 4) threads			= strtoul(argv[3], NULL, 0);
			if (argc >= 5) calculate_sw = strtoul(argv[4], NULL, 0);
			if (argc >= 6) show_results = strtoul(argv[5], NULL, 0);
			if (argc >= 7) show_table   = strtoul(argv[6], NULL, 0);
			
			if (threads <= 0) threads = omp_get_max_threads();
			
			BENCH_PRINT("%s, ", argv[2]);
			BENCH_PRINT("%8d, ", (int) workload->pairs);
			BENCH_PRINT("%8d, ", (int) threads);
		}
		else if (strncmp(argv[1],"-m",2)==0) {
      DEBUG_PRINT("Manual input mode selected. %d arguments supplied.\n", argc);
			int pairs = strtoul(argv[2], NULL, 0);
			int x = strtoul(argv[3], NULL, 0);
			int y = strtoul(argv[4], NULL, 0);
      
      workload = gen_workload(pairs, x, y);
      
			if (argc >= 6) threads			= strtoul(argv[5], NULL, 0);
			if (argc >= 7) calculate_sw = strtoul(argv[6], NULL, 0);
			if (argc >= 8) show_results = strtoul(argv[7], NULL, 0);
			if (argc >= 9) show_table   = strtoul(argv[8], NULL, 0);
			if (threads <= 0) threads = omp_get_max_threads();

			BENCH_PRINT("M, ");
			BENCH_PRINT("%8d, %8d, %8d, ", workload->pairs, x, y);
			BENCH_PRINT("%8d, ", (int) threads);
		}
		else
		{
			fprintf(stderr, "ERROR: Correct usage is: %s <-f = file, -m = manual> ... \n-m: <pairs> <X> <Y> ... \n-f: <input file>\n... <number of threads*> <sw solve?*> <show results?*> <show MID table?*> (* is optional)\n", APP_NAME);
			return EXIT_FAILURE;
		}
  }
  
  BENCH_PRINT("%16lu, ",workload->cups_req);
  
  DEBUG_PRINT("Total workload bytes: %17d \n", (unsigned int) workload->bytes);
  DEBUG_PRINT("CUPS required       : %17lu \n", workload->cups_req);     
  DEBUG_PRINT("Allocating memory for %d batches and %d results...\n", (unsigned int) workload->batches, (unsigned int) workload->pairs);

  if (posix_memalign( (void **) &batch, CACHELINE_BYTES, workload->bytes)) {
    perror("Could not allocate memory to store the batches.\n");
    return -1;
  }

  if (posix_memalign( (void **) &result_hw, CACHELINE_BYTES, sizeof(t_result) * workload->batches * PIPE_DEPTH)) {
    perror("Could not allocate memory to store hardware results.\n");
    return -1;
  }

  if (posix_memalign( (void **) &result_sw, CACHELINE_BYTES, sizeof(t_result) * workload->batches * PIPE_DEPTH)) {
    perror("Could not allocate memory to store software results.\n");
    return -1;
  }

  DEBUG_PRINT("Clearing batch and host result memory ...\n");
  memset(result_sw, 0xFF, sizeof(t_result)  * workload->batches * PIPE_DEPTH);
  memset(batch,     0x00, workload->bytes);



  DEBUG_PRINT("Filling batches...\n");

  clock_sw = omp_get_wtime();
  
  void * batch_cur = batch;  
  
  batches = (t_batch*) malloc(sizeof(t_batch) * workload->batches);
      
  for (int q = 0; q < workload->batches; q++) {
    init_batch_address(&batches[q], batch_cur, workload->bx[q], workload->by[q]);
    fill_batch(&batches[q], workload->bx[q], workload->by[q], 1.0);
    print_batch_info(&batches[q]);    
    batch_cur = (void*) ((uint64_t) batch_cur + (uint64_t) workload->bbytes[q]);
  }





  clock_sw = omp_get_wtime() - clock_sw;
  BENCH_PRINT("%16f,",clock_sw);  
  
  DEBUG_PRINT("Calculating on host...\n");

 // printf("\n software start \n") ;
  clock_sw = omp_get_wtime();
  
  //print_batch_memory(batch, workload->bbytes[0] + workload->bbytes[1]);
    
  if (calculate_sw) 
  {
	omp_set_num_threads(threads);
	#pragma omp parallel for
     for (int q = 0; q < workload->batches; q++) {
      
      int x = workload->bx[q];
      int y = workload->by[q];
	 float * M = (float*)malloc(sizeof(float) * (y+1) * (x+1));
	 float * I = (float*)malloc(sizeof(float) * (y+1) * (x+1));
	 float * D = (float*)malloc(sizeof(float) * (y+1) * (x+1));
		       
      // Calculate results
	for (int p = 0; p < PIPE_DEPTH; p++) 
	{
		    calculate_mids(&batches[q], p, x, y, M, I, D);
        
        result_sw[q*PIPE_DEPTH+p].values[0] = 0.0;
        for (int c = 0; c < y+1; c++)
        {
       // WARNING: THIS IS BECAUSE FLOATING POINT ADDITION IS NOT ASSOCIATIVE
          result_sw[q*PIPE_DEPTH+p].values[0] += M[(y+1)*x+c];
          result_sw[q*PIPE_DEPTH+p].values[0] += I[(y+1)*x+c];
      }
	//printf("software result    %e\n", result_sw[q*PIPE_DEPTH+p].values[0]);
	 if (show_table != 0) {
		      print_mid_table(&batches[q], p, x, y, M, I, D);
          fflush(stdout);
		    }
		  }
		  
		  free(M);
		  free(I);
		  free(D);
		}
  }

  clock_sw = omp_get_wtime() - clock_sw;
  
  if (calculate_sw) {
		BENCH_PRINT("%16f, ", clock_sw);
    BENCH_PRINT("%16f, ", workload->cups_req / clock_sw / 1000000);
  }
	else
  {
    BENCH_PRINT("%16f,",0.0);
    BENCH_PRINT("%16f,",0.0);
  }
    
  
  
  DEBUG_PRINT("%d %d\n",calculate_sw, show_results);
  
  if (calculate_sw && (show_results > 0)) {
  	print_results(result_sw, workload->batches);
  }
  
  DEBUG_PRINT("Clearing result memory\n");
  memset(result_hw, 0xFF, sizeof(t_result)  * workload->batches * PIPE_DEPTH);
  
  //printf("\nSoftware end\n") ;
 // DEBUG_PRINT("Opening device: %s ...\n", DEVICE);
 // afu = cxl_afu_open_dev ((char*) (DEVICE));
 // if (!afu) { perror ("cxl_afu_open_dev"); return -1; }




 cudaSetDevice(0);
 //start GPU programming
  
  //change data format
  //**********in each batch, there is only one pair of read and haplotype. I change the value of PIPE_DEPTH in define.h file
  //memory on host
  struct timespec start,finish;
 
    double computation_time=0;
  int size=workload->batches; // how many pairs in the workloads 
  char * data_h_total;
  data_h_total=(char*) malloc(size*10000*sizeof(char)+sizeof(NUM_ADD)*size);

  NUM_ADD * data_num_add=(NUM_ADD *) (data_h_total);
  char * data_h=data_h_total+(size*sizeof(NUM_ADD)+127)/128*128;  // to make sure the address is aligned

  //memory on GPU
  char * result_d_total;
  cudaMalloc( (char **) &result_d_total, size*10000*sizeof(char)+(size*sizeof(NUM_ADD)+127)/128*128+(size*sizeof(float)+127)/128*128);
  char * data_d_total=result_d_total+(size*sizeof(float)+127)/128*128; // to make sure the address is aligned.


  int data_size=0;
  //for each pair
  for(int q=0;q<workload->batches;q++)
  {
   
    int read_size_new=workload->bx[q];
    int haplotype_size_new=workload->by[q];
    //change read
    char read_base_new[500];
    for(int i=0;i<read_size_new;i++)
    {
      read_base_new[i]=batches[q].read[i].base[0];

   }
    //change haplotype
    int haplotype_size_new_new=(haplotype_size_new+3)/4;
    char4 haplotype_base_new[150];
    for(int i=0;i<haplotype_size_new_new;i++)
    {
          haplotype_base_new[i].x=batches[q].hapl[i*4].base[0];
    	 if(i*4+1<haplotype_size_new)
	{
          haplotype_base_new[i].y=batches[q].hapl[i*4+1].base[0];
         }
	 if(i*4+2<haplotype_size_new)
        {
	  haplotype_base_new[i].z=batches[q].hapl[i*4+2].base[0];
        }
	  if(i*4+3<haplotype_size_new)
        {  haplotype_base_new[i].w=batches[q].hapl[i*4+3].base[0];
   	 }	
    }


    //change parameter
    t_parameters pa[20];
    int aa=(read_size_new+31)/32;
    for(int i=0;i<aa;i++)
    {
      for(int j=0;j<32;j++)
      {
        if(i*32+j<read_size_new)
        {
        pa[i].distm_simi[j]=batches[q].prob[i*32+j].p[7].f;
        pa[i].distm_diff[j]=batches[q].prob[i*32+j].p[6].f;
        pa[i].alpha[j]=batches[q].prob[i*32+j].p[5].f;
        pa[i].beta[j]=batches[q].prob[i*32+j].p[4].f;
        pa[i].delta[j]=batches[q].prob[i*32+j].p[3].f;
        pa[i].upsilon[j]=batches[q].prob[i*32+j].p[2].f;
        pa[i].eta[j]=batches[q].prob[i*32+j].p[1].f;
        pa[i].zeta[j]=batches[q].prob[i*32+j].p[0].f;


	    }
      }
    }

        data_num_add[q].read_number=read_size_new;
        data_num_add[q].haplotype_number=haplotype_size_new;
        data_num_add[q].address_array=data_size;

        memcpy(data_h,read_base_new,sizeof(char)*read_size_new);
        
        data_h+=(read_size_new+127)/128*128;
        data_size+=(read_size_new+127)/128*128;

        memcpy(data_h,haplotype_base_new,sizeof(char4)* haplotype_size_new_new);
        data_h+=(haplotype_size_new_new*sizeof(char4)+127)/128*128;
        data_size+=(haplotype_size_new_new*sizeof(char4)+127)/128*128;
      
        memcpy(data_h,pa,sizeof(t_parameters) *aa);
        data_h+=sizeof(t_parameters)*aa;
        data_size+=sizeof(t_parameters)*aa;
      
  }

      int data_size_to_copy=data_size+(size*sizeof(NUM_ADD)+127)/128*128;
      float * result_h=(float *) malloc(sizeof(float)*size);
      
      clock_hw = omp_get_wtime();
      clock_gettime(CLOCK_MONOTONIC_RAW, &hwstart); /* mark start time */
     
      cudaMemcpy(data_d_total,data_h_total,data_size_to_copy,cudaMemcpyHostToDevice);
      NUM_ADD * num_add_d=(NUM_ADD *) (data_d_total);
      char * data_d=data_d_total+(sizeof(NUM_ADD)*size+127)/128*128;
      

     //  call kernel
      dim3 block(32);
      dim3 grid(size);
         pairHMM<<<grid,block>>> (size,data_d, num_add_d,(float *)result_d_total);

     cudaMemcpy(result_h,result_d_total,size*sizeof(float),cudaMemcpyDeviceToHost);

     // for(int i=0;i<size;i++)
      //   printf("GPU result   i=%d  %e\n",i, result_h[i]);
      //cudaDeviceReset();
    clock_gettime(CLOCK_MONOTONIC_RAW, &hwend); 
    clock_hw =omp_get_wtime() - clock_hw;
    

    uint64_t diff = BILLION * (hwend.tv_sec - hwstart.tv_sec) + hwend.tv_nsec - hwstart.tv_nsec; 
     free(result_h);
     free(data_h_total);
     cudaFree(result_d_total);

  int errs = 0;
  
  if (calculate_sw)
  {
	  errs = count_errors((uint32_t *)result_hw,(uint32_t *)result_sw,workload->batches);
	}
  
  DEBUG_PRINT("Errors: %d\n",errs);

   
     BENCH_PRINT(" %16f,",clock_hw);
     BENCH_PRINT("  %16llu,", (long long unsigned int) diff); 
  
      BENCH_PRINT(" %16f,", ((double)workload->cups_req / (double)clock_hw) / 1000000);
    
	if (calculate_sw)
	{
	BENCH_PRINT("%16f,",clock_sw / clock_hw);
	}
	else
		BENCH_PRINT(" %16f,",0.0);
	
	BENCH_PRINT("%16d",errs);


	BENCH_PRINT("\n");
	free(workload);
	free(result_sw);
  	free(batch);

  return 0;
}

