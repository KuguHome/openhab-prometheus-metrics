#!/bin/bash
cd ..
mvn clean
cd com.kuguhome.openhab.prometheusmetrics.p2site/
mvn p2:site
cd ..
mvn install
cd com.kuguhome.openhab.prometheusmetrics/
echo "Copying artifact to Raspberry Pi..."
scp target/*.jar kugu-sz-roman:/usr/share/openhab2/addons
echo "Deploying artifact to Raspberry Pi..."
ssh kugu-sz-roman sudo systemctl restart openhab2
echo "Waiting till application being started..."
sleep 30s
sleep 60s
echo "Fetching Rasberry Pi endpoint $1"
curl $1