# kafka-streams

## Streams
A stream is an unbounded, continuously updating data set. With the Kafka Streams DSL users can define the processor topology
by concatenating multiple transformation operations where each operation transforming one stream into other stream(s);
the resulted topology then takes the input streams from source Kafka topics and generates the final output streams
throughout its concatenated transformations.

However, different streams may have different semantics in their data records:

- In some streams, each record represents __a new immutable datum__ in their unbounded data set; we call these [record streams](http://docs.confluent.io/3.1.2/streams/concepts.html#streams-concepts-kstream).
- In other streams, each record represents __a revision (or update)__ of their unbounded data set in chronological order; we call these [changelog streams](http://docs.confluent.io/3.1.2/streams/concepts.html#streams-concepts-ktable).

## Aggregations
An aggregation operation takes one input stream, and yields a new stream by combining multiple input records into a single output record.
Examples of aggregations are computing counts or sum. An aggregation over record streams usually needs to be performed on a
__windowing__ basis because otherwise the number of records that must be maintained for performing the aggregation may grow indefinitely.

In the Kafka Streams DSL, an input stream of an aggregation can be a [KStream]() or a [KTable](), but the output stream
__will always be a KTable__. This allows Kafka Streams to update an aggregate value upon the late arrival of further records
after the value was produced and emitted. When such late arrival happens, the aggregating KStream or KTable simply emits a
new aggregate value. Because the output is a KTable, the new value is considered to overwrite the old value with the
same key in subsequent processing steps.

