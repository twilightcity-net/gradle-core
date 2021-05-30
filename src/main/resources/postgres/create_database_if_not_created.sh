#!/bin/bash

user=$1
dbName=$2

if [ -z "${dbName}" ]; then
  echo "usage: create_database_if_not_created.sh <user> <db name>"
  exit 1
fi

attempts=0
while true; do
  psql -U "${user}" -lqt | cut -d \| -f 1 | grep "${dbName}"

  if [ $? -ne 0 ]; then
    echo "Creating database ${dbName}..."
    createdb -U "${user}" "${dbName}"
    if [ $? -ne 0 ]; then
      attempts=$((attempts+1))
    fi
  else
    echo "Database ${dbName} already exists"
    exit 0
  fi

  if [ $attempts -ge 3 ]; then
    echo "Exceeded max number of retries, aborting"
    exit 1
  fi

  echo "Failed to create database, will retry in 1 second"
  sleep 1
done

