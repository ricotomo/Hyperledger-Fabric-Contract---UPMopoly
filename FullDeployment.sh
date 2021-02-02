#!/bin/bash
cd ~/fabric-samples

rm -r -f ~/fabric-samples/contract-java

cp -r /vagrant/contract-java ~/fabric-samples

cd ~/fabric-samples/test-network

./network.sh down
./network.sh up
./network.sh createChannel

pushd ../contract-java

./gradlew installDist

cd ~/fabric-samples/test-network

rm -r -f upmmonopoly.tar.gz

export CORE_PEER_TLS_ENABLED=true

export FABRIC_CFG_PATH=~/fabric-samples/config

export CORE_PEER_MSPCONFIGPATH=~/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp

peer lifecycle chaincode package upmmonopoly.tar.gz --path ~/fabric-samples/contract-java/build/install/papercontract --lang java --label upmmonopoly

export CORE_PEER_LOCALMSPID="Org1MSP"

export CORE_PEER_TLS_ROOTCERT_FILE=~/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt

export CORE_PEER_MSPCONFIGPATH=~/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp

export CORE_PEER_ADDRESS=localhost:7051

peer lifecycle chaincode install upmmonopoly.tar.gz

export CC_PACKAGE_ID=$(peer lifecycle chaincode queryinstalled 2>&1 | awk -F "[, ]+" '/Label: upmmonopoly/{print $3}') && echo $CC_PACKAGE_ID


export CORE_PEER_LOCALMSPID="Org2MSP"

export CORE_PEER_TLS_ROOTCERT_FILE=~/fabric-samples/test-network/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt

export CORE_PEER_MSPCONFIGPATH=~/fabric-samples/test-network/organizations/peerOrganizations/org2.example.com/users/Admin@org2.example.com/msp

export CORE_PEER_ADDRESS=localhost:9051

peer lifecycle chaincode install upmmonopoly.tar.gz

peer lifecycle chaincode approveformyorg -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --channelID mychannel --name upmmonopoly --version 1.0 --package-id $CC_PACKAGE_ID --sequence 1 --tls --cafile ~/fabric-samples/test-network/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem

export CORE_PEER_LOCALMSPID="Org1MSP"

export CORE_PEER_TLS_ROOTCERT_FILE=~/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt

export CORE_PEER_MSPCONFIGPATH=~/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/users/Admin@org1.example.com/msp

export CORE_PEER_ADDRESS=localhost:7051

peer lifecycle chaincode approveformyorg -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --channelID mychannel --name upmmonopoly --version 1.0 --package-id $CC_PACKAGE_ID --sequence 1 --tls --cafile ~/fabric-samples/test-network/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem

peer lifecycle chaincode commit -o localhost:7050 --ordererTLSHostnameOverride orderer.example.com --channelID mychannel --name upmmonopoly --version 1.0 --sequence 1 --tls --cafile ~/fabric-samples/test-network/organizations/ordererOrganizations/example.com/orderers/orderer.example.com/msp/tlscacerts/tlsca.example.com-cert.pem --peerAddresses localhost:7051 --tlsRootCertFiles ~/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/peers/peer0.org1.example.com/tls/ca.crt --peerAddresses localhost:9051 --tlsRootCertFiles ~/fabric-samples/test-network/organizations/peerOrganizations/org2.example.com/peers/peer0.org2.example.com/tls/ca.crt
