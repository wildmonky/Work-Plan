package com.zhuofan.oms.config;

import org.apache.commons.lang3.StringUtils;
import org.jodconverter.boot.autoconfigure.JodConverterLocalProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.jodconverter.local.office.LocalOfficeUtils;
import org.jodconverter.local.process.ProcessManager;
import org.springframework.lang.NonNull;

/** {@link EnableAutoConfiguration Auto-configuration} for JodConverter local module. */
@Configuration
@ConditionalOnClass(LocalConverter.class)
@ConditionalOnProperty(prefix = "jodconverter.local", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(JodConverterLocalProperties.class)
public class JodConverterLocalAutoConfiguration {

//    private static final String DEFAULT_FORMATS_PATH = "classpath:document-formats.json";
//    private static final String CUSTOM_FORMATS_PATH = "classpath:custom-document-formats.json";
//    private static final Logger LOGGER =
//            LoggerFactory.getLogger(JodConverterLocalAutoConfiguration.class);

    private final JodConverterLocalProperties properties;

    /**
     * Creates the local autoconfiguration.
     *
     * @param properties The local properties.
     */
    public JodConverterLocalAutoConfiguration(final @NonNull JodConverterLocalProperties properties) {
        this.properties = properties;
    }

    // Creates the OfficeManager bean.
    private LocalOfficeManager createOfficeManager(final ProcessManager processManager) {

        final LocalOfficeManager.Builder builder =
                LocalOfficeManager.builder()
                        .officeHome(properties.getOfficeHome())
                        .workingDir(properties.getWorkingDir())
                        .templateProfileDir(properties.getTemplateProfileDir())
                        .processTimeout(properties.getProcessTimeout())
                        .processRetryInterval(properties.getProcessRetryInterval())
                        .killExistingProcess(properties.isKillExistingProcess())
//                        .disableOpengl(properties.isDisableOpengl())
//                        .startFailFast(properties.isStartFailFast())
//                        .keepAliveOnShutdown(properties.isKeepAliveOnShutdown())
                        .taskQueueTimeout(properties.getTaskQueueTimeout())
                        .taskExecutionTimeout(properties.getTaskExecutionTimeout())
                        .maxTasksPerProcess(properties.getMaxTasksPerProcess());
        if (StringUtils.isBlank(properties.getProcessManagerClass())) {
            builder.processManager(processManager);
        } else {
            builder.processManager(properties.getProcessManagerClass());
        }

        // Starts the manager
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "processManager")
    public ProcessManager processManager() {
        return LocalOfficeUtils.findBestProcessManager();
    }

//    @Bean
//    @ConditionalOnMissingBean(name = "documentFormatRegistry")
//        /* default */ DocumentFormatRegistry documentFormatRegistry(final ResourceLoader resourceLoader)
//            throws Exception {
//
//        // Load the json resource containing default document formats.
//        final String registryResourceName =
//                StringUtils.isBlank(properties.getDocumentFormatRegistry())
//                        ? DEFAULT_FORMATS_PATH
//                        : properties.getDocumentFormatRegistry();
//        LOGGER.debug("Loading document formats registry from resource [{}]", registryResourceName);
//        try (InputStream in = resourceLoader.getResource(registryResourceName).getInputStream()) {
//
//            // Create the registry.
//            final JsonDocumentFormatRegistry registry =
//                    properties.getFormatOptions() == null
//                            ? JsonDocumentFormatRegistry.create(in)
//                            : JsonDocumentFormatRegistry.create(in, properties.getFormatOptions());
//
//            // Load the custom formats, if any.
//            final Resource resource = resourceLoader.getResource(CUSTOM_FORMATS_PATH);
//            if (resource.exists()) {
//                LOGGER.debug(
//                        "Loading custom document formats registry from resource [{}]", CUSTOM_FORMATS_PATH);
//                registry.addFormat(JsonDocumentFormatRegistry.create(resource.getInputStream()));
//            }
//
//            // Set as default.
//            DefaultDocumentFormatRegistryInstanceHolder.setInstance(registry);
//
//            // Return it.
//            return registry;
//        }
//    }

    @Bean(name = "localOfficeManager", initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean(name = "localOfficeManager")
    public LocalOfficeManager localOfficeManager(final ProcessManager processManager) {
        try {
            return createOfficeManager(processManager);
        } catch (Exception e) {
            return null;
        }
    }

    // Must appear after the localOfficeManager bean creation. Do not reorder this class by name.
//    @Bean
//    @ConditionalOnMissingBean(name = "localDocumentConverter")
//    @ConditionalOnBean(name = {"localOfficeManager", "documentFormatRegistry"})
//    public DocumentConverter localDocumentConverter(
//            final OfficeManager localOfficeManager, final DocumentFormatRegistry documentFormatRegistry) {
//
//        final Map<String, Object> loadProperties = new HashMap<>();
//        if (properties.isApplyDefaultLoadProperties()) {
//            loadProperties.putAll(LocalConverter.DEFAULT_LOAD_PROPERTIES);
//            if (properties.isUseUnsafeQuietUpdate()) {
//                loadProperties.put("UpdateDocMode", UpdateDocMode.QUIET_UPDATE);
//            }
//        }
//
//        return LocalConverter.builder()
//                .officeManager(localOfficeManager)
//                .formatRegistry(documentFormatRegistry)
//                .loadDocumentMode(properties.getLoadDocumentMode())
//                .loadProperties(loadProperties)
//                .build();
//    }
}
