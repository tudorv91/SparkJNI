#!/usr/bin/env python

import subprocess

pes = 32;
step = 1;
readmin = 10;
readmax = 101;
haplmin = 10;
haplmax = 400;


for x in range(readmin, readmax):
	for y in range(haplmin, haplmax):
		workload = 32;
		subprocess.call(["./pairhmm","-m",str(workload),str(x*step), str(y*step), "20", "1"])

