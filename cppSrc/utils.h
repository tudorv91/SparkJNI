#ifndef __UTILS_H
#define __UTILS_H

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

typedef struct struct_workload {
  int        pairs;
  uint32_t * hapl;
  uint32_t * read;
  
  int        batches;
  uint32_t * by;
  uint32_t * bx;
  size_t   * bbytes;
  
  size_t     bytes;
  
  uint64_t   cups_req;
} t_workload;

const char *binstr(uint8_t x);
void print_omp_info(void);
void print_mid_table(t_batch * batch, int pair, int r, int c, float * M, float * I, float * D);
void print_results(t_result * results, int num_batches);
void print_batch_memory(void* batch, size_t batch_size);
void print_batch_info(t_batch*batch);
int px(int x, int y);
int pbp(int x);
int py(int y);

t_workload * load_workload(char * fname);
t_workload * gen_workload(unsigned long pairs, unsigned long fixedX, unsigned long fixedY);


#endif //__UTILS_H
