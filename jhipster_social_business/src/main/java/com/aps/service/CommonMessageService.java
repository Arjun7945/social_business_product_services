package com.aps.service;

import org.springframework.stereotype.Service;

@Service
public class CommonMessageService {

    public String getButtonViewOptions() {
        return "View Options";
    }

    public String getMessageForHandleStaleButton() {
        return "⚠️ ഈ ഓപ്ഷൻ കാലഹരണപ്പെട്ടു. ഏറ്റവും പുതിയ മെനു ഉപയോഗിക്കുക.";
    }
}
