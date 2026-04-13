package com.crd.technical.assessment.service;

import com.crd.technical.assessment.model.Security;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RebalanceService {
    public Map<String, Security> rebalance(double totalAsset, List<Security> securities) {
        if (totalAsset <= 0) {
            throw new IllegalArgumentException("Total asset must be greater than zero");
        }

        if (securities == null || securities.isEmpty()) {
            throw new IllegalArgumentException("Portfolio cannot be empty");
        }

        double totalTarget = 0;

        for (Security sec : securities) {
            if (sec.getUnitPrice() <= 0) {
                throw new IllegalArgumentException("Price must be greater than zero");
            }

            if (sec.getTargetPercentage() < 0 || sec.getCurrentPercentage() < 0) {
                throw new IllegalArgumentException("Percentage cannot be negative");
            }

            totalTarget += sec.getTargetPercentage();
        }

        if (Math.round(totalTarget) != 100) {
            throw new IllegalArgumentException("Total target percentage must be 100");
        }

        Map<String, Security> result = new HashMap<>();

        for (Security sec : securities) {
            double currentValue = totalAsset * (sec.getCurrentPercentage() / 100);
            double targetValue = totalAsset * (sec.getTargetPercentage() / 100);
            double diff = targetValue - currentValue;
            double targetVarianceValue = totalAsset * (sec.getTargetVariance()/ 100);
            int targetVarianceShares =  (int) Math.round(targetVarianceValue / sec.getUnitPrice());
            sec.setTargetVarianceShares(targetVarianceShares);
            int shares = (int) Math.round(diff / sec.getUnitPrice());
            sec.setShareBuySell(shares);
            result.put(sec.getName(), sec);
        }
        return result;
    }
}
