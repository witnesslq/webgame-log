package com.yuhe.szml;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

import com.yuhe.szml.bolt.LogBolt;
import com.yuhe.szml.bolt.StaticsBolt;
import com.yuhe.szml.spout.RedisSpout;



public class StaticsTopology {
	public static void main(String[] args) throws Exception {
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("spout", new RedisSpout(), 1);
		builder.setBolt("log", new LogBolt(), 10).shuffleGrouping("spout");
		builder.setBolt("statics", new StaticsBolt(), 10).fieldsGrouping("log", new Fields("staticsIndex"));
		Config conf = new Config();
		
		if (args != null && args.length > 0) {
			conf.setNumWorkers(1);
			StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
		} else {
			conf.setDebug(true);
			conf.setMaxTaskParallelism(1);
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("oss-statics", conf, builder.createTopology());
			Thread.sleep(10000);
			//cluster.shutdown();
		}
	}

}
