ssh root@159.203.87.152 'bash -s' <<'ENDSSH'
  cd /srv/fffwww/prod/

  ## do a dump
  mysqldump -u root -proot fffprod > fffprod.sql

kill $(cat fullstack-founder-factory-5.2-SNAPSHOT/RUNNING_PID)
  rm -rf fullstack-founder-factory-5.2-SNAPSHOT/
  unzip -o ~/fffstage.zip -d ./
  cd fullstack-founder-factory-5.2-SNAPSHOT
  java_opts="-Xms128M -Xmx256M" ./bin/fullstack-founder-factory -Dconfig.file=/srv/fffwww/prod/conf/prod.conf -Dhttp.port=${1:-9002} &
ENDSSH
