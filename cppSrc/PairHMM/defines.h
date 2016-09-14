#ifndef __DEFINES_H
#define __DEFINES_H

#define MAX_POLLS             32000
#define REPEATS               10
#define MAX_DIFF_REPORT       50
#define MAX_CACHELINES        (1 << 22)
#define DEVICE                "/dev/cxl/afu0.0d"

// PAIRHMM Constants:

// Maximum number of basepairs in a read/haplotype pair
// MAX_BP_STRING must be an integer multiple of 128
#define MAX_BP_STRING         512
#define PES                   32
#define FREQ									166666666.666666666666
#define MAX_CUPS							(double)PES * (double)FREQ

#define PASSES(Y)             1 + ((Y - 1) / PES)

// Batch size (or number of pipeline stages)
#define PIPE_DEPTH           1

// Length of pairs must be multiples of this number
// This is due to howmany bases fit in one cacheline
#define BASE_STEPS            8

// The error margin allowed
#define ERROR_MARGIN					0.000000000001
#define ERR_LOWER							1.0f - ERROR_MARGIN
#define ERR_UPPER							1.0f + ERROR_MARGIN

#define APP_NAME              "pairhmm"

#define CACHELINE_BYTES       128                   // 0x80
#define MMIO_DEBUG            0x03fffff8            // 0x03fffff8 >> 2 = 0xfffffe (because the last two bits are dropped by the PSL interface)

#define MMIO_PAIRHMM_CONTROL  0x00000000 << 2
#define MMIO_PAIRHMM_SIZE     0x00000002 << 2
#define MMIO_PAIRHMM_STATUS   0x00000004 << 2
#define MMIO_PAIRHMM_REQS     0x00000006 << 2
#define MMIO_PAIRHMM_REPS     0x00000008 << 2
#define MMIO_PAIRHMM_FIFO     0x0000000A << 2
#define MMIO_PAIRHMM_FIFO_RVS 0x0000000C << 2
#define MMIO_PAIRHMM_FIFO_WAS 0x0000000E << 2

// Debug printf macro
#ifdef DEBUG
#define DEBUG_PRINT(...) do{ fprintf( stderr, __VA_ARGS__ ); } while( 0 )
#define BENCH_PRINT(...) do{ } while ( 0 )
#else
#define DEBUG_PRINT(...) do{ } while ( 0 )
#define BENCH_PRINT(...) do{ fprintf( stderr, __VA_ARGS__ ); } while( 0 )
#endif

#endif //__DEFINES_H
