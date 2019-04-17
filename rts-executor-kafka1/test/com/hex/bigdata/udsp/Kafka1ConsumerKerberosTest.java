package com.hex.bigdata.udsp;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created by JunjieM on 2018-11-26.
 */
public class Kafka1ConsumerKerberosTest {

    private static String TOPIC_NAME = "test1";

    public static void main(String[] args) {
        System.setProperty ("java.security.krb5.conf", "A:/kerberos/krb5.conf");
        System.setProperty ("java.security.auth.login.config", "A:/kerberos/jaas-keytab.conf");
        System.setProperty ("javax.security.auth.useSubjectCredsOnly", "false");
//       System.setProperty("sun.security.krb5.debug","true");

        Properties props = new Properties ();
        props.put ("bootstrap.servers", "172.18.21.62:9092,172.18.21.63:9020");
        props.put ("group.id", "group1");
        props.put ("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put ("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put ("enable.auto.commit", "true");
        props.put ("auto.commit.interval.ms", "1000");
        props.put ("security.protocol", "SASL_PLAINTEXT");
        props.put ("sasl.kerberos.service.name", "kafka");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<> (props);
        TopicPartition partition0 = new TopicPartition (TOPIC_NAME, 0);
        TopicPartition partition1 = new TopicPartition (TOPIC_NAME, 1);

        consumer.assign (Arrays.asList (partition0, partition1));

        while(true) {
            ConsumerRecords<String, String> records = consumer.poll (100);
            Iterator<ConsumerRecord<String, String>> iterator = records.iterator ();
            while (iterator.hasNext ()) {
                ConsumerRecord<String, String> record = iterator.next ();
                System.out.println ("Receivedmessage: (" + record.key () + "," + record.value () + ") at offset " + record.offset ());
            }
        }
    }
}
