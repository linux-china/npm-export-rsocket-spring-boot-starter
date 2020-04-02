package org.mvnsearch.boot.npm.export.rsocket.generator;

import org.junit.jupiter.api.Test;
import org.mvnsearch.boot.npm.export.demo.Account;

/**
 * JavaToJsTypeConverter Test
 *
 * @author linux_china
 */
public class JavaToJsTypeConverterTest {
    private JavaToJsTypeConverter converter = new JavaToJsTypeConverter() {
    };

    @Test
    public void testToTypeDef() {
        System.out.println(converter.toTypeDef(Account.class));
    }
}
