package org.ihorzima.telegram_notification.builder;

import java.util.List;

public interface PdfFileBuilder<T> {
    byte[] build(T input);
}
