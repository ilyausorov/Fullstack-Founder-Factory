ssh root@159.203.87.152 'bash -s' <<'ENDSSH'
  cd /srv/fffwww/prod/
  
  kill $(cat Fullstack-Founder-Factory-5.2-SNAPSHOT/RUNNING_PID)
  cd Fullstack-Founder-Factory-5.2-SNAPSHOT
  java_opts="-Xms128M -Xmx256M" ./bin/fullstack-founder-factory -Dconfig.file=/srv/fffwww/prod/conf/prod.conf -Dhttp.port=${1:-9002} &
ENDSSH

