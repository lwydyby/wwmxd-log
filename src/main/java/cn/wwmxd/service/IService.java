package cn.wwmxd.service;

public interface IService<T,S> {
    T selectById(S id);
}
