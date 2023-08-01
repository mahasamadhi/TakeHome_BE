package com.bficara.takehome_be.datasource;

import com.bficara.takehome_be.model.Car;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository("h2CarRepository")
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

    @Override
    public List<Car> getAllLessThanPrice(int price) {
        String sql = "SELECT * FROM car where msrp < ?";

        RowMapper<Car> mapper = getCarRowMapper();
        return jdbcTemplate.query(sql, mapper, price);
    }
    public Map<String, List<String>> getMakeOptions() {
        String sql = "SELECT distinct make FROM car";
        List<String> makes = jdbcTemplate.queryForList(sql, String.class);

        Map<String, List<String>> result = new HashMap<>();
        result.put("makeOptions", makes);

        return result;
    }

    public Map<String, List<String>> getYearOptions() {
        String sql = "SELECT distinct caryear FROM car";
        List<String> years = jdbcTemplate.queryForList(sql, String.class);

        Map<String, List<String>> result = new HashMap<>();
        result.put("yearOptions", years);

        return result;
    }


    private RowMapper<Car> getCarRowMapper() {
        return (rs, rowNum) -> new Car(
                rs.getInt("caryear"),
                rs.getString("make"),
                rs.getString("model"),
                rs.getDouble("msrp")
        );
    }

    public void saveAll(List<Car> cars) {
        String sql = "INSERT INTO car (caryear, make, model, msrp) VALUES (?, ?, ?, ?)";

        List<Object[]> parameters = cars.stream()
                .map(car -> new Object[] {car.getYear(), car.getMake(), car.getModel(), car.getMsrp()})
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(sql, parameters);
    }

    public void deleteAll() {
        String sql = "delete from car";
        jdbcTemplate.update(sql);
    }
}