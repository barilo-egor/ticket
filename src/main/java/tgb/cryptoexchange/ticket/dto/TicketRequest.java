package tgb.cryptoexchange.ticket.dto;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import tgb.cryptoexchange.ticket.entity.Ticket;

import java.util.ArrayList;
import java.util.List;

@Data
public class TicketRequest {

    private String appId;

    private Long userId;

    private String category;

    private Boolean hasReply;

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
        if (hasReply != null) {
            if (hasReply) {
                predicates.add(cb.isNotNull(root.get("replyTicket")));
            } else {
                predicates.add(cb.isNull(root.get("replyTicket")));
            }
        }
        return predicates;
    }

}
