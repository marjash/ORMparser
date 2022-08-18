package com.knubisoft;

import java.util.List;

public interface DataReadWriteSource<ReadType> {
    ReadType getContent();
}