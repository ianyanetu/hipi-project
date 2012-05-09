#!/bin/bash
./downloadJob.sh
ant clean
ant downloader
cd examples
./runDumpHIB.sh ../output/downloadJob/downloadout.hib ../output/dumpJob/
