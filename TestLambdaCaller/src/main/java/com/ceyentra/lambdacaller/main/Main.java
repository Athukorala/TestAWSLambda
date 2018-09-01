package com.ceyentra.lambdacaller.main;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaAsyncClient;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.ceyentra.testinglambda.dto.TestInputDTO;
import com.ceyentra.testinglambda.dto.TestOutputDTO;
import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args){
        String name= "AWS Athukorala";
        String region = "us-east-2";
        if(args.length > 0){
            name = args[0];
        }

        if(args.length > 1){
            region = args[1];
        }

        Gson gson = new Gson();
        AWSLambdaAsyncClient client = new AWSLambdaAsyncClient(new ProfileCredentialsProvider()).withRegion(Regions.fromName(region));
        TestInputDTO dto= new TestInputDTO();
        dto.setName(name);
        InvokeRequest invokeRequest=new InvokeRequest().withFunctionName("testLambdaFunc").withPayload(gson.toJson(dto));
        InvokeResult result= client.invoke(invokeRequest);
        System.out.println(client);
        System.out.println(invokeRequest);

        String s = StandardCharsets.UTF_8.decode(result.getPayload()).toString();
        TestOutputDTO outputDTO = gson.fromJson(s, TestOutputDTO.class);

        System.out.println("(1) Message: "+ outputDTO.getMsg());
        System.out.println("(2) FuncName: "+ outputDTO.getFunctionName());
        System.out.println("(3) Memory: "+ outputDTO.getMemoryLimit());
    }
}
