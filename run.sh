#!/bin/bash

METHODS="boolean tf tf_idf"
PROG="java -Xmx2048M -jar bow.jar"
INPUT="20news-18828/"

for METHOD in $METHODS
do
    echo running complete
    $PROG -m ${METHOD}                    -o arff/news_${METHOD}.arff.gz $INPUT
    echo running stemmed
    $PROG -m ${METHOD}                 -s -o arff/news_${METHOD}_s.arff.gz $INPUT
    echo running filter 1
    $PROG -m ${METHOD} -l 0.005 -u 0.7    -o arff/news_${METHOD}_0.005_0.7.arff.gz $INPUT
    echo running filter 1 stemmed
    $PROG -m ${METHOD} -l 0.005 -u 0.7 -s -o arff/news_${METHOD}_s_0.005_0.7.arff.gz $INPUT
    echo running filter 2
    $PROG -m ${METHOD} -l 0.01  -u 0.4    -o arff/news_${METHOD}_0.01_0.4.arff.gz $INPUT
    echo running filter 2 stemmed
    $PROG -m ${METHOD} -l 0.01  -u 0.4 -s -o arff/news_${METHOD}_s_0.01_0.4.arff.gz $INPUT
done
