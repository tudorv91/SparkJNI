#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <unistd.h>
#include <string.h>
#include <time.h>
#include <sys/time.h>
#include <omp.h>

#include "defines.h"
#include "batch.h"
#include "utils.h"

const char *binstr(uint8_t x) {
    int z;
    static char b[9];
    b[0] = '\0';
    for (z = 128; z > 0; z >>= 1) {
        strcat(b, ((x & z) == z) ? "1" : "0");
    }
    return b;
}

void print_omp_info(void) {
  int omp_np = omp_get_num_procs();
  int omp_mt = omp_get_max_threads();

  printf("Host OpenMP statistics:\n");
  printf("╔═══════════════════════════════════════════════════\n");
  printf("║ Number of processors : %4d\n", omp_np);
  printf("║ Max threads          : %4d\n", omp_mt);
  printf("╚═══════════════════════════════════════════════════\n");

  fflush(stdout);
  return;
}

void print_mid_table(t_batch * batch, int pair, int r, int c, float * M, float * I, float * D) {
  
  int w = c + 1;
  t_bbase * read = batch->read;
  t_bbase * hapl = batch->hapl;
  t_result res;
  
  res.values[0] = (float)0.0;
    
  printf("════╦");       for (uint32_t i=0;i<c+1;i++) printf("══════════════════════════╦"); printf("\n");
  printf("    ║");       for (uint32_t i=0;i<c+1;i++) printf("      %5d , %c           ║",i, (i>0)?(hapl[i-1].base[pair]):'-'); printf("\n"); 
  printf("%3d ║", pair); for (uint32_t i=0;i<c+1;i++) printf("══════════════════════════╣"); printf("\n"); 
  printf("    ║");       for (uint32_t i=0;i<c+1;i++) printf("   M        I        D    ║"); printf("\n"); 
  printf("════╣");       for (uint32_t i=0;i<c+1;i++) printf("══════════════════════════╣"); printf("\n");

  // loop over rows
  for (uint32_t j=0;j<r+1;j++) {
    printf("%2d,%c║",j,(j>0)?(read[j-1].base[pair]):('-'));
    // loop over columns
    for (uint32_t i=0;i<c+1;i++) {
      printf("%08X %08X %08X║",*(uint32_t*)&M[j*w+i], *(uint32_t*)&I[j*w+i], *(uint32_t*)&D[j*w+i]);
    }
    printf("\n");
  }
  printf("════╣");       for (uint32_t i=0;i<c+1;i++) printf("══════════════════════════╣"); printf("\n");
  // Result row
  printf("res:║");
  for (uint32_t i=0;i<c+1;i++) {
      res.values[0] += M[r*w+i];
      res.values[0] += I[r*w+i];
      printf("                  %08X║",res.b[0]);
  }
  printf("\n");
  printf("═════");       for (uint32_t i=0;i<c+1;i++) printf("═══════════════════════════"); printf("\n");
  
  
  
  fflush(stdout);
  return;
}

void print_results(t_result * results, int num_batches) {
  DEBUG_PRINT("╔═══════════════════════════════╗\n");
  for (int q = 0; q < num_batches; q++) {
    DEBUG_PRINT("║ RESULT FOR BATCH %3d:         ║\n",q);
    DEBUG_PRINT("╠═══════════════════════════════╣\n");
    for (int p = 0; p < PIPE_DEPTH; p++) {
      DEBUG_PRINT("║%2d: %08X %08X %08X ║\n",
                  p,
                  results[q*PIPE_DEPTH+p].b[0],
                  results[q*PIPE_DEPTH+p].b[1],
                  results[q*PIPE_DEPTH+p].b[2]
      );
    }
    DEBUG_PRINT("╚═══════════════════════════════╝\n");
  }
  fflush(stderr);
  return;
}

void print_batch_memory(void* batch, size_t batch_size)
{
  DEBUG_PRINT("    ");
  for (int j = 0; j < 64; j++)
    DEBUG_PRINT("%02X ", j);
    
  DEBUG_PRINT("\n");
  for (int i = 0; i < batch_size/64; i++)
  {
    DEBUG_PRINT("%02X: ", i);
    for (int j = 0; j < 64; j++)
      DEBUG_PRINT("%02X ", *((uint8_t*)batch + i*64+j));
    DEBUG_PRINT("\n");
  }
  fflush(stderr);
}

void print_batch_info(t_batch * batch)
{
    DEBUG_PRINT("Batch pointers:");
    DEBUG_PRINT("Init: %016lX ", (uint64_t) (batch->init));
    DEBUG_PRINT("Read: %016lX ", (uint64_t) (batch->read));
    DEBUG_PRINT("Hapl: %016lX ", (uint64_t) (batch->hapl));
    DEBUG_PRINT("Prob: %016lX \n", (uint64_t) (batch->prob));
    DEBUG_PRINT("X:%d, PX:%d, PBPX:%d, Y:%d, PY:%d, PBPY:%d\n",
      batch->init->x_size    ,
      batch->init->x_padded  ,
      batch->init->y_size    ,
      batch->init->y_padded  ,
      batch->init->x_bppadded
    );
}

// padded read size
int px(int x, int y)
{
	if (py(y) > PES) {   // if feedback fifo is used
		if (x <= PES)     // and x is equal or smaller than number of PES
			x = PES + 1;    // x will be no. PES + 1, +1 is due to delay in the feedback fifo path
  }
  else {               // feedback fifo is not used
    if (x < PES)      // x is smaller than no. PES
      x = PES;        // pad x to be equal to no. PES
  }
  return x;
}

int pbp(int x)
{
  return (x/BASE_STEPS + (x % BASE_STEPS != 0)) * BASE_STEPS;
}

// padded haplotype size
int py(int y)
{
	// divide Y by PES and round up and multiply:
  return (y/PES + (y % PES != 0)) * PES;
}

// Load workload from file
t_workload * load_workload(char * fname)
{
  int x;
  int y;
  FILE * fp;
  char str[64];
  int lines = 0;
  t_workload * workload = (t_workload*)malloc(sizeof(t_workload));
    
  fp = fopen(fname, "r");
  if (fp == NULL)
    return NULL;
  
  // Count number of lines:
  while(!feof(fp))
    if(fgetc(fp) == '\n')
      lines++;
  
  DEBUG_PRINT("Input file containts %d lines\n",lines);
  
  workload->pairs   = lines;
  
  if (workload->pairs % PIPE_DEPTH != 0) {
		fprintf(stderr,"Number of pairs must be multiple of %d.\n",PIPE_DEPTH);
  	exit(EXIT_FAILURE);
  }
  
  workload->batches = lines / PIPE_DEPTH;
  
  // Allocate memory
  workload->hapl     = (uint32_t*) malloc(lines             * sizeof(uint32_t));
  workload->read     = (uint32_t*) malloc(lines             * sizeof(uint32_t));
  workload->bx       = (uint32_t*) malloc(workload->batches * sizeof(uint32_t));
  workload->by       = (uint32_t*) malloc(workload->batches * sizeof(uint32_t));
  workload->bbytes   = (size_t*)   malloc(workload->batches * sizeof(size_t));
  workload->cups_req = 0;
 
  // Load the data:
  int i=0;
  rewind(fp);
	while (!feof(fp))
	{
		fgets(str,64,fp);
		if (!feof(fp))
		{
			sscanf(str, "%u %u\n", &y, &x);
			if (x > MAX_BP_STRING) {fprintf(stderr,"%s line %d: Read size exceeds maximum of %d\n",fname, i,MAX_BP_STRING); exit(EXIT_FAILURE);}
			if (y > MAX_BP_STRING) {fprintf(stderr,"%s line %d: Haplotype size on exceeds maximum of %d\n",fname, i,MAX_BP_STRING);exit(EXIT_FAILURE);}
			if (y < x) {
//				fprintf(stderr,"%s line %d: haplotype must be larger than read.\n",fname, i);
//				exit(EXIT_FAILURE);
			}

			workload->hapl[i] = y;
			workload->read[i] = x;		
			workload->cups_req += (uint64_t)y * (uint64_t)x;
			i++;
		}
	}
  
  // Set batch info
  DEBUG_PRINT("Batch ║ MAX X ║ MAX Y ║ Passes ║  Bytes ║\n");
  DEBUG_PRINT("═════════════════════════════════════════\n");
  
  // One more cacheline for the first batch.
  workload->bytes = CACHELINE_BYTES;
  
  for (int b = 0; b < workload->pairs / PIPE_DEPTH; b++) {
    int xmax = 0;
    int ymax = 0;
    for (int p = 0; p < PIPE_DEPTH; p++) {
      if (workload->read[b*PIPE_DEPTH+p] > xmax) xmax = workload->read[b*PIPE_DEPTH+p];
      if (workload->hapl[b*PIPE_DEPTH+p] > ymax) ymax = workload->hapl[b*PIPE_DEPTH+p];
    }
    workload->bx[b]     = xmax;
    workload->by[b]     = ymax;
    workload->bbytes[b] = calc_batch_size(xmax,ymax,PES);
    workload->bytes    += workload->bbytes[b];
    
    DEBUG_PRINT("%5d ║ %5d ║ %5d ║ %6d ║ %6d ║\n",b,xmax,ymax,PASSES(ymax),(unsigned int)workload->bbytes[b]);
  }
      
  fclose(fp);
  
  return workload;
}

t_workload * gen_workload(unsigned long pairs, unsigned long fixedX, unsigned long fixedY)
{
	DEBUG_PRINT("Generating workload for %d pairs, with X=%d and Y=%d\n",(int)pairs, (int)fixedX, (int)fixedY);
  t_workload * workload = (t_workload*)malloc(sizeof(t_workload));
  
  if (fixedY < fixedX) {
		//printf("Haplotype cannot be smaller than read.\n");
    //exit(EXIT_FAILURE);
  }
    
  workload->pairs   = pairs;
  
  if (workload->pairs % PIPE_DEPTH != 0) {
		printf("Number of pairs must be an integer multiple of %d.\n",PIPE_DEPTH);
  	exit(EXIT_FAILURE);
  }
  
  workload->batches = pairs / PIPE_DEPTH;
  
  // Allocate memory
  workload->hapl      = (uint32_t*) malloc(workload->pairs   * sizeof(uint32_t));
  workload->read      = (uint32_t*) malloc(workload->pairs   * sizeof(uint32_t));
  workload->bx        = (uint32_t*) malloc(workload->batches * sizeof(uint32_t));
  workload->by        = (uint32_t*) malloc(workload->batches * sizeof(uint32_t));
  workload->bbytes    = (size_t*)   malloc(workload->batches * sizeof(size_t));
  workload->cups_req  = 0;
  
	for (int i=0;i<workload->pairs;i++)
  {
		workload->hapl[i] = fixedY;
		workload->read[i] = fixedX;
    workload->cups_req += fixedY * fixedX;
  }
  
  // Set batch info
  DEBUG_PRINT("Batch ║ MAX X ║ MAX Y ║ Passes ║  Bytes ║\n");
  DEBUG_PRINT("═════════════════════════════════════════\n");
  
  workload->bytes = CACHELINE_BYTES;
  
  for (int b = 0; b < workload->pairs / PIPE_DEPTH; b++) {
    int xmax = 0;
    int ymax = 0;
    for (int p = 0; p < PIPE_DEPTH; p++) {
      if (workload->read[b*PIPE_DEPTH+p] > xmax) xmax = workload->read[b*PIPE_DEPTH+p];
      if (workload->hapl[b*PIPE_DEPTH+p] > ymax) ymax = workload->hapl[b*PIPE_DEPTH+p];
    }
    workload->bx[b]     = xmax;
    workload->by[b]     = ymax;
    workload->bbytes[b] = calc_batch_size(xmax,ymax,PES);
    workload->bytes    += workload->bbytes[b];
    
    DEBUG_PRINT("%5d ║ %5d ║ %5d ║ %6d ║ %6d ║\n",b,xmax,ymax,PASSES(ymax),(unsigned int)workload->bbytes[b]);
  }
       
  return workload;
}

