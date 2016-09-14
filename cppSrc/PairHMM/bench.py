#!/usr/bin/env python

import subprocess

step = 4;
readmin = 4 	/ step;
readmax = (256+step) / step;
haplmax = (256+step) / step;
pairslogmin = 5;
pairslogmax = 6;

for x in range(readmin, readmax):
	for y in range(x, haplmax):
		for pairs in range(pairslogmin, pairslogmax):
			workload = 2**pairs
			subprocess.call(["./pairhmm","-m",str(workload),str(x*step), str(y*step), "20", "0"])

