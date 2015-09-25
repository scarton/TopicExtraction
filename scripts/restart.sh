#!/bin/bash

sudo service tomcat7 stop
sudo rm -rf /home/logs/*
sudo service tomcat7 start
