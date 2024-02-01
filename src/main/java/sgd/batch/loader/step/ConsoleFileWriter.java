package sgd.batch.loader.step;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsoleFileWriter<T> implements ItemWriter<T> {

    @Override
    public void write(List<? extends T> metadataFiles) throws Exception {
        log.info("{}", metadataFiles);
    }

}
