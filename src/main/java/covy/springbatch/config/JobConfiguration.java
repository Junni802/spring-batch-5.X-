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

  @Bean
  public Job exampleJob(Step step1, Step step2) {
    return new JobBuilder("exampleJob", jobRepository)
        .start(step1)
        .next(step2)
        .build();
  }

  @Bean
  @JobScope
  public Step step1() {
    return new StepBuilder("exampleStep1", jobRepository)
        .tasklet((contribution, chunkContext) -> {
          System.out.println("hello step1");
          return RepeatStatus.FINISHED;
        }, transactionManager).build();
  }

  @Bean
  @JobScope
  public Step step2() {
    return new StepBuilder("exampleStep2", jobRepository)
        .tasklet((contribution, chunkContext) -> {
          System.out.println("hello step2");
          return RepeatStatus.FINISHED;
        }, transactionManager).build();
  }

}
