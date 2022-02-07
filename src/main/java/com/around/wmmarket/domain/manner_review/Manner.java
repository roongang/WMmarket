package com.around.wmmarket.domain.manner_review;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Manner {
    // TODO : 매너 리스트 추가해야함
    // GOOD
    GOOD_KIND("친절한 유저입니다."),
    // BAD
    BAD_UNKIND("불친절한 유저입니다.");

    private final String msg;
}
