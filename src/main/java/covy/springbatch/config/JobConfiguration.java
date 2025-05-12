package covy.springbatch.config;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class JobConfiguration {

  private final PlatformTransactionManager transactionManager;
  private final JobRepository jobRepository;
  int size = 0;
  int chunk = 1;

  @Bean
  public Job exampleJob(Step step1, Step step2, Step step3) {
    return new JobBuilder("exampleJob", jobRepository)
        .start(step1)
        .next(step2)
        .next(step3) // 여기에 포함시키면 실행됨!
        .build();
  }

  // task 기반 step 사용한 메서드
  @Bean
  @JobScope
  public Step step1() {
    return new StepBuilder("exampleStep1", jobRepository)
        .tasklet((contribution, chunkContext) -> {
          System.out.println("hello step1");
          return RepeatStatus.FINISHED;
        }, transactionManager).build();
  }

  // task 기반 step 사용한 메서드
  @Bean
  @JobScope
  public Step step2() {
    return new StepBuilder("exampleStep2", jobRepository)
        .tasklet((contribution, chunkContext) -> {
          System.out.println("hello step2");
          return RepeatStatus.FINISHED;
        }, transactionManager).build();
  }

  // chunk 기반 step 사용
  @Bean
  @JobScope
  public Step step3() {
    return new StepBuilder("step1", jobRepository)
        .chunk(10, transactionManager)
        .reader(() -> {
          if (size == 11) {
            return null;
          }
          System.out.println("reader : " + size);
          return size++;
        })
        .processor(item -> {
          System.out.println("processor : " + item);
          return item;
        })
        .writer(it -> {
          System.out.println("writer : " + (chunk++) + "번째 chunk : " + it.getItems());
        })
        .build();
  }

}
