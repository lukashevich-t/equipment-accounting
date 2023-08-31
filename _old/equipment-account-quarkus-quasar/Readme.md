## Что это
Переделка проекта equipment-account под quarkus + quasar


## Bootstrap
### backend
    mvn io.quarkus.platform:quarkus-maven-plugin:2.11.1.Final:create -DprojectGroupId=by.gto.equipment -DprojectArtifactId=equipment-backend -Dextensions="resteasy,resteasy-jackson,elytron-security-jdbc,jdbc-mariadb,mybatis,kotlin" -DbuildTool=gradle-kotlin-dsl

создать самоподписанный сертификат и сертификат клиента:
```
mkdir -p secure
keytool -genkey -keyalg RSA -alias selfsigned -keystore secure/server-store.jks -storepass password1 -validity 3600 -keysize 2048 -dname "CN=equipment-backend, OU=DIST, O=BTO, L=Minsk, C=BY"

# keytool -genkey -keyalg RSA -alias client1 -keystore secure/client-store.jks -storepass password1 -validity 3600 -keysize 2048 -dname "CN=client1, OU=DIST, O=BTO, L=Minsk, C=BY"

CLIENTPASS=client-password
CLIENTNAME=client
DN="CN=$CLIENTNAME, OU=DIST, O=BTO, L=Minsk, C=BY"
mkdir -p secure/temp
keytool -storepass "$CLIENTPASS" -genkey -alias "$CLIENTNAME" -keystore "secure/temp/$CLIENTNAME.p12" -storetype PKCS12 -validity 3650 -keyalg RSA -dname "$DN"
keytool -export -rfc -storepass "$CLIENTPASS" -alias "$CLIENTNAME" -file "secure/temp/$CLIENTNAME.cer" -keystore "secure/temp/$CLIENTNAME.p12" -storetype PKCS12
keytool -import -v -trustcacerts -alias "$DN" -file "secure/temp/$CLIENTNAME.cer" -keystore secure/client-store.jks -storepass password1
```

перед запуском отладки/разработки запустить файл `secure/set-passwords.sh` примерно такого вида:
```bash
export QUARKUS_HTTP_SSL_CERTIFICATE_KEY_STORE_PASSWORD=password1
export QUARKUS_HTTP_SSL_CERTIFICATE_TRUST_STORE_PASSWORD=password1
```
запускать так: `. ./secure/set-passwords.sh`
Или создать профиль запуска в IDE, присваивающий эти переменные.

### Frontend
```
yarn global add @quasar/cli
yarn create quasar

# Ответы на вопросы:
# √ What would you like to build? » App with Quasar CLI, let's go!
# √ Project folder: ... equipment-frontend
# √ Pick Quasar version: » Quasar v2 (Vue 3 | latest and greatest)
# √ Pick script type: » Typescript
# √ Pick Quasar App CLI variant: » Quasar App CLI with Vite
# √ Package name: ... equipment-frontend
# √ Project product name: (must start with letter if building mobile apps) ... Equipment frontend
# √ Project description: ... A Quasar Project
# √ Author: ... Tim Lukashevich <silvernet@yandex.ru>
# √ Pick a Vue component style: » Composition API
# √ Pick your CSS preprocessor: » Sass with SCSS syntax
# √ Check the features needed for your project: » ESLint, State Management (Pinia), Axios
# √ Pick an ESLint preset: » Prettier

cd equipment-frontend
quasar dev
```