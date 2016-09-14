all:
	nvcc -arch=sm_35 --use_fast_math --ptxas-options=-v -O3  -o pairhmm pairhmm.cu batch.c utils.c /tools/ppc_64/libcxl/libcxl.a -L/tools/ppc_64/libcxl  -I/tools/ppc_64/libcxl  --compiler-options -m64 --compiler-options -std=c99 --compiler-options -lrt  --compiler-options  -lpthread --compiler-options -fopenmp
	#nvcc -arch=sm_35 --use_fast_math --ptxas-options=-v -O3  --compiler-options -DDBUG -o pairhmm pairhmm.cu batch.c utils.c /tools/ppc_64/libcxl/libcxl.a -L/tools/ppc_64/libcxl  -I/tools/ppc_64/libcxl  --compiler-options -m64 --compiler-options -std=c99 --compiler-options -lrt  --compiler-options  -lpthread --compiler-options -fopenmp
	#nvcc -arch=sm_35 --use_fast_math --ptxas-options=-v -O3  -o
