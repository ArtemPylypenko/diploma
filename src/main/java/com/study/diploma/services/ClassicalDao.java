package com.study.diploma.services;

import java.util.List;

public interface ClassicalDao<T> {
    public T save(T t);

    public void delete(T t);

    public List<T> getAll();
}
