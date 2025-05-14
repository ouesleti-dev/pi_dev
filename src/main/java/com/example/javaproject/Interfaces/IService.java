package com.example.javaproject.Interfaces;

import java.util.List;

public interface IService<T>{
    public void addEntity(T t);
    public boolean deleteEntity(T t);
    public void updateEntity(T t);

    public List<T> getAllData();
}