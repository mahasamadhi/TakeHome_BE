package com.bficara.takehome_be.car;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("dbCarDataSource")
public class DatabaseCarDataSource implements ICarDataSource {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseCarDataSource(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Car> getData() {
        String sql = "SELECT * FROM car";

        RowMapper<Car> mapper = (rs, rowNum) -> new Car(
                rs.getInt("caryear"),
                rs.getString("make"),
                rs.getString("model"),
                rs.getDouble("price")
        );

        return jdbcTemplate.query(sql, mapper);
    }

    public List<Car> getDataByYear(int year) {
        String sql = "SELECT * FROM car WHERE caryear = ?";

        RowMapper<Car> mapper = getCarRowMapper();

//        return jdbcTemplate.query(sql, mapper, year);
        return jdbcTemplate.query(sql, mapper, year);
    }

    private RowMapper<Car> getCarRowMapper() {
        return (rs, rowNum) -> new Car(
                rs.getInt("caryear"),
                rs.getString("make"),
                rs.getString("model"),
                rs.getDouble("price")
        );
    }
}