参考Dubbo的RPC框架，使用Vert.x作为服务器， Etcd作为注册中心。

此外也支持zk/redis注册中心，支持spring-boot-starter的注解驱动，实现了多种重试机制和容错机制，传输协议为基于TCP的自定义协议，可选择jdk/json/kryo/hessian方式进行(反)序列化
