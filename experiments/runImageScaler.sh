#!/bin/bash
ant -f ../build.xml imagescaler
hadoop jar imagescaler.jar $1 $2 $3 $4
