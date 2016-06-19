package chen.guo.X.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * Keep local Hbase running before running the code.
 */
public class HBaseLocal {
  public static void main(String[] args) throws IOException {
    Configuration conf = HBaseConfiguration.create();
    HBaseAdmin admin = new HBaseAdmin(conf);
    System.out.println("Running");

    HTableDescriptor table = new HTableDescriptor(TableName.valueOf("fun"));
    HColumnDescriptor family = new HColumnDescriptor("myCF");
    table.addFamily(family);
    System.out.println("Creating Table");
    admin.createTable(table);

    System.out.println(" Table created ");
  }
}
