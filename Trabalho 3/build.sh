#!/bin/bash

set -e

echo "Limpando arquivos antigos..."
rm -rf BlackjackRMI_server/bin
rm -rf BlackjackRMI_client/bin
rm -f BlackjackRMI_server/server.jar
rm -f BlackjackRMI_client/client.jar

echo "Compilando servidor..."
mkdir -p BlackjackRMI_server/bin

javac -d BlackjackRMI_server/bin \
    BlackjackRMI_server/src/interfaces/*.java \
    BlackjackRMI_server/src/models/*.java \
    BlackjackRMI_server/src/server/*.java \
    BlackjackRMI_server/src/Main.java

jar cfe BlackjackRMI_server/server.jar Main \
    -C BlackjackRMI_server/bin .

echo "Compilando cliente..."
mkdir -p BlackjackRMI_client/bin

javac --enable-preview --release 21 \
    -d BlackjackRMI_client/bin \
    BlackjackRMI_client/src/interfaces/*.java \
    BlackjackRMI_client/src/Main.java

jar cfe BlackjackRMI_client/client.jar Main \
    -C BlackjackRMI_client/bin .

echo
echo "Build concluído!"
echo
echo "Para executar:"
echo
echo "Servidor:"
echo "java -jar BlackjackRMI_server/server.jar"
echo
echo "Cliente:"
echo "java --enable-preview -jar BlackjackRMI_client/client.jar"