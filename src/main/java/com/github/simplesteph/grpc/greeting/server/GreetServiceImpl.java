package com.github.simplesteph.grpc.greeting.server;

import com.proto.greet.*;
import io.grpc.Context;
import io.grpc.stub.StreamObserver;

import java.util.stream.Stream;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {
    // GreetServiceGrpc'den greeti implemente ettim
    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        // Input olarak GreetRequest' i otomatik aldı
        // Bu class void tanımlanmış

        // extract the fields we need
        Greeting greeting = request.getGreeting(); // Request modele constructor oluşturuyoruz
        String firstName = greeting.getFirstName(); // Greeting Requestin içinde First name olduğu için onu String tutuyoruz


        // create the response
        String result = "Hello " + firstName;
        GreetResponse response = GreetResponse.newBuilder()
                .setResult(result)
                .build(); // Response created

        // send the response
        responseObserver.onNext(response);

        // complete the RPC call
        responseObserver.onCompleted(); // Bunu yapmak zorundayız
    }

    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
        String firstName = request.getGreeting().getFirstName(); // Request'den gelen First Name alındı

        try {
            for (int i = 0; i < 10; i++) {
                String result = "Hello " + firstName + ", response number: " + i; // Result'ın içine sırayla basıp
                GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder()// Response'un içine sırayla yedirildi
                        .setResult(result)
                        .build();

                responseObserver.onNext(response); // Response' u gönder
                Thread.sleep(1000L); //
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            responseObserver.onCompleted();
        }
    }

    @Override
    public StreamObserver<LongGreetRequest> longGreet(StreamObserver<LongGreetResponse> responseObserver) {
        //Class'ın Tipi değişti, yukarıdakiler Void idi.
        //
        // we create the requestObserver that we'll return in this function
        StreamObserver<LongGreetRequest> requestObserver = new StreamObserver<LongGreetRequest>() {
            // Alttaki üç Class otomatik geliyor.

            String result = "";

            @Override
            public void onNext(LongGreetRequest value) {
                // client sends a message

                result += "Hello " + value.getGreeting().getFirstName() + "! ";
                System.out.println(result);
            }

            @Override
            public void onError(Throwable t) {
                // client sends an error
            }

            @Override
            public void onCompleted() {
                // client is done
                //Request'leri aldığında çalışacak

                responseObserver.onNext(
                        LongGreetResponse.newBuilder()
                                .setResult(result)
                                .build()
                );
                System.out.println("Response Completed!");
                responseObserver.onCompleted();
            }
        };


        return requestObserver;
    }

    @Override
    public StreamObserver<GreetEveryoneRequest> greetEveryone(StreamObserver<GreetEveryoneResponse> responseObserver) {
        StreamObserver<GreetEveryoneRequest> requestObserver = new StreamObserver<GreetEveryoneRequest>() {
            @Override
            public void onNext(GreetEveryoneRequest value) {
                String result = "Hello " + value.getGreeting().getFirstName();
                GreetEveryoneResponse greetEveryoneResponse = GreetEveryoneResponse.newBuilder()
                        .setResult(result)
                        .build();

                responseObserver.onNext(greetEveryoneResponse);
            }

            @Override
            public void onError(Throwable t) {
                // do nothing
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };

        return requestObserver;
    }

    @Override
    public void greetWithDeadline(GreetWithDeadlineRequest request, StreamObserver<GreetWithDeadlineResponse> responseObserver) {

        Context current = Context.current();

        try {

            for (int i = 0; i < 3; i++) {
                if (!current.isCancelled()) {
                    System.out.println("sleep for 100 ms");
                    Thread.sleep(100);
                } else {
                    return;
                }
            }

            System.out.println("send response");
            responseObserver.onNext(
                    GreetWithDeadlineResponse.newBuilder()
                            .setResult("hello " + request.getGreeting().getFirstName())
                            .build()
            );

            responseObserver.onCompleted();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
