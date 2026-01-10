package tgb.cryptoexchange.ticket.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import tgb.cryptoexchange.ticket.entity.Ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class TicketRequest {

    private  String appId;

    private Long userId;

    private String category;

    @Min(0)
    private Integer pageNumber = 0;

    @Max(100)
    @Min(1)
    private Integer pageSize = 20;

    public List<Predicate> toPredicates(Root<Ticket> root, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if (StringUtils.isNotBlank(appId)) {
            predicates.add(cb.equal(root.get("appId"), appId));
        }
        if (userId != null) {
            predicates.add(cb.equal(root.get("userId"), userId));
        }
        if (StringUtils.isNotBlank(category)) {
            predicates.add(cb.equal(root.get("category"), category));
        }
        return predicates;
    }

}
