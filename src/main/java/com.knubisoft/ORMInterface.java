package com.knubisoft;

import lombok.SneakyThrows;

import java.util.List;

public interface ORMInterface {

    @SneakyThrows
    <T> List<T> readAll(DataReadWriteSource<?> source, Class<T> cls);

    @SneakyThrows
    <T> void writeAll(DataReadWriteSource<?> source, List<T> objects);
}
