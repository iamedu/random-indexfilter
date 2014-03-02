package com.iamedu.nutch.indexer;

import java.io.*;

import java.util.*;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;

import org.apache.nutch.indexer.*;
import org.apache.nutch.parse.*;
import org.apache.nutch.crawl.*;

import org.slf4j.*;

import org.uncommons.maths.number.*;
import org.uncommons.maths.random.*;

public class RandomIndexFilter implements IndexingFilter {
    public static final String DISTRIBUTION_FILE = "distribution.file";
    public static Logger LOG = LoggerFactory.getLogger(IndexingJob.class);

    private Configuration conf;
    private Properties properties;
    private NumberGenerator<Integer> generator;

    public NutchDocument filter(NutchDocument doc, Parse parse, Text url, CrawlDatum datum, Inlinks inlinks)
        throws IndexingException {

        int n = generator.nextValue();
        LOG.info("Hello world {} {}", url, n);

        if(n > 42) {
            return doc;
        }

        return null;
    }

    public Configuration getConf() {
        return conf;
    }

    protected Reader getConfReader(Configuration conf) throws IOException {
        String distributionFile = conf.get(DISTRIBUTION_FILE);
        return conf.getConfResourceAsReader(distributionFile);
    }


    public void setConf(Configuration conf) {
        this.conf = conf;

        try {
            properties = new Properties();
            properties.load(getConfReader(conf));

            String generatorType = (String)properties.get("distribution");

            if(generatorType == null) {
                generatorType = "";
            }

            if(generatorType.equals("binomial")) {
                int n = Integer.parseInt(properties.get("n").toString());
                double p = Double.parseDouble(properties.get("p").toString());
                generator = new BinomialGenerator(n, p, new Random());
            } else if(generatorType.equals("possion")) {
                double mean = Double.parseDouble(properties.get("mean").toString());
                generator = new PoissonGenerator(mean, new Random());
            } else {
                generator = new ConstantGenerator<Integer>(42);
            }

        } catch(Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }


    }
}

