package com.knubisoft;

import java.util.List;

interface ParsingStrategy<T extends DataReadWriteSource> {
    ORM.Table parseToTable(T content);

    <T> void write(DataReadWriteSource<?> source, List<T> objects);
}
