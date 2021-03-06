package org.mvnsearch.boot.npm.export.rsocket.generator;

import org.intellij.lang.annotations.Language;

import java.util.HashMap;
import java.util.Map;

/**
 * package.json generator
 *
 * @author linux_china
 */
public class PackageJsonGenerator {
    @Language("JSON")
    private String templateText = "{\n" +
            "  \"name\": \"$packageName\",\n" +
            "  \"version\": \"$version\",\n" +
            "  \"main\": \"index.js\",\n" +
            "  \"types\": \"index.d.ts\",\n" +
            "  \"description\": \"$description\",\n" +
            "  \"dependencies\": {\n" +
            "    \"fbjs\": \"^1.0.0\",\n" +
            "    \"rsocket-core\": \"0.0.19\",\n" +
            "    \"rsocket-flowable\": \"^0.0.14\",\n" +
            "    \"rsocket-types\": \"^0.0.16\",\n" +
            "    \"rsocket-websocket-client\": \"0.0.19\",\n" +
            "    \"rxjs\": \"^6.5.4\"\n" +
            "  }\n" +
            "}\n";
    private Map<String, String> context = new HashMap<>();

    public PackageJsonGenerator(String packageName, String version) {
        this.context.put("packageName", packageName);
        this.context.put("version", version);
    }

    public PackageJsonGenerator addContext(String key, String value) {
        this.context.put(key, value);
        return this;
    }

    public String generate() {
        String result = templateText;
        for (Map.Entry<String, String> entry : context.entrySet()) {
            result = result.replace("$" + entry.getKey(), entry.getValue());
        }
        return result;
    }
}
