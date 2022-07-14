package org.example.model;

public record CustomerIdPeriod(long startPointer, long endPointer) {
}

//
//    String SQL_SELECT = "Select * from customer";
//
//        try (Connection conn = DataSourceInitializer.getConnection();
//                PreparedStatement preparedStatement = conn.prepareStatement(SQL_SELECT)) {
//
//                ResultSet resultSet = preparedStatement.executeQuery();
//
//                while (resultSet.next()) {
//
//                long id = resultSet.getLong("customer_id");
//                String name = resultSet.getString("customer_name");
//                System.out.println(id + " " + name);
//
//                }
//
//                } catch (SQLException e) {
//                System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
//                } catch (Exception e) {
//                e.printStackTrace();
//                }
