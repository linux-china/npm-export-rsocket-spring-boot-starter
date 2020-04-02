package org.mvnsearch.boot.npm.export.rsocket.generator;

import io.rsocket.frame.FrameType;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.RepeatableContainers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.ValueConstants;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * RSocket Service JavaScript Stub generator
 *
 * @author linux_china
 */
@SuppressWarnings("StringConcatenationInsideStringBufferAppend")
public class RSocketServiceJavaScriptStubGenerator implements JavaToJsTypeConverter {
    private final Class<?> serviceClassImpl;
    private final String jsClassName;
    private final List<Method> requestMethods;
    private final List<JsRSocketStubMethod> jsHttpStubMethods;
    /**
     * javabean for typeDef from @Schema implementation
     */
    private final Map<Class<?>, String> javaBeanTypeDefMap = new HashMap<>();
    /**
     * customized typedef from @Schema properties
     */
    private Map<String, JSDocTypeDef> customizedTypeDefMap = new HashMap<>();
    private String basePath;

    public RSocketServiceJavaScriptStubGenerator(Class<?> serviceClassImpl) {
        this.serviceClassImpl = serviceClassImpl;
        this.requestMethods = Arrays.stream(this.serviceClassImpl.getMethods())
                .filter(method -> AnnotationUtils.findAnnotation(method, MessageMapping.class) != null)
                .collect(Collectors.toList());
        this.jsHttpStubMethods = this.requestMethods.stream()
                .map(this::generateMethodStub)
                .collect(Collectors.toList());
        this.jsClassName = serviceClassImpl.getSimpleName().replace("Impl", "");
    }

    public String generate(String serviceName) {
        @Language("JavaScript")
        String global = "// Don't edit this file because it was generated by Spring Boot App!!!\n" +
                "const {Observable} = require('rxjs');\n" +
                "const {Flowable} =  require('rsocket-flowable');\n" +
                "const {encodeAndAddWellKnownMetadata, MESSAGE_RSOCKET_ROUTING} = require('rsocket-core');\n" +
                "const {ReactiveSocket} = require('rsocket-types')\n" +
                "\n" +
                "//const murmurhash3Seed = 104729;\n" +
                "const maxRSocketRequestN = 2147483647;\n" +
                "\n" +
                "function requestMetadata(route) {\n" +
                "    return encodeAndAddWellKnownMetadata(\n" +
                "            Buffer.alloc(0),\n" +
                "            MESSAGE_RSOCKET_ROUTING,\n" +
                "            Buffer.from(String.fromCharCode(route.length) + route));\n" +
                "}\n" +
                "\n" +
                "/**\n" +
                " *  convert param to json Buffer\n" +
                " * @param {Object|number|null} param\n" +
                " * @return {Buffer|null}\n" +
                " */\n" +
                "function toJsonBuffer(param) {\n" +
                "    if (param == null) {\n" +
                "        return null;\n" +
                "    } else if (typeof param == 'number') {\n" +
                "        return Buffer.from('' + param);\n" +
                "    } else if (typeof param == 'string') {\n" +
                "        return Buffer.from(param);\n" +
                "    } else {\n" +
                "        return Buffer.from(JSON.stringify(param))\n" +
                "    }\n" +
                "}\n\n";
        @Language(value = "JavaScript", suffix = "}")
        String classDeclare = "/**\n" +
                " * @version $version\n" +
                " */\n" +
                "class XxxxService {\n" +
                "    constructor() {\n" +
                "        this.serviceName = '$serviceName';\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * rsocket requestResponse\n" +
                "     * @param {string} methodName\n" +
                "     * @param {Object|number|null} [param]\n" +
                "     * @return {Promise<Object>}\n" +
                "     */\n" +
                "    rsocketRequestResponse(methodName, param) {\n" +
                "        return this.promiseRSocket.then(rsocket => {\n" +
                "            return new Promise((resolve, reject) => {\n" +
                "                rsocket.requestResponse({\n" +
                "                    data: toJsonBuffer(param),\n" +
                "                    metadata: requestMetadata(this.serviceName + \".\" + methodName)\n" +
                "                }).subscribe({\n" +
                "                    onComplete: (value) => resolve(JSON.parse(value.data.toString())),\n" +
                "                    onError: error => reject(error)\n" +
                "                });\n" +
                "            });\n" +
                "        })\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * rsocket fireAndForget\n" +
                "     * @param {string} methodName\n" +
                "     * @param {Object|number|null} [param]\n" +
                "     * @return {Promise<boolean>}\n" +
                "     */\n" +
                "    rsocketFireAndForget(methodName, param) {\n" +
                "        return this.promiseRSocket.then(rsocket => {\n" +
                "            return new Promise((resolve, reject) => {\n" +
                "                rsocket.fireAndForget({\n" +
                "                    data: toJsonBuffer(param),\n" +
                "                    metadata: requestMetadata(this.serviceName + \".\" + methodName)\n" +
                "                }).subscribe({\n" +
                "                    onComplete: (value) => {\n" +
                "                        resolve(true)\n" +
                "                    },\n" +
                "                    onError: error => reject(error)\n" +
                "                });\n" +
                "            });\n" +
                "        })\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * rsocket requestStream\n" +
                "     * @param {string} methodName\n" +
                "     * @param {Object|number|null} [param]\n" +
                "     * @return {Observable<Object>}\n" +
                "     */\n" +
                "    rsocketRequestStream(methodName, param) {\n" +
                "        return new Observable(subscriber => {\n" +
                "            this.promiseRSocket.then(rsocket => {\n" +
                "                rsocket.requestStream({\n" +
                "                    data: toJsonBuffer(param),\n" +
                "                    metadata: requestMetadata(this.serviceName + \".\" + methodName)\n" +
                "                }).subscribe({\n" +
                "                    onComplete: () => subscriber.complete(),\n" +
                "                    onError: error => subscriber.error(error),\n" +
                "                    onNext: value => subscriber.next(JSON.parse(value.data.toString())),\n" +
                "                    onSubscribe: sub => sub.request(maxRSocketRequestN)\n" +
                "                });\n" +
                "            });\n" +
                "        })\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * rsocket requestChannel\n" +
                "     * @param {string} methodName\n" +
                "     * @param {Array|Observable|Flowable} [param]\n" +
                "     * @return {Observable<Object>}\n" +
                "     */\n" +
                "    rsocketRequestChannel(methodName, param) {\n" +
                "        let fluxData = null;\n" +
                "        if (Array.isArray(param)) {\n" +
                "            fluxData = Flowable.just(param);\n" +
                "        } else if (param instanceof Observable) {\n" +
                "            fluxData = new Flowable(subscriber => {\n" +
                "                param.subscribe({\n" +
                "                    next(x) {\n" +
                "                        subscriber.onNext(x);\n" +
                "                    },\n" +
                "                    error(err) {\n" +
                "                        subscriber.one(err);\n" +
                "                    },\n" +
                "                    complete() {\n" +
                "                        subscriber.onComplete();\n" +
                "                    }\n" +
                "                });\n" +
                "            });\n" +
                "        } else if (param instanceof Flowable) {\n" +
                "            fluxData = param;\n" +
                "        }\n" +
                "        return new Observable(subscriber => {\n" +
                "            this.promiseRSocket.then(rsocket => {\n" +
                "                rsocket.requestChannel(fluxData.map(data => {\n" +
                "                    return {\n" +
                "                        data: toJsonBuffer(data),\n" +
                "                        metadata: requestMetadata(this.serviceName + \".\" + methodName)\n" +
                "                    }\n" +
                "                })).subscribe({\n" +
                "                    onComplete: () => subscriber.complete(),\n" +
                "                    onError: error => subscriber.error(error),\n" +
                "                    onNext: value => subscriber.next(JSON.parse(value.data.toString())),\n" +
                "                    onSubscribe: sub => sub.request(maxRSocketRequestN)\n" +
                "                });\n" +
                "            });\n" +
                "        })\n" +
                "    }\n" +
                "\n" +
                "    /**\n" +
                "     * set rsocket\n" +
                "     * @param {Promise<ReactiveSocket>} promiseRSocket\n" +
                "     * @returns {AccountService}\n" +
                "     */\n" +
                "    setPromiseRSocket(promiseRSocket) {\n" +
                "        this.promiseRSocket = promiseRSocket;\n" +
                "        return this;\n" +
                "    }\n";
        StringBuilder builder = new StringBuilder();
        builder.append(global);
        String version = new SimpleDateFormat("yyyy.MM.dd").format(new Date());
        String newClassDeclare = classDeclare
                .replaceAll("XxxxService", jsClassName)
                .replace("$version", version)
                .replace("$serviceName", serviceName);
        builder.append(newClassDeclare);
        for (JsRSocketStubMethod jsHttpStubMethod : jsHttpStubMethods) {
            builder.append(toJsCode(jsHttpStubMethod, "    ") + "\n");
        }
        builder.append("}\n\n");
        builder.append("module.exports = new " + jsClassName + "();\n\n");
        builder.append(typedefs());
        return builder.toString();
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

    public String toJsCode(JsRSocketStubMethod stubMethod, String indent) {
        StringBuilder builder = new StringBuilder();
        builder.append(indent).append("/**\n");
        //description
        if (stubMethod.getDescription() != null && !stubMethod.getDescription().isEmpty()) {
            builder.append(indent).append("* " + stubMethod.getDescription() + "\n");
        } else {
            builder.append(indent).append("*\n");
        }
        //@deprecated
        if (stubMethod.isDeprecated()) {
            builder.append(indent).append("* @deprecated\n");
        }
        for (JsParam param : stubMethod.getParams()) {
            if (param.isRequired()) {
                builder.append(indent).append("* @param {" + param.getJsType() + "} " + param.getName() + "\n");
            } else {
                //default value
                if (param.getDefaultValue() != null && !param.getDefaultValue().isEmpty() && !param.getDefaultValue().equals(ValueConstants.DEFAULT_NONE)) {
                    builder.append(indent).append("* @param {" + param.getJsType() + "} [" + param.getName() + "=" + param.getDefaultValue() + "]\n");
                } else {  //optional
                    builder.append(indent).append("* @param {" + param.getJsType() + "} [" + param.getName() + "]\n");
                }
            }
            JSDocTypeDef jsDocTypeDef = param.getJsDocTypeDef();
            if (jsDocTypeDef != null) {
                this.customizedTypeDefMap.put(jsDocTypeDef.getName(), jsDocTypeDef);
            }
        }
        String jsReturnType = stubMethod.getJsReturnType();
        if (stubMethod.getJsDocTypeDef() == null && jsReturnType.contains("_")) {
            this.javaBeanTypeDefMap.put(stubMethod.getReturnType(), jsReturnType);
        }
        if (stubMethod.isResultNullable()) {
            jsReturnType = "(" + jsReturnType + "|null)";
        }
        if (stubMethod.getFrameType() == FrameType.REQUEST_RESPONSE) {
            builder.append(indent).append("* @return {Promise<" + jsReturnType + ">}\n");
        } else if (stubMethod.getFrameType() == FrameType.REQUEST_FNF) {
            builder.append(indent).append("* @return {Promise<boolean>}\n");
        } else {
            builder.append(indent).append("* @return {Observable<" + jsReturnType + ">}\n");
        }
        builder.append(indent).append("*/\n");
        builder.append(indent).append(stubMethod.getName() + "(");
        String paramsDeclare = "";
        if (!stubMethod.getParams().isEmpty()) {
            paramsDeclare = stubMethod.getParams().stream()
                    .map(JsParam::getName)
                    .collect(Collectors.joining(", "));
            builder.append(paramsDeclare);
        }
        builder.append(") {\n");
        String requestParams = "";
        if (!paramsDeclare.equals("")) {
            requestParams = "," + paramsDeclare;
        }
        if (stubMethod.getFrameType() == FrameType.REQUEST_CHANNEL) {
            builder.append(indent).append("  return this.rsocketRequestResponse('" + stubMethod.getName() + "'" + requestParams + ");\n");
        } else if (stubMethod.getFrameType() == FrameType.REQUEST_STREAM) {
            builder.append(indent).append("  return this.rsocketRequestStream('" + stubMethod.getName() + "'" + requestParams + ");\n");
        } else if (stubMethod.getFrameType() == FrameType.REQUEST_FNF) {
            builder.append(indent).append("  return this.rsocketFireAndForget('" + stubMethod.getName() + "'" + requestParams + ");\n");
        } else {
            builder.append(indent).append("  return this.rsocketRequestChannel('" + stubMethod.getName() + "'" + requestParams + ");\n");
        }
        builder.append(indent).append("}\n");
        return builder.toString();
    }

    public String typedefs() {
        StringBuilder builder = new StringBuilder();
        builder.append("//================ JSDoc typedef ========================//\n");
        for (Map.Entry<Class<?>, String> entry : javaBeanTypeDefMap.entrySet()) {
            Class<?> clazz = entry.getKey();
            builder.append("/**\n");
            builder.append("* @typedef {Object} " + entry.getValue() + "\n");
            for (Field field : clazz.getDeclaredFields()) {
                builder.append("* @property {" + toJsType(field.getType()) + "} " + field.getName() + "\n");
            }
            builder.append("*/\n");
        }
        //@typeDef for return type and parameter type
        Map<String, JSDocTypeDef> allTypeDefMap = new HashMap<>(this.customizedTypeDefMap);
        Map<String, JSDocTypeDef> typeDefForReturnTypeMap = jsHttpStubMethods.stream()
                .map(JsRSocketStubMethod::getJsDocTypeDef)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(JSDocTypeDef::getName, jsDocTypeDef -> jsDocTypeDef, (a, b) -> b));
        allTypeDefMap.putAll(typeDefForReturnTypeMap);
        for (JSDocTypeDef jsDocTypeDef : allTypeDefMap.values()) {
            builder.append("/**\n");
            builder.append("* @typedef {Object} " + jsDocTypeDef.getName() + "\n");
            for (String property : jsDocTypeDef.getProperties()) {
                builder.append("* @property " + property + "\n");
            }
            builder.append("*/\n");
        }
        return builder.toString();
    }

    @Nullable
    public static <A extends Annotation> A findAnnotationWithAttributesMerged(AnnotatedElement element, Class<A> annotationType) {
        A annotation = AnnotationUtils.findAnnotation(element, annotationType);
        if (annotation != null) {
            annotation = MergedAnnotations.from(element, MergedAnnotations.SearchStrategy.INHERITED_ANNOTATIONS, RepeatableContainers.none())
                    .get(annotationType)
                    .synthesize(MergedAnnotation::isPresent).orElse(null);
        }
        return annotation;
    }

}
