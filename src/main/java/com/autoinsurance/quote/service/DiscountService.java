package com.autoinsurance.quote.service;

import com.autoinsurance.quote.dto.QuoteRequestDto;
import java.math.BigDecimal;
import java.util.List;

public interface DiscountService {
    BigDecimal calculateTotalDiscount(QuoteRequestDto request);
    List<String> getAppliedDiscounts(QuoteRequestDto request);
}