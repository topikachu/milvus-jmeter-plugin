package com.github.topikachu.jmeter.milvus.search;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.vector.request.InsertReq;
import io.milvus.v2.service.vector.response.InsertResp;
import org.apache.jmeter.samplers.SampleResult;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.milvus.MilvusContainer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers(disabledWithoutDocker = true)
class MilvusSearchSamplerTest {
    MilvusContainer milvus = new MilvusContainer("milvusdb/milvus:v2.5.5");

    @Test
    void testSample() {
        milvus.start();


// 1. Connect to Milvus server
        ConnectConfig connectConfig = ConnectConfig.builder()
                .uri(milvus.getEndpoint())
//                .token(TOKEN)
                .build();

        MilvusClientV2 client = new MilvusClientV2(connectConfig);

// 2. Create a collection in quick setup mode
        String collectionName = "quick_setup";
        CreateCollectionReq quickSetupReq = CreateCollectionReq.builder()
                .collectionName(collectionName)
                .dimension(5)
                .build();

        client.createCollection(quickSetupReq);


        Gson gson = new Gson();
        List<JsonObject> data = Arrays.asList(
                gson.fromJson("{\"id\": 0, \"vector\": [0.3580376395471989f, -0.6023495712049978f, 0.18414012509913835f, -0.26286205330961354f, 0.9029438446296592f], \"color\": \"pink_8682\"}", JsonObject.class),
                gson.fromJson("{\"id\": 1, \"vector\": [0.19886812562848388f, 0.06023560599112088f, 0.6976963061752597f, 0.2614474506242501f, 0.838729485096104f], \"color\": \"red_7025\"}", JsonObject.class),
                gson.fromJson("{\"id\": 2, \"vector\": [0.43742130801983836f, -0.5597502546264526f, 0.6457887650909682f, 0.7894058910881185f, 0.20785793220625592f], \"color\": \"orange_6781\"}", JsonObject.class),
                gson.fromJson("{\"id\": 3, \"vector\": [0.3172005263489739f, 0.9719044792798428f, -0.36981146090600725f, -0.4860894583077995f, 0.95791889146345f], \"color\": \"pink_9298\"}", JsonObject.class),
                gson.fromJson("{\"id\": 4, \"vector\": [0.4452349528804562f, -0.8757026943054742f, 0.8220779437047674f, 0.46406290649483184f, 0.30337481143159106f], \"color\": \"red_4794\"}", JsonObject.class),
                gson.fromJson("{\"id\": 5, \"vector\": [0.985825131989184f, -0.8144651566660419f, 0.6299267002202009f, 0.1206906911183383f, -0.1446277761879955f], \"color\": \"yellow_4222\"}", JsonObject.class),
                gson.fromJson("{\"id\": 6, \"vector\": [0.8371977790571115f, -0.015764369584852833f, -0.31062937026679327f, -0.562666951622192f, -0.8984947637863987f], \"color\": \"red_9392\"}", JsonObject.class),
                gson.fromJson("{\"id\": 7, \"vector\": [-0.33445148015177995f, -0.2567135004164067f, 0.8987539745369246f, 0.9402995886420709f, 0.5378064918413052f], \"color\": \"grey_8510\"}", JsonObject.class),
                gson.fromJson("{\"id\": 8, \"vector\": [0.39524717779832685f, 0.4000257286739164f, -0.5890507376891594f, -0.8650502298996872f, -0.6140360785406336f], \"color\": \"white_9381\"}", JsonObject.class),
                gson.fromJson("{\"id\": 9, \"vector\": [0.5718280481994695f, 0.24070317428066512f, -0.3737913482606834f, -0.06726932177492717f, -0.6980531615588608f], \"color\": \"purple_4976\"}", JsonObject.class)
        );

        InsertReq insertReq = InsertReq.builder()
                .collectionName(collectionName)
                .data(data)
                .build();

        InsertResp insertResp = client.insert(insertReq);

        MilvusSearchSampler sampler = new MilvusSearchSampler();
        sampler.setProperty(MilvusSearchSampler.ENDPOINT, milvus.getEndpoint());
        sampler.setProperty(MilvusSearchSampler.COLLECTION_NAME, collectionName);
        sampler.setProperty(MilvusSearchSampler.FILTER, "color like \"red%\" ");
        sampler.setProperty(MilvusSearchSampler.TOP_K, 5);
        sampler.setProperty(MilvusSearchSampler.OUTPUT_FIELDS, "color,likes");

        String queryVector = Arrays.stream(new double[]{0.3580376395471989, -0.6023495712049978, 0.18414012509913835, -0.26286205330961354, 0.9029438446296592})
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(","));

        sampler.setProperty(MilvusSearchSampler.QUERY_VECTOR, queryVector);


        SampleResult sampleResult = sampler.sample(null);

        assertThat(sampleResult.isSuccessful()).isTrue();
        milvus.stop();

    }
}