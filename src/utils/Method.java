package utils;



public enum Method {
    HEAD("HEAD"),
    GET("GET"),
    POST("POST"),
    DELETE("DELETE"),
    OPTIONS("OPTIONS"),
    ;

    private String method;

    Method(String method) {
        this.method = method;
    }

    public String getPhrase() {
        return method;
    }


    public boolean equals(String method) {
        return this.method.equals(method);
    }

    public boolean equals(Method method) {
        return this.method.equals(method.method);
    }

}
