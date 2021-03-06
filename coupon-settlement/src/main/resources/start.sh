#for example
java -Xms128m -Xmx256m -XX:+UseG1GC -jar -Dlogging.file=/root/settlement.log coupon-settlement-1.0-SNAPSHOT.jar &

#观察日志
tail -f settlement.log
