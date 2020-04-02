package org.mvnsearch.boot.npm.export.rsocket.generator;

import io.rsocket.frame.FrameType;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * JS RSocket stub method
 *
 * @author linux_china
 */
public class JsRSocketStubMethod implements JavaToJsTypeConverter {
    private String name;
    private String description;
    private String path;
    private RequestMethod method;
    private List<JsParam> params = new ArrayList<>();
    private Class<?> returnType;
    private boolean resultNullable = false;
    private JSDocTypeDef jsDocTypeDef;
    private boolean deprecated = false;
    private FrameType frameType;
    private boolean monoChannel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public void setMethod(RequestMethod method) {
        this.method = method;
    }

    public List<JsParam> getParams() {
        return params;
    }

    public void setParams(List<JsParam> params) {
        this.params = params;
    }

    public void addParam(JsParam param) {
        params.add(param);
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    public String getJsReturnType() {
        if (this.jsDocTypeDef != null) {
            return jsDocTypeDef.getName();
        } else {
            return toJsType(this.returnType);
        }
    }

    public boolean isResultNullable() {
        return resultNullable;
    }

    public void setResultNullable(boolean resultNullable) {
        this.resultNullable = resultNullable;
    }

    public JSDocTypeDef getJsDocTypeDef() {
        return jsDocTypeDef;
    }

    public void setJsDocTypeDef(JSDocTypeDef jsDocTypeDef) {
        this.jsDocTypeDef = jsDocTypeDef;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public FrameType getFrameType() {
        return frameType;
    }

    public void setFrameType(FrameType frameType) {
        this.frameType = frameType;
    }

    public boolean isMonoChannel() {
        return monoChannel;
    }

    public void setMonoChannel(boolean monoChannel) {
        this.monoChannel = monoChannel;
    }
}
