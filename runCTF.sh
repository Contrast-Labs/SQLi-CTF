#!/bin/bash
echo -n "Enter Flag":
read -s password
echo $password > contrast/secret/flag
docker-compose rm -f
docker-compose up