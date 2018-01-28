#!/bin/bash

i=0
TIMEOUT=30

while true; do
	docker exec postgres pg_isready >& /dev/null

	if [ $? -ne 0 ] && [ $i -lt $TIMEOUT ]; then
		sleep 1
		i=$((i+1))
	elif [ $i -ge $TIMEOUT ]; then
		exit -1
	else
		exit 0
	fi
done
