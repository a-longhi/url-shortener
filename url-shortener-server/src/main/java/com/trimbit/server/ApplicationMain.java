package com.trimbit.server;

import com.trimbit.server.service.MessagingService;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@QuarkusMain
@Slf4j
public class ApplicationMain implements QuarkusApplication {
  private final MessagingService messagingService;

  public ApplicationMain(MessagingService messagingService){
    this.messagingService = messagingService;
  }

  @Override
  public int run(String... args) {
    log.info("APPLICATION STARTED");
    try {
      this.messagingService.startProcessingMessages();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Quarkus.waitForExit();
    return 0;
  }
}
