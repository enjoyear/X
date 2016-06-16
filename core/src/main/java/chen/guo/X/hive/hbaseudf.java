package chen.guo.X.hive;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hive.ql.exec.UDF;

import java.io.IOException;

public class hbaseudf extends UDF {
  HTable table;

  public hbaseudf() throws IOException {
    Configuration conf = HBaseConfiguration.create();
    HBaseAdmin admin = new HBaseAdmin(conf);

    table = new HTable(conf, "fun");
  }

  public String evaluate(String acc, String ptype, String pid) throws IOException {
    Get g = new Get(Bytes.toBytes(acc + "_" + ptype + "_" + pid));

    Result result = table.get(g);
    System.out.println("Get results...");
    return result.toString();
  }
}
