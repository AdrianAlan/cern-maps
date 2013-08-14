#!/usr/bin/python

tram = '18'
answer = '"trams": ['
for i in range(0,5):
    f = open('scheduleWEEK', 'r')
    for line in f.read().split('\n'):
        if line:
            hour = line.split('h')[0]
            minutes = line.split('h')[1]
            for min in minutes.split('\t'):
                if not min.strip() == '':
                    answer += '{"line":"' + tram + '","time":"' + str(i) + ':' + hour + ':' + min.strip() + '"},'

f = open('scheduleSATURDAY', 'r')
for line in f.read().split('\n'):
    if line:
        hour = line.split('h')[0]
        minutes = line.split('h')[1]
        for min in minutes.split('\t'):
            if not min.strip() == '':
                answer += '{"line":"' + tram + '","time":"5:' + hour + ':' + min.strip() + '"},'

f = open('scheduleSUNDAY', 'r')
for line in f.read().split('\n'):
    if line:
        hour = line.split('h')[0]
        minutes = line.split('h')[1]
        for min in minutes.split('\t'):
            if not min.strip() == '':
                answer += '{"line":"' + tram + '","time":"6:' + hour + ':' + min.strip() + '"},'


answer += '\b]'

print answer
