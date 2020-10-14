package com.github.simplesteph.grpc.greeting.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.File;
import java.io.IOException;

public class GreetingServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Hello gRPC");

        // plaintext server
        Server server = ServerBuilder.forPort(50051)
                .addService(new GreetServiceImpl()) // Service Impl'yi yaptıkdan sonra bunu porta ekliyoruz
                .build(); // Opening Server

        // secure server
//        Server server = ServerBuilder.forPort(50051)
//                .addService(new GreetServiceImpl())
//                .useTransportSecurity(
//                        new File("ssl/server.crt"),
//                        new File("ssl/server.pem")
//                )
//                .build();


        server.start(); // start the server

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received Shutdown Request");
            server.shutdown(); // Shut down
            System.out.println("Successfully stopped the server");
        }));

        server.awaitTermination();
    }

}
