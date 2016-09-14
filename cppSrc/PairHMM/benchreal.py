#!/usr/bin/env python

import subprocess

for x in range(0,1832):
   subprocess.call(["./pairhmm","-f","input/14/x"+str(x).zfill(4),"20","0"])
   
for x in range(0,916):
   subprocess.call(["./pairhmm","-f","input/15/x"+str(x).zfill(4),"20","0"])
  
for x in range(0,458):
   subprocess.call(["./pairhmm","-f","input/16/x"+str(x).zfill(4),"20","0"])
   
