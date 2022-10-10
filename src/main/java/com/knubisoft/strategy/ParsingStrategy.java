package com.knubisoft.strategy;

import com.knubisoft.DataReadWriteSource;
import com.knubisoft.ORM;

import java.util.List;

public interface ParsingStrategy<T extends DataReadWriteSource> {
    ORM.Table parseToTable(T content);
    <T> void write(DataReadWriteSource<?> source, List<T> objects);
}
