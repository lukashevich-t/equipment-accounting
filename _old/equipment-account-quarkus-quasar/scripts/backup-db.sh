#!/bin/bash

mysqldump --skip-dump-date -d -uequipment -pequipment --protocol=TCP -P3306 --add-drop-database --hex-blob --default_character_set=utf8 -Q -R --triggers -B equipment | sed "s/ \(AUTO_INCREMENT\|AVG_ROW_LENGTH\)=[[:digit:]]\+//g" >db/equipment-metadata.sql

mysqldump -uroot -p123456 --protocol=TCP -P3306 --compact --no-create-info --no-create-db --add-drop-database -x --dump-date --hex-blob --default_character_set=utf8 -Q equipment action equipment equipment equipment_state equipment_type responsible_person role user user_role> db/equipment-refs.sql
