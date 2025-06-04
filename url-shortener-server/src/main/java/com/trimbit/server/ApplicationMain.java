package com.trimbit.server;

import com.trimbit.server.service.MessagingService;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@QuarkusMain
public class ApplicationMain implements QuarkusApplication {
  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationMain.class);
  private final MessagingService messagingService;

  public ApplicationMain(MessagingService messagingService){
    this.messagingService = messagingService;
  }

  @Override
  public int run(String... args) {
    LOGGER.info("APPLICATION STARTED");
    try {
      this.messagingService.startProcessingMessages();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    Quarkus.waitForExit();
    return 0;
  }
}
