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

## About this project
Redis cache can be used in cache aside pattern using @Cacheable.
But in this project, we have used a more low level redisTemplate, which gives you more control as to how you save data.
This way, you can also set TTL per cahce entry as done in this project.

Files to notice:
1. LocalConfig.java: Redisson client connection to local Redis.
2. AWSConfig.java: AWS config for Redisson client.
3. EmployeeController: Various endpoints for saving, retreiving, deleting cached entries. 
   We also set TTL per entry, which is being returned in the /keys endpoint for reference.

