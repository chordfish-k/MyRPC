package com.chord.myrpc.registry;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.GetResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class EtcdRegistry {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 创建etcd客户端
        Client client = Client.builder().endpoints("http://localhost:2379").build();

        // kvClient：用于对 etcd 中的键值对进行操作
        KV kvClient = client.getKVClient();
        ByteSequence key = ByteSequence.from("test_key".getBytes());
        ByteSequence value = ByteSequence.from("test_value".getBytes());

        // put 键值对
        kvClient.put(key, value);

        // get
        CompletableFuture<GetResponse> getFuture = kvClient.get(key);
        GetResponse response = getFuture.get();

        // delete
        kvClient.delete(key).get();
    }
}
