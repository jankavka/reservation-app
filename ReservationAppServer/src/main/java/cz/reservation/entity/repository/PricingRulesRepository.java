package cz.reservation.entity.repository;

import cz.reservation.constant.PricingType;
import cz.reservation.entity.PricingRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PricingRulesRepository extends JpaRepository<PricingRuleEntity, Long> {

    @Query(value = "SELECT (*) FROM pricing_rules WHERE pricing_type = :type", nativeQuery = true)
    List<PricingRuleEntity> getAllPricingRulesByPricingType(@Param("type")PricingType type);
}
