package org.example;

import org.example.config.DataSourceInitializer;
import org.example.model.CustomerIdPeriod;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.example.Constant.CHUNK_SIZE;
import static org.example.Constant.THREAD_SIZE;


public class App {

    public static void main(String[] args) {
        final ConcurrentLinkedQueue<CustomerIdPeriod> manipulationRebateIdPeriodQueue = new ConcurrentLinkedQueue<>();
        final ConcurrentLinkedQueue<CustomerIdPeriod> manipulationFailedQueue = new ConcurrentLinkedQueue<>();

        try {
            int startPointer = 0;
            int endPointer = 0;

            String SQL_SELECT_MAX_MIN = "SELECT min(customer_id) as minPrimary, max(customer_id)  as maxPrimary FROM customer where city='Columbia'";

            Connection conn = DataSourceInitializer.getConnection();
            Statement statement = conn.createStatement();
            statement.execute(SQL_SELECT_MAX_MIN);
            ResultSet resultSet = statement.getResultSet();

            while (resultSet.next()) {

                startPointer = resultSet.getInt("minPrimary");
                endPointer = resultSet.getInt("maxPrimary");

            }

            if (startPointer != 0) {
                while (startPointer <= endPointer) {
                    CustomerIdPeriod customerIdPeriod = new CustomerIdPeriod(startPointer, Math.min(startPointer + CHUNK_SIZE - 1, endPointer));
                    manipulationRebateIdPeriodQueue.add(customerIdPeriod);
                    startPointer = startPointer + CHUNK_SIZE;
                }
                System.out.println("data deletion from customer table started");

                List<Callable<Object>> customerDeletionRunnableTasks = new ArrayList<>(THREAD_SIZE);
                for (int i = 0; i < THREAD_SIZE; i++) {
                    customerDeletionRunnableTasks.add(Executors.callable(new CustomerDeletionRunnable(manipulationRebateIdPeriodQueue, manipulationFailedQueue, conn)));
                }
                ExecutorService executorService = Executors.newFixedThreadPool(THREAD_SIZE);
                executorService.invokeAll(customerDeletionRunnableTasks);
            } else {
                System.out.println("data deletion skipped due to unavailable lines");
            }


        } catch (SQLException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}