#!/bin/bash

cd /home/steve/projects/trainer
mvn -DskipTests clean package
sudo service tomcat7 stop
sudo rm -rf /home/webapps/trainer*
sudo rm -rf /home/logs/*
sudo rm -rf /var/log/tomcat7/*
sudo cp target/trainer.war /home/webapps
sudo service tomcat7 start
