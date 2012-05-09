#!/bin/bash
ant clean
ant imagescaler
cd experiments
./runImageScaler.sh ../input/test_jpg ../output 50 small_files

