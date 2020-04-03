package org.mvnsearch.boot.npm.export.rsocket.generator;

import io.rsocket.frame.FrameType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Base generator
 *
 * @author linux_china
 */
public class BaseGenerator {
    protected final Class<?> serviceClassImpl;
    protected final String jsClassName;
    protected final List<Method> requestMethods;
    protected final List<JsRSocketStubMethod> jsHttpStubMethods;
    /**
     * javabean for typeDef from @Schema implementation
     */
    protected final Map<Class<?>, String> javaBeanTypeDefMap = new HashMap<>();
    public BaseGenerator(Class<?> serviceClassImpl) {
        this.serviceClassImpl = serviceClassImpl;
        this.requestMethods = Arrays.stream(this.serviceClassImpl.getMethods())
                .filter(method -> AnnotationUtils.findAnnotation(method, MessageMapping.class) != null)
                .collect(Collectors.toList());
        this.jsHttpStubMethods = this.requestMethods.stream()
                .map(this::generateMethodStub)
                .collect(Collectors.toList());
        this.jsClassName = serviceClassImpl.getSimpleName().replace("Impl", "");
    }

    public JsRSocketStubMethod generateMethodStub(Method method) {
        JsRSocketStubMethod stubMethod = new JsRSocketStubMethod();
        stubMethod.setName(method.getName());
        //@deprecated
        Deprecated deprecated = method.getAnnotation(Deprecated.class);
        if (deprecated != null) {
            stubMethod.setDeprecated(true);
        }
        //parameters
        Parameter[] parameters = method.getParameters();
        if (parameters.length > 0) {
            for (Parameter parameter : parameters) {
                JsParam jsParam = new JsParam();
                jsParam.setName(parameter.getName());
                jsParam.setType(parameter.getType());
                stubMethod.addParam(jsParam);
            }
        }
        //return type
        Type genericReturnType = method.getGenericReturnType();
        stubMethod.setReturnType(parseInferredClass(genericReturnType));
        //frame type
        //bi direction check: param's type is Flux for 1st param or 2nd param
        int paramCount = method.getParameterCount();
        FrameType rsocketFrameType = null;
        if (paramCount == 1 && method.getParameterTypes()[0].equals(Flux.class)) {
            rsocketFrameType = FrameType.REQUEST_CHANNEL;
        } else if (paramCount == 2 && method.getParameterTypes()[1].equals(Flux.class)) {
            rsocketFrameType = FrameType.REQUEST_CHANNEL;
        }
        if (rsocketFrameType == FrameType.REQUEST_CHANNEL) {
            if (method.getReturnType().isAssignableFrom(Mono.class)) {
                stubMethod.setMonoChannel(true);
            }
        }
        if (rsocketFrameType == null) {
            Class<?> returnType = method.getReturnType();
            // fire_and_forget
            if (returnType.equals(Void.TYPE) || (returnType.equals(Mono.class) && stubMethod.getReturnType().equals(Void.TYPE))) {
                rsocketFrameType = FrameType.REQUEST_FNF;
            } else if (returnType.equals(Flux.class)) {  // request/stream
                rsocketFrameType = FrameType.REQUEST_STREAM;
            } else { //request/response
                rsocketFrameType = FrameType.REQUEST_RESPONSE;
            }
        }
        stubMethod.setFrameType(rsocketFrameType);
        return stubMethod;
    }

    public static Class<?> parseInferredClass(Type genericType) {
        Class<?> inferredClass = null;
        if (genericType instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType) genericType;
            Type[] typeArguments = type.getActualTypeArguments();
            if (typeArguments.length > 0) {
                final Type typeArgument = typeArguments[0];
                if (typeArgument instanceof ParameterizedType) {
                    inferredClass = (Class<?>) ((ParameterizedType) typeArgument).getActualTypeArguments()[0];
                } else {
                    inferredClass = (Class<?>) typeArgument;
                }
            }
        }
        if (inferredClass == null && genericType instanceof Class) {
            inferredClass = (Class<?>) genericType;
        }
        return inferredClass;
    }
}
