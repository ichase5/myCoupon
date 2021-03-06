# myCoupon

微服务优惠券系统



## 开发工具及组件

git，maven, springcloud(nacos,feign,zuul,hystrix), springboot, mysql, redis, kafka



### 注册中心Nacos

nacos伪集群，通过三个端口部署在腾讯云上



### 网关Zuul

通过zuul的filter实现了以下功能：

​	token校验，令牌桶限流，访问时间记录

使用了RoundRobin 负载均衡



### 远程调用

open feign(RoundRobin负载均衡)

hystrix作为兜底fallback

熔断:  open, closed, half-open状态

降级

资源隔离（线程池隔离）



## 数据库表设计

**coupon_template**

自增id作为主键

**索引：**

user_id（运营人员）

category（满减，折扣，立减）

根据user或category查找优惠券

**唯一索引：**

name  不允许同名的coupon template存在



**coupon**

自增id作为主键

template_id作为逻辑外键

**索引：**

template_id

user_id（用户）



## redis的作用

**存储优惠券码**

list键

无过期时间



**存储用户优惠券信息**

hash键： key为coupon id, value为coupon

随机过期时间

查询时key为userId+status， status为usable/used/expired



## kafka的作用

异步化更新优惠券状态（usable, used, expired)，减少响应时间



# 三个微服务

## 模板微服务

面向运营人员使用，生成coupon_template

创建优惠券模板时，**异步的生成不重复的coupon code，保存在redis的list中, 永久有效 **

优惠券模板的过期处理机制

定期清理（spring定时任务）

分发微服务调用时，需要自己根据当前时间校验是否过期

## 分发微服务

领取优惠券时，从redis得到优惠券码

生成coupon, 保存到数据库和redis缓存（哈希建）中。



对于userId+status的coupon查询，有redis缓存，减少访问时间（设置随机过期时间）



缓存穿透： 如果缓存和数据库都没有，则需要缓存空值 （查询时需要过滤这个空值）

缓存雪崩： 设置随机过期时间



过期优惠券的处理：

用户查询可用优惠券时，处理过期优惠券，更新“userId+status”的coupons缓存，同时通过kafka发送消息异步更新mysql中的优惠券状态



feign 调用模板微服务和结算微服务

hystrix兜底



## 结算微服务

不同的优惠券有自己的执行器

如果需要核销（实际支付），核销后，更新“userId+status”的coupons缓存，通过kafka发送消息异步更新mysql中的优惠券状态



## 压测，响应时间

TODO