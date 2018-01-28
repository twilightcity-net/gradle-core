#!/bin/bash

user=$1
dbName=$2

if [ -z "${dbName}" ]; then
    echo "usage: create_database_if_not_created.sh <user> <db name>"
    exit 1
fi

psql -U "${user}" -lqt | cut -d \| -f 1 | grep "${dbName}"
if [ $? -eq 1 ]; then
    echo "Creating database ${dbName}..."
    createdb -U "${user}" "${dbName}"
else
    echo "Database ${dbName} already exists"
fi
