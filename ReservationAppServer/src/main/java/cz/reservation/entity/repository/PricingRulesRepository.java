package cz.reservation.entity.repository;

import cz.reservation.entity.PricingRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PricingRulesRepository extends JpaRepository<PricingRuleEntity, Long> {
}
