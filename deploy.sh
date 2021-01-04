#!/bin/bash
cd ~/fabric-samples/test-network
rm -r ~/fabric-samples/contract-java
cp -r ~/vagrant/contract-java ~/fabric-samples
./network.sh down
./network.sh up
./network.sh createChannel
pushd ../contract-java
./gradlew installDist