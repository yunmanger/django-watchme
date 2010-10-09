#!/bin/bash

pwd=$( readlink -f "$( dirname "$BASH_SOURCE" )" )
. $pwd/env.sh
. $pwd/deploy/server.cfg.local
echo $PYTHONPATH

uwsgi_cmd="$uwsgi_bin/uwsgi -s $socket --pythonpath "$PYTHONPATH" -p 4 -M -t 20 -r -C -L -d ../wsgi_watchme.log wsgi"

case $2 in
"start")
if [ $1 != "workday" ]
then
$uwsgi_cmd
fi
sudo $nginx_bin/nginx -c $nginx_conf
;;
"stop")
if [ $1 != "workday" ]
then
ps aux | grep "$uwsgi_cmd" | grep -v grep | awk '{system("kill -9 " $2)}'
fi
sudo $nginx_bin/nginx -s stop -c $nginx_conf
;;
"restart")
bash $0 $1 stop
sleep 1
bash $0 $1 start
;;
*) echo "Usage: ./server.sh <config> {start|stop|restart}"
esac