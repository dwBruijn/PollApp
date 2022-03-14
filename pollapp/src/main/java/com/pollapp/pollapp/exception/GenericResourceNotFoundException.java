package com.pollapp.pollapp.exception;

public class GenericResourceNotFoundException extends RuntimeException {
    private String resource;
    private String name;
    private Object value;

    public GenericResourceNotFoundException(String resource, String name, Object value) {
        super(resource + " " + name + " = " + String.valueOf(value) + " not found.");

        this.resource = resource;
        this.name = name;
        this.value = value;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
