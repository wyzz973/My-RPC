package org.example.client.serviceCenter.balance.impl;

import org.example.client.serviceCenter.balance.LoadBalance;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author sd
 * @date 2025/3/10 21:47
 * @description: 一致性哈希算法 负载均衡
 */
public class ConsistencyHashBalance implements LoadBalance {
    //虚拟节点个数
    private static final int VIRTUAL_NUM = 5;

    //保存虚拟节点的hash值和对应的虚拟节点，key为hash值，value为虚拟节点的名称
    /**
     * 问题分析：
     * shards 是 非线程安全的 TreeMap，在多线程环境下：
     * getServer() 在查找虚拟节点时可能发生 并发修改异常 (ConcurrentModificationException)。
     * addNode() 和 delNode() 可能在操作 shards 时导致 数据错乱。
     */
//    private SortedMap<Integer, String> shards = new TreeMap<Integer, String>();  线程不安全！！！！
    private final ConcurrentSkipListMap<Integer, String> shards = new ConcurrentSkipListMap<>();

    //真实节点列表
    //addNode() 方法中只检查 realNodes.contains(node)，但 realNodes 是 LinkedList，查找复杂度为 O(n)，对于大规模服务器可能影响性能。
    /*private List<String> realNodes = new LinkedList<String>();   */
    private final Set<String> realNodes = new HashSet<>();

    //模拟初始服务器
    private String[] servers = null;

    /**
     * 初始化负载均衡器，将真实的服务节点和对应的虚拟节点添加到哈希环中。
     * @param serviceList
     */
    /** new!!! 改进init。*/
    /**
    private void init(List<String> serviceList) {
        serviceList.forEach(server -> {
            realNodes.add(server);
            System.out.println("真实节点[" + server + "]被添加");
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = server + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.put(hash, virtualNode);
                System.out.println("虚拟节点[" + virtualNode + "] hash:" + hash + "，被添加");
            }
        });
    }
     */
    public void init(List<String> serviceList){
        if (shards.isEmpty() && !serviceList.isEmpty()){
            synchronized (shards){
                serviceList.forEach(server -> {
                    realNodes.add(server);
                    System.out.println("真实节点[" + server + "]被添加");
                    for (int i = 0; i < VIRTUAL_NUM; i++) {
                        String virtualNode = server + "&&VN" + i;
                        int hash = getHash(virtualNode);
                        shards.put(hash, virtualNode);
                        System.out.println("虚拟节点[" + virtualNode + "] hash:" + hash + "，被添加");
                    }
                });
            }
        }
    }


    /**
     * 获取被分配的节点名
     * @param node
     * @param serverList
     * @return
     */

    /**
    public String getServer(String node, List<String> serverList){

        init(serverList);
        int hash = getHash(node);
        Integer key = null;
        //
        SortedMap<Integer,String> subMap = shards.tailMap(hash);
        if (subMap.isEmpty()){
            key = shards.lastKey();
        }else {
            key = subMap.firstKey();
        }
        String virtualNode = shards.get(key);
        return virtualNode.substring(0,virtualNode.indexOf("&&"));
    }
     */

    /** new!!! 改进init。*/

    public String getServer(String node, List<String> serverList){
        if (serverList == null || serverList.isEmpty()){
            throw new IllegalArgumentException("服务器列表为空");
        }

        init(serverList);

        int hash = getHash(node);

        Map.Entry<Integer,String> entry = shards.ceilingEntry(hash);
        /*shards.ceilingEntry(hash)：在 TreeMap 中查找 大于等于 (≥) hash 的最小键（即最近的节点）。*/
        if (entry == null){
            entry = shards.firstEntry();
        }

        return entry.getValue().substring(0,entry.getValue().indexOf("&&"));

    }


    @Override
    public String balance(List<String> addressList) {
        String random = UUID.randomUUID().toString();
        return getServer(random,addressList);
    }

    @Override
    public void addNode(String node) {
        if (!realNodes.contains(node)){
            realNodes.add(node);
            System.out.println("真实节点[" + node + "] 上线添加");
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.put(hash,virtualNode);
                System.out.println("虚拟节点[" + virtualNode + "] hash:" + hash + "，被添加");
            }
        }
    }

    @Override
    public void delNode(String node) {
        if (realNodes.contains(node)){
            realNodes.remove(node);
            System.out.println("真实节点[" + node + "] 下线移除");
            for (int i = 0; i < VIRTUAL_NUM; i++) {
                String virtualNode = node + "&&VN" + i;
                int hash = getHash(virtualNode);
                shards.remove(hash);
                System.out.println("虚拟节点[" + virtualNode + "] hash:" + hash + "，被移除");
            }
        }
    }


    private static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++)
            hash = (hash ^ str.charAt(i)) * p;
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        // 如果算出来的值为负数则取其绝对值    有问题  Math.abs(Integer.MIN_VALUE) 仍然是 负数 (-2147483648)，可能导致哈希冲突。
        /*if (hash < 0)
            hash = Math.abs(hash);
        return hash;*/
        hash = hash & 0x7fffffff; // 只保留正数部分
        return hash;
    }

    public static void main(String[] args) {
        System.out.println(Integer.MIN_VALUE);
        System.out.println(Integer.MIN_VALUE & 0x7fffffff);
    }
}
