#encoding=utf-8
try:
    import psyco
    psyco.full()
except:
    pass

s3 = file("text.txt").read()
words = [x.rstrip() for x in file("main.dic") ]
from smallseg import SEG
seg = SEG()
print 'Load dict...'
seg.set(words)
print "Dict is OK."
from time import time

for i in xrange(1,101):
    start = time()
    for j in xrange(0,i):
        A = seg.cut(s3)
    cost = time()-start
    print i,"times, cost:",cost

print "********************************"

