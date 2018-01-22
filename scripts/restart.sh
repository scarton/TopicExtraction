#!/bin/bash

sudo service tomcat7 stop
sudo rm -rf /home/logs/*
sudo rm -rf /var/log/tomcat7/*
sudo service tomcat7 start
