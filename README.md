# Redis
Redis (cache and streams) with spring-boot on local redis, and on AWS ElasticCache. 

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

## Prerequisite - Redis setup on AWS
To run against AWS ElasticCache, you need to first create the AWS ElasticCache,
and set the ElasticCache password in AWS SecretManager.

When creating elastic cache cluster, take t3.micro instance, disable multi az, 
no. of nodes = 1, failover = no etc. to keep cost low for poc.
Select SSL as well as cluster mode enabled.
Access control = select AUTH default user, and set the AUTH token.
Do select security group that allows access to machine on which this code is being run on.
Give cluster name = app-cache-cluster (as present in application.properties)

Next got to AWS secret manager, and create a secret with name /secret/application-cache, 
and secret of type key-value pair, with key = redis_password, 
and value = the auth token entered when creating the cluster.

AWSConfig section below speaks about how code will retrieve the secret and then connect to ElastiCache.

## About this project
Redis cache can be used in cache aside pattern using @Cacheable.
But in this project, we have used a more low level redisTemplate, which gives you more control as to how you save data.
This way, you can also set TTL per cahce entry as done in this project.
Other than using Redis Cache, we also use Redis Streams.
We put events into streams, which other applications can listen to (similar to Kafka), using counsumer groups and StreamMessageListenerContainer.
Or you can have logic in your application to check all events for a given key, 
before allowing any operations etc.
You can further use Redis Transactions to ensure that duplicate events don't happen for a given key.

Postman collection with all endpoints configred is also checked in.

Files to notice:
1. LocalConfig.java: Redisson client connection to local Redis.
2. AWSConfig.java: AWS config for Redisson client. 
   To point to AWS elastic cache, update application.properties 
   set cache.redis.use.local=false, AWS region, and elasticcache replication group id, secret id etc.
   The code first fetches the password value from AWS Secret Manager, 
   and then tries to connect to the create the Redis client using that password.
   The AWS account configured on the machine running this code should have relevant AWS permissions 
   including sts assume role which is required when fetching secrets from secret manager.
   Also note the "rediss://" in connection string, with an extra s for SSL connection with Elasticcache.
3. EmployeeController: Various endpoints for saving, retreiving, deleting cached entries. 
   We also set TTL per entry, which is being returned in the /keys endpoint for reference.
   After saving an employee, if you repeatedly hit cache /keys endpoint, you can see the TTL fall, 
   and after it reaches 0, the entry is removed from cache.
4. EventController: add/retrieve events from Redis Streams. 
   Redis streams don't have an expiry, 
   so we use trim function to delete old records from strim for the given size.

   

