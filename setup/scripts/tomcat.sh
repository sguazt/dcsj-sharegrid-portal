#!/bin/sh

if [-z "$CATALINA_HOME"]; then
	CATALINA_HOME=/opt/apache-tomcat
	export CATALINA_HOME
fi
if [-z "$MGROOT"]; then
	MGROOT=/usr/local/opt/OurGrid/v3/mygrid
	export MGROOT
fi
TZ='GMT'
export TZ

case "$1" in
	start)
		$CATALINA_HOME/bin/startup.sh 
		;;
	stop)
		$CATALINA_HOME/bin/shutdown.sh 
		;;
	restart)
		$CATALINA_HOME/bin/shutdown.sh 
		rm -f $CATALINA_HOME/logs/*
		sleep 5
		$CATALINA_HOME/bin/startup.sh 
		;;
	*)
		echo "Usage $0 {stop|start|restart}"
		exit 1
esac
