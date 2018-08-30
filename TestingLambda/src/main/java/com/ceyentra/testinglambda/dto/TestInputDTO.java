package com.ceyentra.testinglambda.dto;

/**
 * Created by Athukorala on 30/08/2018.
 */
public class TestInputDTO {

    public String name;

    public TestInputDTO() {
    }

    public TestInputDTO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TestInputDTO{" +
                "name='" + name + '\'' +
                '}';
    }
}
