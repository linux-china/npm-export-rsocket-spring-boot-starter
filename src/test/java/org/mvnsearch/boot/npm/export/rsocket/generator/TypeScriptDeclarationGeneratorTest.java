package org.mvnsearch.boot.npm.export.rsocket.generator;

import org.junit.jupiter.api.Test;
import org.mvnsearch.boot.npm.export.demo.AccountServiceImpl;

/**
 * TypeScriptDeclarationGenerator test
 *
 * @author linux_china
 */
public class TypeScriptDeclarationGeneratorTest {

    private TypeScriptDeclarationGenerator generator = new TypeScriptDeclarationGenerator(AccountServiceImpl.class);

    @Test
    public void testGenerateTsDeclare() {
        System.out.println(generator.generate());
    }


}
