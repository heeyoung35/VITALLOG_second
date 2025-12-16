package com.vitallog.recommend;

import com.vitallog.config.ZillizDBConfig;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.SearchResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@SpringBootApplication
public class SearchVector {
    private ZillizDBConfig zillizDBConfig;
    private VectorizationModel vectorizationModel;

    @Autowired
    public SearchVector(ZillizDBConfig zillizDBConfig, VectorizationModel vectorizationModel) {
        this.zillizDBConfig = zillizDBConfig;
        this.vectorizationModel = vectorizationModel;
    }

    public List<Object> search(String inputText) {
        float[] vector = vectorizationModel.intoVector(inputText);
        System.out.println("vector size: " + vector.length);
        FloatVec queryVector = new FloatVec(vector);

        SearchReq searchReq = SearchReq.builder()
            .collectionName("supplement")
            .data(Collections.singletonList(queryVector))
            .annsField("primary_fncity")
            .topK(5)
            .outputFields(List.of("metadata"))
            .build();

        SearchResp searchResp = zillizDBConfig.milvusClient().search(searchReq);

        List<Object> metadataList = new ArrayList<>();
        List<List<SearchResp.SearchResult>> searchResults = searchResp.getSearchResults();
        for (List<SearchResp.SearchResult> results : searchResults) {
            //System.out.println("TopK results:");
            for (SearchResp.SearchResult result : results) {
                metadataList.add(result.getEntity().get("metadata"));
            }
        }
        return metadataList;
    }
}
