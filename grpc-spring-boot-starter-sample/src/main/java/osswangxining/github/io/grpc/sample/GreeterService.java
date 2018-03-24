package osswangxining.github.io.grpc.sample;

import io.grpc.examples.GreeterGrpc;
import lombok.extern.slf4j.Slf4j;
import osswangxining.github.io.grpc.annotation.GRpcService;

@Slf4j
@GRpcService(interceptors = { LogInterceptor.class })
public class GreeterService extends GreeterGrpc.GreeterImplBase {
	/**
	 * <pre>
	 * Sends a greeting
	 * </pre>
	 */
	public void sayHello(io.grpc.examples.GreeterOuterClass.HelloRequest request,
			io.grpc.stub.StreamObserver<io.grpc.examples.GreeterOuterClass.HelloReply> responseObserver) {
		String message = "Hello " + request.getName();
		io.grpc.examples.GreeterOuterClass.HelloReply.Builder builder = io.grpc.examples.GreeterOuterClass.HelloReply
				.newBuilder();
		builder.setMessage(message);
		responseObserver.onNext(builder.build());
		responseObserver.onCompleted();
		log.info("returned message:" + message);
	}
}
