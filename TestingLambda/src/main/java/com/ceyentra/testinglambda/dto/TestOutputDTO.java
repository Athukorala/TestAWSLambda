package com.ceyentra.testinglambda.dto;

/**
 * Created by Athukorala on 30/08/2018.
 */
public class TestOutputDTO {

    private String msg;
    private String functionName;
    private int memoryLimit;

    public TestOutputDTO() {
    }

    public TestOutputDTO(String msg, String functionName, int memoryLimit) {
        this.msg = msg;
        this.functionName = functionName;
        this.memoryLimit = memoryLimit;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public int getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(int memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    @Override
    public String toString() {
        return "TestOutputDTO{" +
                "msg='" + msg + '\'' +
                ", functionName='" + functionName + '\'' +
                ", memoryLimit=" + memoryLimit +
                '}';
    }
}
