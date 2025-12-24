package com.yohan.javabasic.java.protobuf;

import com.yohan.javabasic.java.protobuf.hello.HelloReply;
import com.yohan.javabasic.java.protobuf.hello.HelloRequest;
import io.grpc.stub.StreamObserver;

class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

  @Override
  public void say(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
    HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
    responseObserver.onNext(reply);
    responseObserver.onCompleted();
  }
}