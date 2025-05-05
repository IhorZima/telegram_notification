package org.ihorzima.telegram_notification.builder;

public interface PdfFileBuilder<T> {
    byte[] build(T input);
}
