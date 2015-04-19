#!/bin/python

import pycurl
import io
import sys
from PIL import Image

class FetchFromOSM():

    def __init__(self):
        self.zoom = 17
        self.latitude = range(46490, 46511)
        self.longitude = range(67730, 67751)
        
    def curl(self, url):
        e = io.BytesIO()
        c = pycurl.Curl()
        c.setopt(pycurl.URL, str(url))
        c.setopt(pycurl.WRITEFUNCTION, e.write)
        c.perform()
        return e.getvalue()

    def get_file_name(self, t, a, b):
        return '../assets/images/map/%s/%d-%d.png' % (t, b, a)
    
    def loop_and_save(self, url, mtype):
        progress = 0
        full = len(self.latitude)*len(self.longitude)
        for a in self.latitude:
            for b in self.longitude:
                print ('Downloading %s%%' % int(100*progress/full))
                u = self.url_builder(url, a, b)
                f = open(self.get_file_name(mtype, a, b), 'wb')
                f.write(self.curl(u))
                f.close()
                progress += 1

    def url_builder(self, u, a, b):
        return '%s%d/%d/%d.png' % (u, self.zoom, b, a)

if __name__ == "__main__":
    url = sys.argv[1]
    mtype = sys.argv[2]
    ffo = FetchFromOSM()
    ffo.loop_and_save(url, mtype)

'''
urlo = 'https://c.tile.openstreetmap.org/17/67730/46490.png'
ffo = FetchFromOSM()
a = ffo.curl(urlo)

file_ = open('somefile.png', 'wb')
file_.write(a)
file_.close()
'''
