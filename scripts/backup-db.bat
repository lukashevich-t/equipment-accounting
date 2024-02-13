%echo off

echo Утилита sed.exe взята из комплекта win-bash, в который в свою очередь попала с сайта http://unxutils.sourceforge.net/
echo Версия sed.exe с сайта http://unxutils.sourceforge.net/ на 26.11.2019 заменяла переводы строк, поэтому используем старую версию, найденную в закромах

set curpath=%~dp0%
SET MARIA_CLIENT=%curpath%mariadb-java-client.jar
SET USERNAME=root
SET PASSWORD=123456
SET MYSQL_BIN="c:\Program Files\MariaDB 10.7\bin\mysqldump.exe"
FOR %%d IN ("%curpath%db\metadata") DO ( if not exist %%d ( mkdir %%d ) )

%MYSQL_BIN% --skip-comments --skip-dump-date -d -u%USERNAME% -p%PASSWORD% --add-drop-database --hex-blob --default_character_set=utf8 -Q -R --triggers -B equipment | sed -f sed-script.txt > "%curpath%db/metadata/equipment.sql"

@REM %MYSQL_BIN% --skip-comments -u%USERNAME% -p%PASSWORD% --compact --no-create-info --no-create-db --add-drop-database -x --dump-date --hex-blob --skip-extended-insert --default_character_set=utf8 -Q erip action_type erip_transaction_block_reason erip_transaction_state role tariff> "%curpath%db\refs\erip\other.sql"

pause
