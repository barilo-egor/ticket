package tgb.cryptoexchange.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import tgb.cryptoexchange.ticket.entity.Ticket;

public interface TickerRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {

}
