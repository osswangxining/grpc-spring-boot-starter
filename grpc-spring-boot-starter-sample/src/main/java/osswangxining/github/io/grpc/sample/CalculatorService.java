package osswangxining.github.io.grpc.sample;

import io.grpc.examples.CalculatorGrpc;
import io.grpc.examples.CalculatorOuterClass;
import io.grpc.examples.CalculatorOuterClass.CalculatorResponse;
import lombok.extern.slf4j.Slf4j;
import osswangxining.github.io.grpc.annotation.GRpcService;

@Slf4j
@GRpcService(interceptors = { LogInterceptor.class })
public class CalculatorService extends CalculatorGrpc.CalculatorImplBase {
	/**
	 * <pre>
	 * calculate
	 * </pre>
	 */
	public void calculate(io.grpc.examples.CalculatorOuterClass.CalculatorRequest request,
			io.grpc.stub.StreamObserver<io.grpc.examples.CalculatorOuterClass.CalculatorResponse> responseObserver) {
		CalculatorOuterClass.CalculatorResponse.Builder resultBuilder = CalculatorOuterClass.CalculatorResponse
				.newBuilder();
		switch (request.getOperation()) {
		case ADD:
			resultBuilder.setResult(request.getNumber1() + request.getNumber2());
			break;
		case SUBTRACT:
			resultBuilder.setResult(request.getNumber1() - request.getNumber2());
			break;
		case MULTIPLY:
			resultBuilder.setResult(request.getNumber1() * request.getNumber2());
			break;
		case DIVIDE:
			resultBuilder.setResult(request.getNumber1() / request.getNumber2());
			break;
		case UNRECOGNIZED:
			break;
		}
		CalculatorResponse response = resultBuilder.build();
		log.info(request.getNumber1() + " " + request.getOperation() +" " + request.getNumber2() + "=" + response.getResult());
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
}
