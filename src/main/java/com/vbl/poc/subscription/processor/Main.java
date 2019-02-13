package com.vbl.poc.subscription.processor;

import com.vbl.poc.subscription.processor.core.SubscriptionProcessor;
import com.vbl.poc.subscription.processor.core.SubscriptionRepository;
import com.vbl.poc.subscription.processor.repo.InMemorySubscriptionRepository;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        SubscriptionRepository repository = new InMemorySubscriptionRepository();
        SubscriptionProcessor[] processors = new SubscriptionProcessor[3];
        processors[0] = new SubscriptionProcessor("Node1", repository);
        processors[1] = new SubscriptionProcessor("Node2", repository);
        processors[2] = new SubscriptionProcessor("Node3", repository);

        Scanner scanner = new Scanner(System.in);
        System.out.println(">");
        boolean finished = false;
        while (!finished) {
            try {
                String command = scanner.nextLine();
                String[] splittedCommand = command.split(" ");
                if ("exit".equals(splittedCommand[0])) {
                    finished = true;
                    for (SubscriptionProcessor processor : processors) {
                        processor.stop();
                    }
                } else if ("start".equals(splittedCommand[0])) {
                    int processorIndex = Integer.valueOf(splittedCommand[1]);
                    new Thread(processors[processorIndex], processors[processorIndex].getName()).start();
                } else if ("stop".equals(splittedCommand[0])) {
                    int processorIndex = Integer.valueOf(splittedCommand[1]);
                    processors[processorIndex].stop();
                }
                System.out.println(">");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

}
