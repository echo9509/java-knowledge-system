#!/bin/bash

docker-compose down
rm -rf ./master/data/*
rm -rf ./slave/data/*
docker-compose build
docker-compose up -d

until docker exec mysql_master sh -c 'export MYSQL_PWD=root;mysql -u root'
do
    echo "Waiting for master database connection..."
    sleep 4
done


priv_stmt='GRANT REPLICATION SLAVE ON *.* TO "replication"@"%" IDENTIFIED BY "replication"; FLUSH PRIVILEGES;'
docker exec mysql_master sh -c "export MYSQL_PWD=root;mysql -u root -e '$priv_stmt'"

echo "grant replication success"

until docker-compose exec mysql_slave sh -c 'mysql -u root -p"root" -e ";"'
do
    echo "Waiting for slave database connection..."
    sleep 4
done

docker-ip() {
    docker inspect --format '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' "$@"
}

MS_STATUS=`docker exec mysql_master sh -c 'export MYSQL_PWD=root;mysql -u root -e "SHOW MASTER STATUS"'`
CURRENT_LOG=`echo $MS_STATUS | awk '{print $6}'`
CURRENT_POS=`echo $MS_STATUS | awk '{print $7}'`

start_slave_stmt="CHANGE MASTER TO MASTER_HOST='$(docker-ip mysql_master)',MASTER_USER='replication',MASTER_PASSWORD='replication',MASTER_LOG_FILE='$CURRENT_LOG',MASTER_LOG_POS=$CURRENT_POS; START SLAVE;"
start_slave_cmd='export MYSQL_PWD=root;mysql -u root -e "'
start_slave_cmd+="$start_slave_stmt"
start_slave_cmd+='set global read_only = 1;"'

docker exec mysql_slave sh -c "$start_slave_cmd"

echo "change master success"

docker exec mysql_slave sh -c "export MYSQL_PWD=root;mysql -u root -e 'SHOW SLAVE STATUS \G'"