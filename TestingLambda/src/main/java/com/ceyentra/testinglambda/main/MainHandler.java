package com.ceyentra.testinglambda.main;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.ceyentra.testinglambda.dto.TestInputDTO;
import com.ceyentra.testinglambda.dto.TestOutputDTO;

/**
 * Created by Athukorala on 30/08/2018.
 */
public class MainHandler implements RequestHandler<TestInputDTO ,TestOutputDTO>{

    @Override
    public TestOutputDTO handleRequest(TestInputDTO testInputDTO, Context context) {
        TestOutputDTO dto=new TestOutputDTO();
        dto.setMsg(testInputDTO.getName());
        dto.setFunctionName(context.getFunctionName());
        dto.setMemoryLimit(context.getMemoryLimitInMB());

        context.getLogger().log(testInputDTO.getName()+" calling...");

        return dto;
    }
}
