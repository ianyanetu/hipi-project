#!/bin/bash
ant clean
ant downloader
cd examples
./runDownloader.sh ../input/testurl.txt ../output/downloadJob/downloadout.hib 5
