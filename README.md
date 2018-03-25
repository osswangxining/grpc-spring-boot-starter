# grpc-spring-boot-starter
grpc-spring-boot-starter is the Spring Boot starter module based on the underlying gRPC framework.



![](https://raw.githubusercontent.com/osswangxining/myimages/master/wire-mesh-1117741_1920.jpg)
图片来自https://pixabay.com

## Nginx支持gRPC服务集群

(蹭热点)2018年3月17日，NGINIX官方宣布在nginx 1.13.10中将会支持gRPC，这一宣告表示了NGINX已完成对gRPC的原生支持。众所周知，gRPC已经是新一代微服务的事实标准RPC框架。对于实现来说，可以用服务框架等手段来做到负载均衡，但业界其实还没有非常成熟的针对gRPC的反向代理软件。
NGINIX作为老牌负载均衡软件对gRPC进行了支持，之前已经可以代理gRPC的TCP连接，新版本之后，还可以终止、检查和跟踪 gRPC 的方法调用：
- 发布 gRPC 服务，然后使用 NGINX 应用 HTTP/2 TLS 加密、速率限制、基于 IP 的访问控制列表和日志记录；
- 通过单个端点发布多个 gRPC 服务，使用 NGINX 检查并跟踪每个内部服务的调用；
- 使用 Round Robin, Least Connections 或其他方法在集群分配调用，对 gRPC 服务集群进行负载均衡；

## gRPC RPC服务
gRPC是一种远程过程调用协议，用于客户端和服务器应用程序之间的通信, 具有紧凑（节省空间）和可跨多种语言移植的特点，并且支持请求响应和流式交互。 由于其广泛的语言支持和简单的面向用户的设计，该协议越来越受欢迎，其中包括服务网格实现。
![一个简单的基于 gRPC 的应用程序](https://cdn-1.wp.nginx.com/wp-content/uploads/2018/03/grpc-unproxied-cleartext.png)

gRPC 通过 HTTP / 2 传输，无论是明文还是 TLS 加密。 gRPC 调用被实现为具有高效编码主体的 HTTP POST 请求（协议缓冲区是标准编码）。 gRPC 响应使用类似的编码体，并在响应结束时使用 HTTP trailers 发送状态码。
按照设计，gRPC 协议不能通过 HTTP 传输。 gRPC 协议规定使用 HTTP / 2，是为了利用 HTTP / 2 连接的复用和流式传输功能。

## gRPC Spring Boot Starter
[grpc-spring-boot-starter](https://github.com/osswangxining/grpc-spring-boot-starter) 是本人实现的一个整合gRPC服务框架的Spring Boot Starter，以便于使用 Spring Boot 的应用进行自动配置，内嵌 gRPC server。
@TODO: 目前只是实现了server端的autoconfigure，client端其实是类似的实现，读者可以自行尝试。

启动gRPC Spring Boot Starter - sample server：
```sh

 ===================================================
 :: gRPC Spring Boot Starter - sample server :: 1.0
 ===================================================
 ......
 ......
 21:59:53.175 [main] INFO  o.github.io.grpc.GRpcServerRunner - Starting gRPC Server ...
21:59:53.211 [main] INFO  o.github.io.grpc.GRpcServerRunner - 'osswangxining.github.io.grpc.sample.CalculatorService' service has been registered.
21:59:53.215 [main] INFO  o.github.io.grpc.GRpcServerRunner - 'osswangxining.github.io.grpc.sample.GreeterService' service has been registered.
21:59:53.320 [main] INFO  o.github.io.grpc.GRpcServerRunner - gRPC Server started, listening on port 6565.
21:59:53.322 [main] INFO  o.g.io.grpc.sample.SampleApplication - Started SampleApplication in 1.759 seconds (JVM running for 3.121)
```

可以看到，gRPC Server启动，监听端口6565。

原始的client调用方式如下：
```java
package osswangxining.github.io.grpc;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ListenableFuture;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.CalculatorGrpc;
import io.grpc.examples.CalculatorGrpc.CalculatorBlockingStub;
import io.grpc.examples.CalculatorGrpc.CalculatorFutureStub;
import io.grpc.examples.CalculatorGrpc.CalculatorStub;
import io.grpc.examples.CalculatorOuterClass.CalculatorRequest;
import io.grpc.examples.CalculatorOuterClass.CalculatorRequest.OperationType;
import io.grpc.examples.CalculatorOuterClass.CalculatorResponse;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CalculatorServiceClient {
	private final ManagedChannel channel;
	private final CalculatorBlockingStub newBlockingStub;
	private final CalculatorFutureStub newFutureStub;
	private final CalculatorStub newStub;

	public CalculatorServiceClient(String host, int port) {
		this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
	}

	public CalculatorServiceClient(ManagedChannelBuilder<?> channelBuilder) {
		channel = channelBuilder.build();
		newBlockingStub = CalculatorGrpc.newBlockingStub(channel);
		newFutureStub = CalculatorGrpc.newFutureStub(channel);
		newStub = CalculatorGrpc.newStub(channel);
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

	public CalculatorResponse blockingCalculate(double num1, double num2, OperationType operationType) {
		CalculatorRequest request = CalculatorRequest.newBuilder().setNumber1(num1).setNumber2(num2)
				.setOperation(operationType).build();
		return newBlockingStub.calculate(request);
	}

	public ListenableFuture<CalculatorResponse> futureCalculate(double num1, double num2, OperationType operationType) {
		CalculatorRequest request = CalculatorRequest.newBuilder().setNumber1(num1).setNumber2(num2)
				.setOperation(operationType).build();
		return newFutureStub.calculate(request);
	}

	public void calculate(double num1, double num2, OperationType operationType) {
		CalculatorRequest request = CalculatorRequest.newBuilder().setNumber1(num1).setNumber2(num2)
				.setOperation(operationType).build();
		newStub.calculate(request, new StreamObserver<CalculatorResponse>() {

			@Override
			public void onCompleted() {
				log.info("onCompleted");
			}

			@Override
			public void onError(Throwable t) {
				log.info("onError:" + t.getMessage());
			}

			@Override
			public void onNext(CalculatorResponse response) {
				log.info("onNext:" + response.getResult());
			}

		});
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		CalculatorServiceClient client = new CalculatorServiceClient("localhost", 6565);
		double num1 = 1.2;
		double num2 = 2.3;
		OperationType operationType = OperationType.ADD;

		CalculatorResponse response = client.blockingCalculate(num1, num2, operationType);
		log.info("blockingCalculate({}, {}, {}):{}", num1, num2, operationType, response.getResult());

		ListenableFuture<CalculatorResponse> futureCalculate = client.futureCalculate(num1, num2, operationType);
		log.info("futureCalculate({}, {}, {}):{}", num1, num2, operationType, futureCalculate.get().getResult());

		client.calculate(num1, num2, operationType);
	}

}

```

注意，IP地址和端口为"localhost"、6565。示例执行结果如下：
```sh
22:06:33.997 [main] INFO  o.g.io.grpc.CalculatorServiceClient - blockingCalculate(1.2, 2.3, ADD):3.5
22:06:34.006 [main] INFO  o.g.io.grpc.CalculatorServiceClient - futureCalculate(1.2, 2.3, ADD):3.5
```

## 使用NGINX代理gRPC 服务
![NGINX proxying gRPC traffic](https://cdn-1.wp.nginx.com/wp-content/uploads/2018/03/grpc-nginx-cleartext.png)

在客户端和服务器应用程序之间插入 NGINX，为服务器应用程序提供了一个稳定可靠的网关。

### 使用Docker容器搭建NGINX Server
使用NGINX官方提供的docker image搭建server：
```sh
$ docker pull nginx:1.13.10
1.13.10: Pulling from library/nginx
2a72cbf407d6: Pull complete
fefa2faca81f: Pull complete
080aeede8114: Pull complete
Digest: sha256:c4ee0ecb376636258447e1d8effb56c09c75fe7acf756bf7c13efadf38aa0aca
Status: Downloaded newer image for nginx:1.13.10
```

### NGINX gRPC配置
NGINX 使用 HTTP 服务器监听 gRPC 流量，并使用 grpc_pass 指令代理流量。 为 NGINX 创建以下代理配置，在端口 80 上侦听未加密的 gRPC 流量并将请求转发到端口6565上的服务器：

```nginx
server {
    listen  80     http2;

    access_log /var/log/nginx/access.log main;

    location / {
        # Replace with the address and port of your gRPC server
        # The 'grpc://' prefix is optional; unencrypted gRPC is the default
        grpc_pass grpc://192.168.1.3:6565;
    }
}
```

### 启动NGINX容器
```sh
docker run --name mynginx4grpc -p 80:80 -v /Users/xiningwang/tmp4myworkspace:/etc/nginx/conf.d:ro -d nginx:1.13.10
```
其中，上述NGINX配置文件位于 /Users/xiningwang/tmp4myworkspace目录下。


## 修改gRPC客户端
将gRPC客户端的代码修改为相应的IP地址(如果container是在本地，则可以设为localhost)与端口80，如下:
```java
CalculatorServiceClient client = new CalculatorServiceClient("localhost", 80);
```

当运行修改后的客户端时，您会看到与之前相同的响应，但事务将由 NGINX 终止并转发。
具体可以在NGINX配置的访问日志中看到它们：
```text
$ docker logs mynginx4grpc
172.17.0.1 - - [24/Mar/2018:14:26:46 +0000] "POST /Calculator/Calculate HTTP/2.0" 200 14 "-" "grpc-java-netty/1.10.0" "-"
172.17.0.1 - - [24/Mar/2018:14:26:46 +0000] "POST /Calculator/Calculate HTTP/2.0" 200 14 "-" "grpc-java-netty/1.10.0" "-"
172.17.0.1 - - [24/Mar/2018:14:26:46 +0000] "POST /Calculator/Calculate HTTP/2.0" 200 14 "-" "grpc-java-netty/1.10.0" "-"
172.17.0.1 - - [24/Mar/2018:14:27:15 +0000] "POST /Calculator/Calculate HTTP/2.0" 200 14 "-" "grpc-java-netty/1.10.0" "-"
172.17.0.1 - - [24/Mar/2018:14:27:15 +0000] "POST /Calculator/Calculate HTTP/2.0" 200 14 "-" "grpc-java-netty/1.10.0" "-"
```

## NGINX其他关于gRPC的支持
关于加密 gRPC 流量，在 NGINX 配置中，需要更改用于将 gRPC 流量代理到上游服务器的协议：grpcs。

![路由 gRPC 流量](https://cdn-1.wp.nginx.com/wp-content/uploads/2018/03/grpc-nginx-routing.png)


如果有多个 gRPC 服务，每个服务都由不同的服务器应用程序实现。使用 NGINX，您可以识别服务和方法，然后使用位置指令路由流量。 可能你已经推断出每个 gRPC 请求的 URL 是从 proto 规范中的包，服务和方法名称派生的。 如下：

```protobuf

service Calculator {
  // calculate
  rpc Calculate(CalculatorRequest) returns (CalculatorResponse) {}
}

....
"POST /Calculator/Calculate HTTP/2.0" 200
....
```
因此，使用 NGINX 路由流量非常简单：

```nginx
upstream grpcservers {
    server 192.168.1.3:6565;
    server 192.168.1.4:6565;
}

location /Calculator/Calculate {
    grpc_pass grpc://grpcservers;
}

location /Greeter/SayHello {
    grpc_pass grpc://192.168.1.4:6565;
}
```
