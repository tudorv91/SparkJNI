__kernel void run(__global float *randNumArray, __global int *sum){
   int gid = get_global_id(0);
   int baseIndex = gid * 2;
   float x = (randNumArray[baseIndex] * 2.0f) - 1.0f;
   float y = (randNumArray[(baseIndex + 1)] * 2.0f) - 1.0f;
   sum[gid]  = (((x * x) + (y * y))<1.0f)?1:0;
   return;
}
