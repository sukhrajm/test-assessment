package co.copper.test.storage;

import java.util.List;
import java.util.UUID;

import co.copper.test.datamodel.User;
import co.copper.test.services.TestJavaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.sbuslab.utils.db.JacksonBeanRowMapper;

import co.copper.test.datamodel.Test;


@Repository
public class TestJavaRepository {

    private static final Logger log = LoggerFactory.getLogger(TestJavaService.class);

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final JacksonBeanRowMapper<Test> rowMapper;
    private final JacksonBeanRowMapper<User> userRowMapper;

    @Autowired
    public TestJavaRepository(NamedParameterJdbcTemplate jdbcTemplate, ObjectMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new JacksonBeanRowMapper<>(Test.class, mapper);
        this.userRowMapper = new JacksonBeanRowMapper<>(User.class, mapper);
    }

    public List<Test> getById(Long id) {
        return jdbcTemplate.query("SELECT * FROM test WHERE id = :id", new MapSqlParameterSource("id", id), rowMapper);
    }

    public List<User> insert(User user) {

        return jdbcTemplate.query("INSERT INTO users (id, first_name, last_name, email, password) VALUES (:id, :firstName, :lastName, :email, :password) RETURNING *",
            new MapSqlParameterSource("id", UUID.randomUUID().toString())
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("password", user.getPassword()), userRowMapper);
    }

}
