/**
 * Created By Yufan Wu
 * 2021/7/18
 */
package com.myse1f.myrpc.registry.zookeeper;

import com.myse1f.myrpc.registry.ServiceRegistry;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class ZKServiceRegistry implements ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ZKServiceRegistry.class);
    private ZkClient zkClient;

    public ZKServiceRegistry(String zkAddress) {
        zkClient = new ZkClient(zkAddress, Constants.ZK_SESSION_TIMEOUT, Constants.ZK_CONNECTION_TIMEOUT);
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        // create node under "/registry"
        String servicePath = Constants.ZK_REGISTRY_PATH + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath, true);
            logger.info("create service node: {}", servicePath);
        }
        // create ephemeral node under service
        String addressPath = servicePath + "/address-";
        String addressNode = zkClient.createEphemeralSequential(addressPath, serviceAddress);
        logger.info("create address node: {} with address {}", addressNode, serviceAddress);
    }

    // todo 有分布式同步的问题
    @Override
    public List<String> getService(String serviceName) {
        String servicePath = Constants.ZK_REGISTRY_PATH + "/" + serviceName;
        if (!zkClient.exists(servicePath)) {
            throw new IllegalStateException("Can not find service" + serviceName);
        }

        List<String> addressList = zkClient.getChildren(servicePath);
        if (CollectionUtils.isEmpty(addressList)) {
            throw new IllegalStateException("Can not find service address under " + serviceName);
        }

        List<String> list = addressList.stream().map(addr -> servicePath + "/" + addr)
                .map(path -> (String)zkClient.readData(path))
                .collect(Collectors.toList());

        return list;
    }
}
