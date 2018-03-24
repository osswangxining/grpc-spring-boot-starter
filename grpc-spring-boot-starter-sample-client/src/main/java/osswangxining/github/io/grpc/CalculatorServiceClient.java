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
