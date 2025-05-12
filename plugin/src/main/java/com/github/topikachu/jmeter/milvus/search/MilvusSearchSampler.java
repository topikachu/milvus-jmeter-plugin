package com.github.topikachu.jmeter.milvus.search;

import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.SearchResp;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;

import java.nio.charset.StandardCharsets;
import java.util.*;


public class MilvusSearchSampler extends AbstractSampler {

    static final String ENDPOINT = "MilvusServerEndpoint";
    static final String TOKEN = "MilvusServerToken";
    static final String USERNAME = "MilvusServerUsername";
    static final String PASSWORD = "MilvusServerPassword";
    static final String COLLECTION_NAME = "collectionName";
    static final String FILTER = "filter";
    static final String OUTPUT_FIELDS = "outputFields";
    static final String TOP_K = "topK";
    static final String QUERY_VECTOR = "queryVector";
    public static final String ENCODING = StandardCharsets.UTF_8.name();

    @Override
    public SampleResult sample(Entry entry) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(getName());
        result.setDataType(SampleResult.TEXT);
        result.setContentType("text/plain"); // $NON-NLS-1$
        result.setDataEncoding(ENCODING);

        // Assume we will be successful
        result.setSuccessful(true);
        result.setResponseMessageOK();
        result.setResponseCodeOK();

        result.sampleStart();
        MilvusClientV2 client = null;
        try {
            String endpoint = getPropertyAsString(ENDPOINT);
            String token = getPropertyAsString(TOKEN, null);
            String username = getPropertyAsString(USERNAME, null);
            String password = getPropertyAsString(PASSWORD, null);

            ConnectConfig.ConnectConfigBuilder<?, ?> builder = ConnectConfig.builder()
                    .uri(endpoint);
            if (token != null && !token.isEmpty()) {
                builder.token(token);
            }
            if (username != null && !username.isEmpty()) {
                builder.username(username);
            }
            if (password != null && !password.isEmpty()) {
                builder.password(password);
            }
            ConnectConfig connectConfig = builder.build();
            client = new MilvusClientV2(connectConfig);

            String vector = getPropertyAsString(QUERY_VECTOR);
            String[] elements = vector.split("\\s*,\\s*");
            double[] doubleVector = Arrays.stream(elements).mapToDouble(Float::parseFloat)
                    .toArray();

            float[] floatVector = new float[doubleVector.length];
            for (int i = 0; i < doubleVector.length; i++) {
                floatVector[i] = (float) doubleVector[i];
            }

            String outputFieldsString = getPropertyAsString(OUTPUT_FIELDS);
            List<String> outputFields = Arrays.asList(outputFieldsString.split("\\s*,\\s*"));
            FloatVec queryVector = new FloatVec(floatVector);
            Map<String, Object> searchParameters = new HashMap();
            SearchReq searchReq = SearchReq.builder()
                    .collectionName(getPropertyAsString(COLLECTION_NAME))
                    .data(Collections.singletonList(queryVector))
                    .topK(getPropertyAsInt(TOP_K))
                    .filter(getPropertyAsString(FILTER))
                    .outputFields(outputFields)
                    .searchParams(searchParameters)
                    .build();
            result.setSamplerData( searchReq.toString());
            SearchResp searchResp = client.search(searchReq);
            List<List<SearchResp.SearchResult>> searchResults = searchResp.getSearchResults();
            StringBuilder sb = new StringBuilder();
            for (List<SearchResp.SearchResult> results : searchResults) {
                for (SearchResp.SearchResult r : results) {
                  sb.append(r.toString()).append("\n");
                }
            }
            searchResults.clear();
            result.setResponseData(sb.toString(), ENCODING);
            result.setResponseMessage("Milvus operation successful");
        } catch (Throwable e) {
            result.setSuccessful(false);
            result.setResponseMessage("Milvus operation failed: " + e.getMessage());
        } finally {
            if (client != null) {
                client.close();
            }
            result.sampleEnd();
        }

        return result;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}