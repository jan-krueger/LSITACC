#!/bin/bash
echo "Generate key called $1..."

openssl genrsa -out $1.cert 2048
openssl pkcs8 -topk8 -inform PEM -outform DER -in $1.cert -out $1.pri -nocrypt
openssl rsa -in $1.cert -pubout -outform DER -out $1.pub