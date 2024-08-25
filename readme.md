# Что это
Очередная попытка сделать бэкенд, фронтенд и мобильное приложение для учета материальных ценностей.
# Используемый стек технологий:
  - Backend:
    - Java 17
    - gradle/groovy v8.1
    - Spring boot 3.1.3
    - Hibernate
  - Frontend:
    - Vue 3
    - Quasar framework
  - Мобильное приложение:
    - ?
    - ?
    - 

# Требования
  - Аутентификация по паролю, Oauth или сертификату SSL
  - ?

# Создание сертификатов
[взято отсюда](https://www.baeldung.com/x-509-authentication-in-spring-security)

Создать самоподписанный сертификат для root Certification Authority:
```
cd ./certs
set PATH=%PATH%;d:\Program Files\git\usr\bin
openssl req -x509 -sha256 -days 3650 -newkey rsa:4096 -keyout rootCA.key -out rootCA.crt -subj '/L=Minsk/O=BTO/OU=DIST/CN=rootca/C=BY'

```
Посмотреть информацию о созданном сертификате:
```
openssl x509 -in rootCA.crt -text -noout
```

Создать запрос на подписание сертификата:
```
openssl req -new -newkey rsa:4096 -keyout localhost.key -out localhost.csr -subj '/L=Minsk/O=BTO/OU=DIST/CN=localhost/C=BY'
```

Создать файлик `localhost.ext` с настройками:
```
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
subjectAltName = @alt_names
[alt_names]
DNS.1 = localhost
```

Подписать сертификат с использованием этих настроек:
```
openssl x509 -req -CA rootCA.crt -CAkey rootCA.key -in localhost.csr -out localhost.crt -days 365 -CAcreateserial -extfile localhost.ext
```

Создать keystore и положить туда свежеподписанный сертификат:
```
openssl pkcs12 -export -out localhost.p12 -name "localhost" -inkey localhost.key -in localhost.crt
keytool -importkeystore -srckeystore localhost.p12 -srcstoretype PKCS12 -destkeystore keystore.jks -deststoretype JKS
```

Создать truststore и положить туда сертификат root CA (чисто для тестов, в проде так делать НЕЛЬЗЯ):
```
keytool -import -trustcacerts -noprompt -alias ca -ext san=dns:localhost,ip:127.0.0.1 -file rootCA.crt -keystore truststore.jks
```

Для команд openssl везде использовался пароль 1234, а для keytool - 123456.

# Полезные команды
## для backend
### Запуск
```
gradlew bootRun
```
### привести БД к актуальному виду
`gradlew flywayMigrate`
Перед этим может понадобиться очистка БД:
```
gradlew flywayClean -Dflyway.cleanDisabled=false
```