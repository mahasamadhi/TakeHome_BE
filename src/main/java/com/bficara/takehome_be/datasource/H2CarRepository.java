package com.bficara.takehome_be.datasource;

import com.bficara.takehome_be.datasource.ICarDataSource;
import com.bficara.takehome_be.model.Car;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

@Repository
public class H2CarRepository implements ICarDataSource {

    private final JdbcTemplate jdbcTemplate;

    public H2CarRepository(JdbcTemplate jdbcTemplate) {
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