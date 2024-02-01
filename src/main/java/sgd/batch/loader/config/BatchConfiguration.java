package sgd.batch.loader.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import lombok.RequiredArgsConstructor;
import sgd.batch.loader.models.Metadata;
import sgd.batch.loader.step.ConsoleFileWriter;
import sgd.batch.loader.step.MetadataFileReader;

@Configuration
@RequiredArgsConstructor
public class BatchConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    Job readMetadataFilesJob(Step readMetadataFilesStep) {
        return jobBuilderFactory
                .get("readMetadataFilesJob")
                .start(readMetadataFilesStep)
                .build();
    }

    @Bean
    Step readMetadataFilesStep(ItemReader<Metadata> reader,
            ItemWriter<Metadata> writer) {
        return stepBuilderFactory
                .get("readMetadataFilesStep")
                .<Metadata, Metadata>chunk(5)
                .reader(reader)
                .writer(writer)
                .build();
    }

    @Bean
    ItemReader<Metadata> reader(Environment environment) {
        return new MetadataFileReader(environment.getProperty("batch.dir.input"));
    }

    @Bean
    ItemWriter<Metadata> writer() {
        return new ConsoleFileWriter<>();
    }

}