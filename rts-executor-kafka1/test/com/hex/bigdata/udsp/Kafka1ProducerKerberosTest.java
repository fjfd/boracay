package com.hex.bigdata.udsp;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Created by JunjieM on 2018-11-26.
 */
public class Kafka1ProducerKerberosTest {
    private static String TOPIC_NAME = "test1";

    public static void main(String[] args) {
        System.setProperty ("java.security.krb5.conf", "A:/kerberos/krb5.conf");
        System.setProperty ("java.security.auth.login.config", "A:/kerberos/jaas-keytab.conf");
        System.setProperty ("javax.security.auth.useSubjectCredsOnly", "false");
//       System.setProperty("sun.security.krb5.debug","true");

        Properties props = new Properties ();
        props.put ("bootstrap.servers", "172.18.21.62:9092,172.18.21.63:9020");
        props.put ("acks", "all");
        props.put ("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put ("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put ("security.protocol", "SASL_PLAINTEXT");
        props.put ("sasl.kerberos.service.name", "kafka");

        Producer<String, String> producer = new KafkaProducer<> (props);
        for (int i = 0; i < 10; i++) {
            String key = "key-" + i;
            String message = "Message-" + i;
            producer.send (new ProducerRecord<> (TOPIC_NAME, key, message));
            System.out.println (key + "----" + message);
        }
        producer.close ();
    }
}
