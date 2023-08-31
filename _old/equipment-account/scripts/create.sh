#!/bin/bash
JAVA_CLIENT_VERSION=2.7.2
WORKDIR=./temp
DLDIR=./dl
JBOSS_HOME=${WORKDIR}/wildfly
WILDFLY_VERSION=23.0.2.Final

. ./wildfly.properties
rm -rf "${WORKDIR}"
mkdir -p "${WORKDIR}"
mkdir -p "${DLDIR}"

WILDFLY_ZIP_FILE=wildfly-${WILDFLY_VERSION?}.zip
unzip -qt ${DLDIR}/${WILDFLY_ZIP_FILE} >/dev/null
if [ $? -ne 0 ] ; then
    curl --output ${DLDIR}/${WILDFLY_ZIP_FILE} https://download.jboss.org/wildfly/${WILDFLY_VERSION?}/${WILDFLY_ZIP_FILE}
fi


unzip -q -d ${WORKDIR} ${DLDIR}/wildfly-${WILDFLY_VERSION} >/dev/null
mv "${WORKDIR}/wildfly-${WILDFLY_VERSION}" "$JBOSS_HOME"

echo Создаю заново хранилища сертификатов.
mkdir -p $JBOSS_HOME/standalone/configuration/certs/

function recreateKeystore() {
    if [ $# -eq 0 ] ; then
        exit
    fi
    rm "$1"
    keytool -keystore "$1" -storepass "$KEYSTORE_PASS" -genkeypair -alias blablabla -keyalg RSA -dname "CN=blablabla, OU=IAO, O=BTO, L=Minsk, C=BY"
    keytool -keystore "$1" -storepass "$KEYSTORE_PASS" -alias blablabla -delete
}
recreateKeystore "$JBOSS_HOME/standalone/configuration/certs/users.jks"
recreateKeystore "$JBOSS_HOME/standalone/configuration/certs/server.jks"
keytool -keystore "$JBOSS_HOME/standalone/configuration/certs/server.jks" -storepass "$KEYSTORE_PASS" -genkeypair -alias equipment -keyalg RSA -dname "CN=proxy, OU=IAO, O=BTO, L=Minsk, C=BY"

curl https://repo1.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/$JAVA_CLIENT_VERSION/mariadb-java-client-$JAVA_CLIENT_VERSION.jar --output ${DLDIR}/mariadb-java-client.jar

cat >./temp/wildfly-script.txt <<ENDOFSTRING
module add --name=org.mariadb.jdbc --resources=${DLDIR}/mariadb-java-client.jar --dependencies=javax.api,javax.transaction.api,javax.servlet.api
/subsystem=datasources/jdbc-driver=mariadb-java-client:add(driver-module-name=org.mariadb.jdbc,driver-xa-datasource-class-name=org.mariadb.jdbc.MySQLDataSource,driver-name=mariadb-java-client)
data-source add --name=EquipmentDs --jndi-name=java:/env/jdbc/EquipmentDs --driver-name=mariadb-java-client --connection-url=jdbc:mysql://${MASTER_DB?}/equipment --user-name=${MASTER_DB_USER?} --password=${MASTER_DB_PASSWORD?} --enabled=true --jta=true --use-ccm=true --driver-class=org.mariadb.jdbc.Driver --max-pool-size=25 --min-pool-size=1 --initial-pool-size=1 --idle-timeout-minutes=5

/subsystem=security/security-domain=equipment-cert-domain:add(cache-type=default)
/subsystem=security/security-domain=equipment-cert-domain/authentication=classic:add
/subsystem=security/security-domain=equipment-cert-domain/authentication=classic/login-module=db-cert:add(code=org.jboss.security.auth.spi.DatabaseCertLoginModule,flag=required,module-options={dsJndiName=>java:/env/jdbc/EquipmentDs,securityDomain=>equipment-cert-domain,rolesQuery=>"SELECT r.name, 'Roles' FROM user u INNER JOIN user_role utr ON u.id = utr.user_id INNER JOIN role r ON utr.role_id = r.id WHERE u.login_cert = ?"})
/subsystem=security/security-domain=equipment-cert-domain/jsse=classic:add(keystore={password="${KEYSTORE_PASS?}",url="\${jboss.server.config.dir}/certs/server.jks"},truststore={password="${KEYSTORE_PASS?}",url="\${jboss.server.config.dir}/certs/users.jks"},client-auth=true)

/subsystem=security/security-domain=equipment-pass-domain:add(cache-type=default)
/subsystem=security/security-domain=equipment-pass-domain/authentication=classic:add
/subsystem=security/security-domain=equipment-pass-domain/authentication=classic/login-module=db:add(code=Database,flag=required,module-options={dsJndiName=>java:/env/jdbc/EquipmentDs,securityDomain=>equipment-pass-domain,principalsQuery=>"SELECT u.password FROM equipment.user u WHERE u.login=?",rolesQuery=>"SELECT r.name, 'Roles' FROM equipment.user u INNER JOIN equipment.user_role utr ON u.id = utr.user_id INNER JOIN equipment.role r ON utr.role_id = r.id WHERE u.login = ?",hashAlgorithm=SHA-1,hashEncoding=hex})

/subsystem=elytron/key-store=serverKeyStore:add(credential-reference={clear-text=${KEYSTORE_PASS?}},path=certs/server.jks,relative-to=jboss.server.config.dir,type=JKS)
/subsystem=elytron/key-store=clientTrustStore:add(credential-reference={clear-text=${KEYSTORE_PASS?}},path=certs/users.jks,relative-to=jboss.server.config.dir,type=JKS)
/subsystem=elytron/key-manager=serverKeyManager:add(credential-reference={clear-text=${KEYSTORE_PASS?}},key-store=serverKeyStore)
/subsystem=elytron/trust-manager=clientTrustManager:add(key-store=clientTrustStore)
/subsystem=elytron/server-ssl-context=qsSSLContext:add(protocols=[TLSv1.2],need-client-auth=true,key-manager=serverKeyManager,trust-manager=clientTrustManager)

/interface=public:undefine-attribute(name=inet-address)
/interface=public:write-attribute(name=any-address, value)
/socket-binding-group=standard-sockets/socket-binding=https-certs:add(port=8445)

/subsystem=undertow/server=default-server/https-listener=ssl-certs:add(socket-binding=https-certs,ssl-context=qsSSLContext)

reload
ENDOFSTRING

#sed -i "s/<resolve-parameter-values>false<\/resolve-parameter-values>/<resolve-parameter-values>true<\/resolve-parameter-values>/" $JBOSS_HOME/bin/jboss-cli.xml
$JBOSS_HOME/bin/standalone.sh -Djboss.socket.binding.port-offset=1130 &
sleep 15
$JBOSS_HOME/bin/jboss-cli.sh --controller=localhost:11120 --properties=wildfly.properties --connect --file=./temp/wildfly-script.txt
$JBOSS_HOME/bin/jboss-cli.sh --controller=localhost:11120 --connect --command="shutdown"
