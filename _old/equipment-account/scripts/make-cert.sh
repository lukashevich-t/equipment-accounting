#!/bin/bash

clientTrustStore=/d/utils/wildfly/equipment/standalone/configuration/certs/users.jks

if [ "x$STOREPASS" = "x" ]; then
    echo !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    echo You didn''t specify keystore password
    echo Before running this script, execute:
    echo  " export STOREPASS=secret" '(with leading space!)'
    echo !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    exit
fi

if [ "x$@" = "x" ]; then
    echo !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    echo You didn''t specify any user names
    echo Specify them as arguments in command line, for example:
    echo ./make.sh tim kan ...
    echo !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    exit
fi

mkdir -p temp/key-out

for CN in "$@"
do
    DN="CN=$CN, OU=UIST, O=BTO, L=Minsk, C=BY"
    echo Making certificate for $DN...
    rm "$CN.p12"
    export PASS=123456

    keytool -storepass $PASS -genkey -alias "$CN" -keystore "$CN.p12" -storetype PKCS12 -validity 3650 -keyalg RSA -dname "$DN"
    keytool -export -rfc -storepass $PASS -alias "$CN" -file "$CN.cer" -keystore "$CN.p12" -storetype PKCS12
    keytool -delete -alias "$DN" -keystore "$clientTrustStore" -storepass $STOREPASS

    echo yes | keytool -import -v -trustcacerts -alias "$DN" -file "$CN.cer" -keystore "$clientTrustStore" -storepass $STOREPASS

    openssl pkcs12 -in "$CN.p12" -nocerts -passin env:PASS -passout env:PASS -out "temp/key-out/$CN.key"
    openssl pkcs12 -in "$CN.p12" -clcerts -passin env:PASS -passout env:PASS -nokeys -out "temp/key-out/$CN.crt"

    mv "$CN.p12" temp/key-out/
    rm "$CN.cer"
done
