package org.mvnsearch.boot.npm.export.rsocket.generator;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TypeScript Declaration generator: index.d.ts
 *
 * @author linux_china
 */
public class TypeScriptDeclarationGenerator extends BaseGenerator implements JavaToJsTypeConverter {

    public TypeScriptDeclarationGenerator(Class<?> controllerClass) {
        super(controllerClass);
    }

    public String generate() {
        String global = "/**\n" +
                " * set Promise RSocket\n" +
                " * @param promiseRSocket Promise RSocket\n" +
                " */\n" +
                "export function setPromiseRSocket(promiseRSocket: Promise<any>): void;\n\n";
        StringBuilder builder = new StringBuilder();
        builder.append(global);
        for (JsRSocketStubMethod stubMethod : jsHttpStubMethods) {
            builder.append(toTypeScriptDeclarationMethod(stubMethod) + "\n\n");
        }
        builder.append(typeScriptClasses());
        return builder.toString();
    }

    public String toTypeScriptDeclarationMethod(JsRSocketStubMethod stubMethod) {
        StringBuilder builder = new StringBuilder();
        builder.append("export function " + stubMethod.getName() + "(");
        if (!stubMethod.getParams().isEmpty()) {
            String paramsDeclare = stubMethod.getParams().stream()
                    .map(param -> param.getName() + ": " + toTsType(param.getJsType()))
                    .collect(Collectors.joining(", "));
            builder.append(paramsDeclare);
        }
        //java bean type
        String jsReturnType = stubMethod.getJsReturnType();
        if (stubMethod.getJsDocTypeDef() == null && jsReturnType.contains("_")) {
            this.javaBeanTypeDefMap.put(stubMethod.getReturnType(), jsReturnType);
        }
        builder.append("): Promise<" + toTsType(stubMethod.getJsReturnType()) + ">;");
        return builder.toString();
    }

    public String typeScriptClasses() {
        StringBuilder builder = new StringBuilder();
        builder.append("//================ TypeScript Class ========================//\n");
        for (Map.Entry<Class<?>, String> entry : javaBeanTypeDefMap.entrySet()) {
            Class<?> clazz = entry.getKey();
            builder.append("declare class " + entry.getValue() + " {\n");
            for (Field field : clazz.getDeclaredFields()) {
                builder.append("  " + field.getName() + ": " + toTsType(toJsType(field.getType()) + "\n"));
            }
            builder.append("}\n\n");
        }
        return builder.toString();
    }
}
