package de.adesso.kicker.ranking.persistence;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KFactor {
    LOW(16),
    MEDIUM(24),
    HIGH(32);

    private final int value;
}
