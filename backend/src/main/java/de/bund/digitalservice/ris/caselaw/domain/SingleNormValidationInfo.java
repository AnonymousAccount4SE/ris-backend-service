package de.bund.digitalservice.ris.caselaw.domain;

import de.bund.digitalservice.ris.caselaw.domain.validator.SingleNormConstraint;

public record SingleNormValidationInfo(
    @SingleNormConstraint String singleNormStr, String normAbbreviationStr) {}