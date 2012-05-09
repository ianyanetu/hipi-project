#!/bin/bash
cd ..
ant clean
ant imagescaler
cd experiments
./runImageScaler.sh ../images/test_jpg ../images/output 50 small_files

