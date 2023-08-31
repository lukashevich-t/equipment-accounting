%echo off

echo �⨫�� sed.exe ���� �� �������� win-bash, � ����� � ᢮� ��।� ������ � ᠩ� http://unxutils.sourceforge.net/
echo ����� sed.exe � ᠩ� http://unxutils.sourceforge.net/ �� 26.11.2019 �����﫠 ��ॢ��� ��ப, ���⮬� �ᯮ��㥬 ����� �����, ��������� � ���஬��

"c:\Program Files\MariaDB 10.3\bin\mysqldump.exe" --skip-dump-date -d -uequipment -pequipment --add-drop-database --hex-blob --default_character_set=utf8 -Q -R --triggers -B equipment | sed "s/ \(AUTO_INCREMENT\|AVG_ROW_LENGTH\)=[[:digit:]]\+//g" >db/equipment-metadata.sql

"c:\Program Files\MariaDB 10.3\bin\mysqldump.exe" -uequipment -pequipment --compact --no-create-info --no-create-db --add-drop-database -x --dump-date --hex-blob --default_character_set=utf8 -Q equipment equipment_state equipment_type responsible_person> db/equipment-refs.sql

pause
