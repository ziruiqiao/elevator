#!/usr/bin/env python3
import sys, random
maxFloorNum = 9
time_interval = 1011

if len(sys.argv) < 2:
    print(sys.argv[0] + " numEvents [max floors] [msInterval]")
    exit(1)
if len(sys.argv) >= 3:
    maxFloorNum = int(sys.argv[2])
    
if len(sys.argv) >= 4:
    time_interval = int(sys.argv[3])
curTime = 0

def getTimeStr(t):
    h = 0
    m = 0
    s = 0
    if (t >= 60 * 60 * 1000):
        h = t//(60 * 60 * 1000)
        t -= h * 60 * 60 * 1000
    if (t >= 60 * 1000):
        m = t//(60 * 1000)
        t -= m * 60 * 1000
    if (t >= 1000):
        s = t//1000
        t -= s * 1000

    return "{:02d}:{:02d}:{:02d}:{:03d}".format(int(h),int(m),int(s),int(t))


def genReq():
    f1 = random.randint(0, maxFloorNum)        
    f2 = f1
    while f2 == f1:
        f2 = random.randint(0, maxFloorNum)
    if (f1 <  f2):
        return "{:d} up {:d}".format(f1, f2)
    return "{:d} down {:d}".format(f1, f2)

for i in range (int(sys.argv[1])):
    print(getTimeStr(curTime) + " " + genReq() )
    curTime += time_interval
    