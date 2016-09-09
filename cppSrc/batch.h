#ifndef __BATCH_H
#define __BATCH_H

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <time.h>
#include <sys/time.h>
#include <omp.h>

#include "defines.h"

typedef union union_prob {
  float         f; // as float
  uint32_t      b; // as binary
} t_prob;

typedef struct struct_probs {
  t_prob        p[8];   //lambda_1, lambda_2, alpha, beta, delta, epsilon, zeta eta
} t_probs;

typedef struct struct_bbase {
  unsigned char base[PIPE_DEPTH];
} t_bbase;

typedef struct struct_sizes {
  uint32_t      pairsize[PIPE_DEPTH];
  uint8_t       padding[CACHELINE_BYTES - sizeof(uint32_t) * PIPE_DEPTH];
} t_sizes;

// Initial values and batch configuration
typedef struct struct_init {
  float         initials[PIPE_DEPTH];   //   0 ...  511
  uint32_t      batch_bytes;            // 512 ...  543 
  uint32_t      x_size;                 // 544 ...  575
  uint32_t      x_padded;               // 576 ...  607
  uint32_t      y_size;                 // 608 ...  639
  uint32_t      y_padded;               // 640 ...  671
  uint32_t      x_bppadded;             // 672 ...  704
  uint8_t       padding[40];            // 
} t_inits;

typedef struct struct_batch {
  t_inits       *init;
  t_bbase       *read;
  t_bbase       *hapl;
  t_probs       *prob;
} t_batch;

typedef union union_result {
  float         values[3];
  uint32_t      b[4];     // integer image and padding
} t_result;

void fill_batch(t_batch * b, int pair_size, int padded_size, float initial);

void calculate_mids(t_batch * batch, int pair, int r, int c, float * M, float * I, float * D);

int count_errors (uint32_t * hr, uint32_t * sr, int num_batches);

void init_batch_address(t_batch * b, void * batch, int x, int y);

size_t calc_batch_size(int x, int y, int e);

#endif //__BATCH_H
