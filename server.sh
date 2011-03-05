#!/bin/bash
pwd=$( readlink -f "$( dirname "$BASH_SOURCE" )" )

name=wm
PYTHONPATH=$pwd/apps:$pwd/..:/opt/envs/$name/lib/python2.5/site-packages:/opt/envs/$name/src/django-trunk
nginx=/opt/nginx
uwsgi_bin=$nginx/sbin
socket=$nginx/sock/$name.$1.sock
nginx_vhosts=$nginx/vhosts
log_dir=$nginx/logs

env_local=$pwd/env_local.sh

if [ -e $env_local ]
then
. $env_local
fi

uwsgi_cmd="$uwsgi_bin/uwsgi -s $socket --env PYTHONPATH=$PYTHONPATH -p 4 -M -t 20 -r -C -L -d $log_dir/wsgi_$name.log -w wsgi"
procid=$(ps -ef|grep "nginx: master process" | grep -v grep | awk '{print $2}')

case $2 in
"start")
bash $0 $1 deploy
if [ $1 != "workday" ]
then
$uwsgi_cmd
fi
sudo kill -HUP $procid
;;
"stop")
bash $0 $1 undeploy
if [ $1 != "workday" ]
then
ps aux | grep "$uwsgi_cmd" | grep -v grep | awk '{system("kill -9 " $2)}'
fi
sudo kill -HUP $procid
;;
"restart")
bash $0 $1 stop
sleep 1
bash $0 $1 start
;;
"deploy")
cp $pwd/deploy/$1/nginx.conf $nginx_vhosts/$name.kz.conf
cp $pwd/deploy/$1/nginx.media $nginx_vhosts/$name.kz.media
;;
"undeploy")
rm $nginx_vhosts/$name.kz.conf
rm $nginx_vhosts/$name.kz.media
;;
*) echo "Usage: ./server.sh <config> {start|stop|restart}"
esac