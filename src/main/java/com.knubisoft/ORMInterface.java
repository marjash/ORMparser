package com.knubisoft;

import lombok.SneakyThrows;

import java.util.List;

public interface ORMInterface {

//    interface DataReadWriteSource {
//    }

    @SneakyThrows
    <T> List<T> readAll(DataReadWriteSource<?> source, Class<T> cls);

    @SneakyThrows
    <T> void writeAll(DataReadWriteSource<?> source, List<T> objects);
//
//    @RequiredArgsConstructor
//    @Getter
//    final class FileReadWriteSource implements DataReadWriteSource {
//        private final File source;
//    }
//
//    @RequiredArgsConstructor
//    @Getter
//    final class ConnectionReadWriteSource implements DataReadWriteSource {
//        private final Connection source;
//    }

}
