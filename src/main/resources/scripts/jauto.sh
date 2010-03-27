#!/bin/sh
# This script outputs a representation of 
# the automaton from a rational expression

DIRNAME=`dirname $0`
export CLASSPATH=

for i in ${DIRNAME}/../lib/*; do
   CLASSPATH=$i:$CLASSPATH
done
JAVA=

if ! which java > /dev/null 2>&1;then
    echo "Cannot find path for java executable, exiting"
    exit 1
fi

JAVA=`which java`

# execute
exec $JAVA -classpath $CLASSPATH rationals.JAuto $@