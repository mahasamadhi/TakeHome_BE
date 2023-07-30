package com.bficara.takehome_be.car;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("H2CarDataSource")
public class H2CarDatasource implements ICarDataSource {

    private final JdbcTemplate jdbcTemplate;

    public H2CarDatasource(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Car> getAll() {
        String sql = "SELECT * FROM car";

        RowMapper<Car> mapper = getCarRowMapper();
        return jdbcTemplate.query(sql, mapper);
    }

    @Override
    public List<Car> getAllByYear(int year) {
        String sql = "SELECT * FROM car where caryear = ?";

        RowMapper<Car> mapper = getCarRowMapper();
        return jdbcTemplate.query(sql, mapper, year);
    }

    @Override
    public List<Car> getAllByMake(String make) {
        String sql = "SELECT * FROM car where make = ?";

        RowMapper<Car> mapper = getCarRowMapper();
        return jdbcTemplate.query(sql, mapper, make);
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