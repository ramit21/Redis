# Redis
Redis with spring-boot

There are many drivers available that help Java code connect to Redis cluster like Lettuce, Jedis, etc. 
In this project, we use Redisson, with many additional features than Lettuce like supporting transactions
across clustered Redis (redis is available in clustered as wellas non clustered mode).
In this project, we create a Redisson client, Redisson connection factory, and plug the latter in spring provided redisTemplate.
All operations with redis are then performed using redisTemplate.

## Prerequisite - Redis setup on local
There are many ways to setup Redis on local, you can also use docker. Another way is to first setup wsl.
Open Powershell and type wsl for installation command. 
Once installed, type wsl to enter into wsl prompt. Setup will ask for username and password, remember whatever you give.
Then install and start redis using: 
```
sudo apt update
sudo apt install redis-server
redis-server
```
In another powershell window, ping to test redis server:
```
wsl
redis-cli
ping
```

