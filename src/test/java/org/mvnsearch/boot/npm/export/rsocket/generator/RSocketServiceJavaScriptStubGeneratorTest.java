package org.mvnsearch.boot.npm.export.rsocket.generator;

import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.Test;
import org.mvnsearch.boot.npm.export.demo.AccountService;
import org.mvnsearch.boot.npm.export.demo.AccountServiceImpl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

/**
 * RSocketServiceJavaScriptStubGenerator test
 *
 * @author linux_china
 */
public class RSocketServiceJavaScriptStubGeneratorTest {
    private final RSocketServiceJavaScriptStubGenerator generator = new RSocketServiceJavaScriptStubGenerator(AccountServiceImpl.class);

    @Test
    public void testParserController() {
        System.out.println(generator);
    }

    @Test
    public void testGenerateJsModule() throws Exception {
        String jsCode = generator.generate("org.mvnsearch.user.AccountService");
        FileOutputStream fos = new FileOutputStream(new File("src/test/nodejs/demo/AccountService.js"));
        IOUtils.copy(new ByteArrayInputStream(jsCode.getBytes(StandardCharsets.UTF_8)), fos);
        fos.close();
        System.out.println(jsCode);
    }

    @Test
    public void testJsMethodGenerate() throws Exception {
        Method method = AccountService.class.getMethod("findById", Integer.class);
        JsRSocketStubMethod jsMethod = generator.generateMethodStub(method);
        System.out.println(jsMethod);
    }

    @Test
    public void testOutputJsCode() throws Exception {
        Method method = AccountService.class.getMethod("findByNick", String.class);
        JsRSocketStubMethod jsMethod = generator.generateMethodStub(method);
        System.out.println(generator.toJsCode(jsMethod, "  "));
    }
}
