package com.springboot.dubbo.example.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public interface ISayHelloService {
    @GET
    @Path("say")
    String sayHell();
}
