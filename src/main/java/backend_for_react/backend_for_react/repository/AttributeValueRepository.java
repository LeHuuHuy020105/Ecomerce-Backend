package backend_for_react.backend_for_react.repository;

import backend_for_react.backend_for_react.common.enums.Status;
import backend_for_react.backend_for_react.model.Attribute;
import backend_for_react.backend_for_react.model.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttributeValueRepository extends JpaRepository<AttributeValue,Long> {
    List<AttributeValue> findAllByAttribute(Attribute attribute);

    Optional<AttributeValue>findByIdAndStatus(Long id, Status status);
}
