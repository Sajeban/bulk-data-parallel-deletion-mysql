package org.example;

import org.example.model.CustomerIdPeriod;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Queue;

import static org.example.Constant.LIMIT;

public class CustomerDeletionRunnable implements Runnable {
    private final Queue<CustomerIdPeriod> manipulationRebateIdPeriodQueue;
    private final Queue<CustomerIdPeriod> manipulationFailedQueue;
    private final Connection mySQLConnection;


    public CustomerDeletionRunnable(Queue<CustomerIdPeriod> manipulationRebateIdPeriodQueue, Queue<CustomerIdPeriod> manipulationFailedQueue, Connection mySQLConnection) {
        this.manipulationRebateIdPeriodQueue = manipulationRebateIdPeriodQueue;
        this.manipulationFailedQueue = manipulationFailedQueue;
        this.mySQLConnection = mySQLConnection;
    }

    @Override
    public void run() {
        CustomerIdPeriod customerIdPeriod;
        while ((customerIdPeriod = manipulationRebateIdPeriodQueue.poll()) != null) {
            try {

                Statement statement = mySQLConnection.createStatement();
                statement.execute("SET group_concat_max_len = 18446744073709547520; CALL delete_customer_by_chunks(" +
                        customerIdPeriod.startPointer() + "," + customerIdPeriod.endPointer() + "," + LIMIT + ");");
                System.out.println("deletion completed for chunk " + customerIdPeriod.startPointer() + "-" + customerIdPeriod.endPointer());

            } catch (Exception e) {
                System.out.println("deletion failed for chunk " + customerIdPeriod.startPointer() + "-" + customerIdPeriod.endPointer());
                manipulationFailedQueue.add(customerIdPeriod);
            }

        }
    }
}
