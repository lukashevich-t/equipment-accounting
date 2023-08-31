%echo off

echo Утилита sed.exe взята из комплекта win-bash, в который в свою очередь попала с сайта http://unxutils.sourceforge.net/
echo Версия sed.exe с сайта http://unxutils.sourceforge.net/ на 26.11.2019 заменяла переводы строк, поэтому используем старую версию, найденную в закромах

"c:\Program Files\MariaDB 10.3\bin\mysqldump.exe" --skip-dump-date -d -uequipment -pequipment --add-drop-database --hex-blob --default_character_set=utf8 -Q -R --triggers -B equipment | sed "s/ \(AUTO_INCREMENT\|AVG_ROW_LENGTH\)=[[:digit:]]\+//g" >db/equipment-metadata.sql

"c:\Program Files\MariaDB 10.3\bin\mysqldump.exe" -uequipment -pequipment --compact --no-create-info --no-create-db --add-drop-database -x --dump-date --hex-blob --default_character_set=utf8 -Q equipment equipment_state equipment_type responsible_person> db/equipment-refs.sql

pause
