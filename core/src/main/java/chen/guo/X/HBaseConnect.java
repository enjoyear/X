package chen.guo.X;


import com.google.protobuf.ServiceException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class HBaseConnect {
//https://forums.aws.amazon.com/thread.jspa?messageID=386668

  public static void main(String[] args) throws IOException, ServiceException {
    HBaseConfiguration config = new HBaseConfiguration();
    config.clear();
    config.set("hbase.zookeeper.quorum", "ec2-52-39-11-246.us-west-2.compute.amazonaws.com");
    config.set("hbase.zookeeper.property.clientPort", "2181");
    config.set("hbase.master", "ec2-52-39-11-246.us-west-2.compute.amazonaws.com:60000");
    //HBaseConfiguration config = HBaseConfiguration.create();
    //config.set("hbase.zookeeper.quorum", "localhost");  // Here we are running zookeeper locally
    HBaseAdmin.checkHBaseAvailable(config);
    System.out.println("HBase is running!");


  }
}
