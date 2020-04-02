package org.mvnsearch.boot.npm.export.rsocket;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.jetbrains.annotations.NotNull;
import org.mvnsearch.boot.npm.export.rsocket.generator.PackageJsonGenerator;
import org.mvnsearch.boot.npm.export.rsocket.generator.RSocketServiceJavaScriptStubGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * npm export Controller
 *
 * @author linux_china
 */
@RestController
public class NpmRSocketExportController {
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private Environment env;

    @GetMapping(value = "/npm/{*packageName}", produces = {"application/tar+gzip"})
    public byte[] npmPackage(@PathVariable("packageName") String packageName, ServerWebExchange exchange) throws IOException {
        if (packageName.startsWith("/")) {
            packageName = packageName.substring(1);
        }
        String rsocketServiceName = packageName.substring(packageName.lastIndexOf("/") + 1);
        Object serviceBean = getServiceBean(rsocketServiceName);
        if (serviceBean != null) {
            @NotNull
            MessageMapping messageMapping = serviceBean.getClass().getAnnotation(MessageMapping.class);
            String version = new SimpleDateFormat("yyyy.MM.dd").format(new Date());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(bos);
            TarArchiveOutputStream tgzOut = new TarArchiveOutputStream(gzOut);
            //package.json
            PackageJsonGenerator jsonGenerator = new PackageJsonGenerator(packageName, version);
            jsonGenerator.addContext("description", "npm package to call RSocket " + rsocketServiceName + " from " + env.getProperty("spring.application.name") + " Spring Boot App");
            addBinaryToTarGz(tgzOut, rsocketServiceName + "/package.json", jsonGenerator.generate().getBytes(StandardCharsets.UTF_8));
            //index.js
            RSocketServiceJavaScriptStubGenerator jsGenerator = new RSocketServiceJavaScriptStubGenerator(serviceBean.getClass());
            addBinaryToTarGz(tgzOut, rsocketServiceName + "/index.js", jsGenerator.generate(messageMapping.value()[0]).getBytes(StandardCharsets.UTF_8));
            tgzOut.finish();
            tgzOut.close();
            gzOut.close();
            return bos.toByteArray();
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
            return new byte[]{};
        }
    }

    public Object getServiceBean(String rsocketServiceName) {
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            Object bean = applicationContext.getBean(beanName);
            Class<?> clazz = bean.getClass();
            MessageMapping messageMapping = clazz.getAnnotation(MessageMapping.class);
            if (messageMapping != null && messageMapping.value().length > 0 && messageMapping.value()[0].endsWith("." + rsocketServiceName)) {
                return bean;
            }
        }
        return null;
    }

    public void addBinaryToTarGz(TarArchiveOutputStream tgzOut, String name, byte[] content) throws IOException {
        TarArchiveEntry entry = new TarArchiveEntry(name);
        entry.setSize(content.length);
        tgzOut.putArchiveEntry(entry);
        tgzOut.write(content);
        tgzOut.closeArchiveEntry();
    }
}
